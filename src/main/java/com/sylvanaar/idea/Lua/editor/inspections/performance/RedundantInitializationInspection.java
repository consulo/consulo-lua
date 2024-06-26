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

package com.sylvanaar.idea.lua.editor.inspections.performance;

import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemsHolder;
import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.lua.editor.inspections.LuaFix;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaAssignmentStatement;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaDeclarationStatement;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaLocalDefinitionStatement;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import org.jetbrains.annotations.Nls;
import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/17/11
 * Time: 12:53 AM
 */
public class RedundantInitializationInspection extends AbstractInspection {

    @Nls
    @Nonnull
    @Override
    public String getDisplayName() {
        return "Redundant Initialization";
    }


    @Nonnull
    @Override
    public String getGroupDisplayName() {
        return PERFORMANCE_ISSUES;
    }

    @Override
    public String getStaticDescription() {
        return "Looks for unnecessary initialization of local variables";
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WEAK_WARNING;
    }


    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {
            @Override
            public void visitDeclarationStatement(LuaDeclarationStatement e) {
                super.visitDeclarationStatement(e);

                if (e instanceof LuaLocalDefinitionStatement) {
                    LuaIdentifierList left = ((LuaLocalDefinitionStatement) e).getLeftExprs();
                    LuaExpressionList right = ((LuaLocalDefinitionStatement) e).getRightExprs();

                    if (right == null || right.count() == 0)
                        return;

                    boolean allNil = true;
                    for (LuaExpression expr : right.getLuaExpressions())
                        if (!expr.getText().equals("nil")) {
                            allNil = false;
                            break;
                        }

                    if (allNil) {
                        LocalQuickFix[] fixes = {new RedundantInitializationFix()};
                        holder.registerProblem(e, "Redundant Initialization", fixes);
                    }
                }
            }
        };
    }

    private class RedundantInitializationFix extends LuaFix {

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) throws IncorrectOperationException
		{
            final LuaAssignmentStatement assign = (LuaAssignmentStatement) descriptor.getPsiElement();

            assign.getOperatorElement().delete();
            assign.getRightExprs().delete();
        }

        @Nonnull
        @Override
        public String getName() {
            return "Remove unnecessary initialization";
        }
    }
}
