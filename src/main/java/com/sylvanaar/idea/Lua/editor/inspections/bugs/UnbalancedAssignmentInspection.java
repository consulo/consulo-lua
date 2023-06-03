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

package com.sylvanaar.idea.lua.editor.inspections.bugs;

import javax.annotation.Nonnull;

import com.sylvanaar.idea.lua.editor.inspections.*;
import com.sylvanaar.idea.lua.editor.inspections.utils.*;
import com.sylvanaar.idea.lua.lang.psi.*;
import com.sylvanaar.idea.lua.lang.psi.expressions.*;
import com.sylvanaar.idea.lua.lang.psi.lists.*;
import com.sylvanaar.idea.lua.lang.psi.statements.*;
import com.sylvanaar.idea.lua.lang.psi.symbols.*;
import com.sylvanaar.idea.lua.lang.psi.visitor.*;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import org.jetbrains.annotations.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 13, 2010
 * Time: 7:10:29 AM
 */
public class UnbalancedAssignmentInspection extends AbstractInspection {
    @Nls
    @Nonnull
    @Override
    public String getDisplayName() {
        return "Unbalanced Assignment";
    }

    @Nonnull
    @Override
    public String getGroupDisplayName() {
        return PROBABLE_BUGS;
    }

    @Override
    public String getStaticDescription() {
        return "Looks for unbalanced assignment statements where the number of identifiers on the left could be " +
               "different than the number of expressions on the right.";
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {
            public void visitAssignment(LuaAssignmentStatement assign) {
                super.visitAssignment(assign);
                if (assign instanceof LuaLocalDefinitionStatement) {
                    LuaIdentifierList left = ((LuaLocalDefinitionStatement) assign).getLeftExprs();
                    LuaExpressionList right = ((LuaLocalDefinitionStatement) assign).getRightExprs();

                    if (right == null)
                        return;

                    if (ExpressionUtils.onlyNilExpressions(right))
                        return;

                    if (right.count() > 0)
                        checkAssignment(assign, left, right, holder);
                } else {
                    LuaIdentifierList left = assign.getLeftExprs();
                    LuaExpressionList right = assign.getRightExprs();
                    checkAssignment(assign, left, right, holder);
                }

            }

            @Override
            public void visitDeclarationStatement(LuaDeclarationStatement e) {
                super.visitDeclarationStatement(e);


            }
        };
    }

    private void checkAssignment(PsiElement element,
								 LuaIdentifierList left,
								 LuaExpressionList right,
								 ProblemsHolder holder) {
        if (left != null && right != null && left.count() != right.count()) {

            boolean tooManyExprs = left.count() < right.count();
            boolean ignore = false;

            int exprcount = right.getLuaExpressions().size();
            ignore = exprcount == 0;

            PsiElement expr = null;

            if (!ignore) {
                LuaExpression last = right.getLuaExpressions().get(exprcount - 1);

                expr = last;

                if (expr instanceof LuaCompoundIdentifier)
                    expr = ((LuaCompoundIdentifier) expr).getScopeIdentifier();
            }

            if (expr != null)
                ignore = (expr.getText()).equals("...");
            else
                ignore = true;

            if (!ignore && expr instanceof LuaFunctionCallExpression)
                ignore = true;

            if (!ignore) {
                LocalQuickFix[] fixes = {new UnbalancedAssignmentFix(tooManyExprs)};
                holder.registerProblem(element, "Unbalanced number of expressions in assignment", fixes);
            }
        }
    }


    private class UnbalancedAssignmentFix extends LuaFix {
        boolean tooManyExprs;

        public UnbalancedAssignmentFix(boolean tooManyExprs) {
            this.tooManyExprs = tooManyExprs;
        }


        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) throws IncorrectOperationException
		{
            final LuaAssignmentStatement assign = (LuaAssignmentStatement) descriptor.getPsiElement();
            final LuaIdentifierList identifierList = assign.getLeftExprs();
            final LuaExpressionList expressionList = assign.getRightExprs();
            final PsiElement lastExpr = expressionList.getLastChild();
            final int leftCount = identifierList.count();
            final int rightCount = expressionList.count();

            if (tooManyExprs) {
                for (int i = rightCount - leftCount; i > 0; i--) {
                    identifierList.addAfter(LuaPsiElementFactory.getInstance(project).createExpressionFromText("_"), lastExpr);
                }
            } else {
                for (int i = leftCount - rightCount; i > 0; i--) {
                    expressionList.addAfter(LuaPsiElementFactory.getInstance(project).createExpressionFromText("nil"), lastExpr);
                }
            }
        }

        @Nonnull
        @Override
        public String getName() {
            if (tooManyExprs)
                return "Balance by adding '_' identifiers on the left";
            else
                return "Balance by adding nil's on the right";
        }
    }

}
