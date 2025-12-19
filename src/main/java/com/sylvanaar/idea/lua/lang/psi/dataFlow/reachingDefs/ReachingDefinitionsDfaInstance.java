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
package com.sylvanaar.idea.lua.lang.psi.dataFlow.reachingDefs;

import com.sylvanaar.idea.lua.lang.psi.controlFlow.Instruction;
import com.sylvanaar.idea.lua.lang.psi.controlFlow.ReadWriteVariableInstruction;
import com.sylvanaar.idea.lua.lang.psi.dataFlow.DfaInstance;
import consulo.util.collection.primitive.ints.IntMaps;
import consulo.util.collection.primitive.ints.IntObjectMap;
import consulo.util.collection.primitive.ints.IntSet;
import consulo.util.collection.primitive.ints.IntSets;
import consulo.util.collection.primitive.objects.ObjectIntMap;
import consulo.util.collection.primitive.objects.ObjectMaps;
import jakarta.annotation.Nonnull;

/**
 * @author ven
 */
public class ReachingDefinitionsDfaInstance implements DfaInstance<IntObjectMap<IntSet>> {
  private final ObjectIntMap<String> myVarToIndexMap = ObjectMaps.newObjectIntHashMap();

  public int getVarIndex(String varName) {
    return myVarToIndexMap.getInt(varName);
  }

  public ReachingDefinitionsDfaInstance(Instruction[] flow) {
    int num = 0;
    for (Instruction instruction : flow) {
      if (instruction instanceof ReadWriteVariableInstruction) {
        final String name = ((ReadWriteVariableInstruction) instruction).getVariableName();
        if (!myVarToIndexMap.containsKey(name)) {
          myVarToIndexMap.putInt(name, num++);
        }
      }
    }
  }


  public void fun(IntObjectMap<IntSet> m, Instruction instruction) {
    if (instruction instanceof ReadWriteVariableInstruction) {
      final ReadWriteVariableInstruction varInsn = (ReadWriteVariableInstruction) instruction;
      final String name = varInsn.getVariableName();
      if (name == null) return;
      assert myVarToIndexMap.containsKey(name);
      final int num = myVarToIndexMap.getInt(name);
      if (varInsn.isWrite()) {
        IntSet defs = m.get(num);
        if (defs == null) {
          defs = IntSets.newHashSet();
          m.put(num, defs);
        } else defs.clear();
        defs.add(varInsn.num());
      }
    }
  }

  @Nonnull
  public IntObjectMap<IntSet> initial() {
    return IntMaps.newIntObjectHashMap();
  }

  public boolean isForward() {
    return true;
  }
}
