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
package com.sylvanaar.idea.lua.editor.inspections.unassignedVariable;

import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.lua.lang.psi.LuaControlFlowOwner;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.controlFlow.ControlFlowUtil;
import com.sylvanaar.idea.lua.lang.psi.controlFlow.Instruction;
import com.sylvanaar.idea.lua.lang.psi.controlFlow.ReadWriteVariableInstruction;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.inspection.UnfairLocalInspectionTool;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiFile;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nls;

/**
 * @author ven
 */
public class UnassignedVariableAccessInspection extends AbstractInspection  implements UnfairLocalInspectionTool
{
    @Nls
    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO("Variable not assigned");
    }

    @Override
    public String getStaticDescription() {
        return "Variable is read from without being assigned to.";
    }

    @Nonnull
    @Override
    public LocalizeValue getGroupDisplayName() {
        return PROBABLE_BUGS;
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {
            //            @Override
            //            public void visitBlock(LuaBlock e) {
            //                super.visitBlock(e);
            //
            //                check(e, holder);
            //            }

            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
                if (! (file instanceof LuaPsiFile))
                    return;

                check((LuaControlFlowOwner) file, holder);
            }
        };
    }


    protected void check(LuaControlFlowOwner owner, ProblemsHolder problemsHolder) {
        try {
            Instruction[] flow = owner.getControlFlow();
            if (flow == null) return;
            ReadWriteVariableInstruction[] reads = ControlFlowUtil.getReadsWithoutPriorWrites(flow);
            for (ReadWriteVariableInstruction read : reads) {
                PsiElement element = read.getElement();
                if (element instanceof LuaReferenceElement) {
                    if (((LuaReferenceElement) element).getElement() instanceof LuaGlobal)
                        if (((LuaReferenceElement) element).multiResolve(false).length == 0) {

                            if (element.getTextLength() > 0)
                                problemsHolder.registerProblem(element, "Unassigned variable usage",
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
