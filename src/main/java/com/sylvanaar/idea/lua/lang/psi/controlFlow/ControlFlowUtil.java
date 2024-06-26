/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sylvanaar.idea.lua.lang.psi.controlFlow;

import consulo.logging.Logger;
import consulo.util.collection.primitive.ints.IntLists;
import consulo.util.collection.primitive.ints.IntSet;
import consulo.util.collection.primitive.ints.IntSets;
import consulo.util.collection.primitive.objects.ObjectIntMap;
import consulo.util.collection.primitive.objects.ObjectMaps;

import java.util.ArrayList;
import java.util.List;

public class ControlFlowUtil {
    private static final Logger LOG = Logger.getInstance("Lua.ControlFlowUtil");

    public static int[] postorder(Instruction[] flow) {
        int[] result = new int[flow.length];
        boolean[] visited = new boolean[flow.length];
        for (int i = 0; i < result.length; i++)
            visited[i] = false;

        int N = flow.length;
        for (int i = 0; i < flow.length; i++) { //graph might not be connected
            if (!visited[i])
                N = doVisitForPostorder(flow[i], N, result, visited);
        }

        LOG.assertTrue(N == 0);
        return result;
    }

    private static int doVisitForPostorder(Instruction curr, int currN, int[] postorder, boolean[] visited) {
        visited[curr.num()] = true;
        for (Instruction succ : curr.allSucc()) {
            if (!visited[succ.num()]) {
                currN = doVisitForPostorder(succ, currN, postorder, visited);
            }
        }
        postorder[curr.num()] = --currN;
        return currN;
    }

    public static ReadWriteVariableInstruction[] getReadsWithoutPriorWrites(Instruction[] flow) {
        List<ReadWriteVariableInstruction> result = new ArrayList<ReadWriteVariableInstruction>();
        ObjectIntMap<String> namesIndex = buildNamesIndex(flow);

        IntSet[] definitelyAssigned = new IntSet[flow.length];

        int[] postorder = postorder(flow);
        int[] invpostorder = invPostorder(postorder);

        findReadsBeforeWrites(flow, definitelyAssigned, result, namesIndex, postorder, invpostorder);

        return result.toArray(new ReadWriteVariableInstruction[result.size()]);
    }

    private static int[] invPostorder(int[] postorder) {
        int[] result = new int[postorder.length];
        for (int i = 0; i < postorder.length; i++) {
            result[postorder[i]] = i;
        }

        return result;
    }

    private static ObjectIntMap<String> buildNamesIndex(Instruction[] flow) {
        ObjectIntMap<String> namesIndex = ObjectMaps.newObjectIntHashMap();
        int idx = 0;
        for (Instruction instruction : flow) {
            if (instruction instanceof ReadWriteVariableInstruction) {
                String name = ((ReadWriteVariableInstruction) instruction).getVariableName();
                if (!namesIndex.containsKey(name)) {
                    namesIndex.putInt(name, idx++);
                }
            }
        }
        return namesIndex;
    }

    private static void findReadsBeforeWrites(Instruction[] flow,
                                              IntSet[] definitelyAssigned,
                                              List<ReadWriteVariableInstruction> result,
                                              ObjectIntMap<String> namesIndex,
                                              int[] postorder,
                                              int[] invpostorder) {
        //skip instructions that are not reachable from the start
        int start = 0;
        while (invpostorder[start] != 0)
            start++;

        for (int i = start; i < flow.length; i++) {
            int j = invpostorder[i];
            Instruction curr = flow[j];
            if (curr instanceof ReadWriteVariableInstruction) {
                ReadWriteVariableInstruction readWriteInsn = (ReadWriteVariableInstruction) curr;
                int idx = namesIndex.getInt(readWriteInsn.getVariableName());
                IntSet vars = definitelyAssigned[j];
                if (readWriteInsn.isGlobal()) {
                    if (!readWriteInsn.isWrite()) {
                        if (vars == null || !vars.contains(idx)) {
                            result.add(readWriteInsn);
                        }
                    } else {
                        if (vars == null) {
                            vars = IntSets.newHashSet();
                            definitelyAssigned[j] = vars;
                        }
                        vars.add(idx);
                    }
                }
            }

            for (Instruction succ : curr.allSucc()) {
                if (postorder[succ.num()] > postorder[curr.num()]) {
                    IntSet currDefinitelyAssigned = definitelyAssigned[curr.num()];
                    IntSet succDefinitelyAssigned = definitelyAssigned[succ.num()];
                    if (currDefinitelyAssigned != null) {
                        int[] currArray = currDefinitelyAssigned.toArray();
                        if (succDefinitelyAssigned == null) {
                            succDefinitelyAssigned = IntSets.newHashSet();
                            succDefinitelyAssigned.addAll(currArray);
                            definitelyAssigned[succ.num()] = succDefinitelyAssigned;
                        } else {
                            succDefinitelyAssigned.retainAll(IntLists.newArrayList(currArray));
                        }
                    } else {
                        if (succDefinitelyAssigned != null) {
                            succDefinitelyAssigned.clear();
                        } else {
                            succDefinitelyAssigned = IntSets.newHashSet();
                            definitelyAssigned[succ.num()] = succDefinitelyAssigned;
                        }
                    }
                }
            }

        }
    }

}
