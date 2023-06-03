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
package com.sylvanaar.idea.Lua.editor.inspections.usage;

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.inspection.UnfairLocalInspectionTool;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import consulo.logging.Logger;
import consulo.application.progress.ProgressIndicatorProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import com.sylvanaar.idea.Lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.Lua.lang.psi.LuaControlFlowOwner;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.Lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.Lua.lang.psi.controlFlow.Instruction;
import com.sylvanaar.idea.Lua.lang.psi.controlFlow.ReadWriteVariableInstruction;
import com.sylvanaar.idea.Lua.lang.psi.dataFlow.DFAEngine;
import com.sylvanaar.idea.Lua.lang.psi.dataFlow.reachingDefs.ReachingDefinitionsDfaInstance;
import com.sylvanaar.idea.Lua.lang.psi.dataFlow.reachingDefs.ReachingDefinitionsSemilattice;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaAssignmentStatement;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaLocal;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaParameter;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.util.collection.primitive.ints.IntObjConsumer;
import consulo.util.collection.primitive.ints.IntObjectMap;
import consulo.util.collection.primitive.ints.IntSet;
import consulo.util.collection.primitive.ints.IntSets;
import org.jetbrains.annotations.Nls;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.IntConsumer;

/**
 & @author ven
 */
public class UnusedDefInspection extends AbstractInspection implements UnfairLocalInspectionTool {
  private static final Logger log = Logger.getInstance("Lua.UnusedDefInspection");


    @Override
    public String getStaticDescription() {
        return "Variable is not used";
    }

    @Override
    @Nls
    @Nonnull
    public String getGroupDisplayName() {
        return DATA_FLOW;
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Override
    @Nls
    @Nonnull
    public String getDisplayName() {
        return "Unused Assignment";
    }

    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {
//            public void visitFunctionDef(LuaFunctionDefinitionStatement e) {
//                super.visitFunctionDef(e);
//                LuaBlock block = e.getBlock();
//                if (block != null)
//                    check(block, holder);
//            }
//
//            @Override
//            public void visitAnonymousFunction(LuaAnonymousFunctionExpression e) {
//                super.visitAnonymousFunction(e);
//                LuaBlock block = e.getBlock();
//                if (block != null)
//                    check(block, holder);
//            }
            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);

                if (file instanceof LuaPsiFile) try {
                    check((LuaControlFlowOwner) file, holder);
                } catch (Exception ignored) {
                    log.debug(ignored);
                }
            }
        };
    }    


  protected static void check(final LuaControlFlowOwner owner, final ProblemsHolder problemsHolder) {
    final Instruction[] flow = owner.getControlFlow();
    if (flow == null) return;
    final ReachingDefinitionsDfaInstance dfaInstance = new ReachingDefinitionsDfaInstance(flow);
    final ReachingDefinitionsSemilattice lattice = new ReachingDefinitionsSemilattice();
    final DFAEngine<IntObjectMap<IntSet>> engine = new DFAEngine<>(flow, dfaInstance, lattice);
    final ArrayList<IntObjectMap<IntSet>> dfaResult = engine.performDFA();
    final IntSet unusedDefs = IntSets.newHashSet();
    for (Instruction instruction : flow) {
      if (instruction instanceof ReadWriteVariableInstruction && ((ReadWriteVariableInstruction) instruction).isWrite()) {
          if (!((ReadWriteVariableInstruction) instruction).getVariableName().equals("_"))
            unusedDefs.add(instruction.num());
      }
    }

    ProgressIndicatorProvider.checkCanceled();

    for (int i = 0; i < dfaResult.size(); i++) {
      final Instruction instruction = flow[i];
      if (instruction instanceof ReadWriteVariableInstruction) {
        final ReadWriteVariableInstruction varInsn = (ReadWriteVariableInstruction) instruction;
        if (!varInsn.isWrite()) {
          final String varName = varInsn.getVariableName();
          IntObjectMap<IntSet> e = dfaResult.get(i);
          e.forEach(new IntObjConsumer<IntSet>() {
            @Override
            public void accept(int key, IntSet reaching) {
              reaching.forEach(new IntConsumer() {
                @Override
                public void accept(int defNum) {
                  final String defName = ((ReadWriteVariableInstruction) flow[defNum]).getVariableName();
                  if (varName != null && varName.equals(defName)) {
                    unusedDefs.remove(defNum);
                  }
                }
              });
            }
          });
        }
      }
    }

    ProgressIndicatorProvider.checkCanceled();

      unusedDefs.forEach(new IntConsumer() {
          @Override
          public void accept(int num) {
              final ReadWriteVariableInstruction instruction = (ReadWriteVariableInstruction) flow[num];
              final PsiElement element = instruction.getElement();
              if (element == null) return;
              if (isLocalAssignment(element)) {
                  PsiElement toHighlight = null;
                  if (element instanceof LuaReferenceElement) {
                      PsiElement parent = element.getParent();
                      if (parent instanceof LuaAssignmentStatement) {
                          toHighlight = ((LuaAssignmentStatement) parent).getLeftExprs();
                      }
                  } else if (element instanceof LuaSymbol) {
                      toHighlight = ((LuaSymbol) element);//.getNamedElement();
                  }
                  if (toHighlight == null) toHighlight = element;

                  if (toHighlight.getTextLength() > 0)
                  problemsHolder
                          .registerProblem(toHighlight, "Unused Assignment", ProblemHighlightType.LIKE_UNUSED_SYMBOL);
              }
          }
      });
  }

//  private boolean isUsedInToplevelFlowOnly(PsiElement element) {
//    LuaSymbol var = null;
//    if (element instanceof LuaSymbol) {
//      var = (LuaSymbol) element;
//    } else if (element instanceof LuaReferenceElement) {
//      final PsiElement resolved = ((LuaReferenceElement) element).resolve();
//      if (resolved instanceof LuaSymbol) var = (LuaSymbol) resolved;
//    }
//
//    if (var != null) {
//      final LuaPsiElement scope = getScope(var);
//      if (scope == null) {
//        PsiFile file = var.getContainingFile();
//        log.error(file == null ? "no file???" : DebugUtil.psiToString(file, true, false));
//      }
//
//      return ReferencesSearch.search(var, new LocalSearchScope(scope)).forEach(new Processor<PsiReference>() {
//        public boolean process(PsiReference ref) {
//          return getScope(ref.getElement()) == scope;
//        }
//      });
//    }
//
//    return true;
//  }

//  private LuaPsiElement getScope(PsiElement var) {
//    return PsiTreeUtil.getParentOfType(var, LuaBlock.class, LuaPsiFile.class);
//  }

  private static boolean isLocalAssignment(PsiElement element) {
    if (element instanceof LuaSymbol) {
      return isLocalVariable((LuaSymbol) element, false);
    } else if (element instanceof LuaReferenceElement) {
      final PsiElement resolved = ((PsiReference) element).resolve();
      return resolved instanceof LuaSymbol && isLocalVariable((LuaSymbol) resolved, true);
    }

    return false;
  }

  private static boolean isLocalVariable(LuaSymbol var, boolean parametersAllowed) {
    if (var instanceof LuaLocal) return true;
    else if (var instanceof LuaParameter && !parametersAllowed) return false;

    return false;
  }
}
