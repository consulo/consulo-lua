/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
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
package com.sylvanaar.idea.lua.editor.inspections.validity;

import javax.annotation.Nonnull;

import consulo.language.editor.inspection.ProblemsHolder;
import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.lua.editor.inspections.utils.ControlFlowUtils;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaStatementElement;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiFile;
import org.jetbrains.annotations.Nls;

import javax.annotation.Nullable;


public class LuaUnreachableStatementInspection extends AbstractInspection {

    @Nls
    @Nonnull
    public String getGroupDisplayName() {
        return VALIDITY_ISSUES;
    }

    @Nls
    @Nonnull
    public String getDisplayName() {
        return "Unreachable Statement";
    }

    @Nullable
    protected String buildErrorString(Object... args) {
        return "Unreachable statement #loc";

    }

    public boolean isEnabledByDefault() {
        return true;
    }

    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {
            public void visitFile(PsiFile file) {
                if (!(file instanceof LuaPsiFile)) return;

                LuaStatementElement[] statements = ((LuaPsiFile) file).getStatements();
                for (int i = 0; i < statements.length - 1; i++) {
                    checkPair(statements[i], statements[i + 1]);
                }
            }

            private void checkPair(LuaStatementElement prev, LuaStatementElement statement) {
                if (!ControlFlowUtils.statementMayCompleteNormally(prev)) {
                    holder.registerProblem(statement,
                            buildErrorString(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }

            }
        };
    }
}
