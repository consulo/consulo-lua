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
package com.sylvanaar.idea.lua.editor.inspections.performance;

import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.lua.editor.inspections.utils.ControlFlowUtils;
import com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaBinaryExpression;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaAssignmentStatement;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.ast.IElementType;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;

public class StringConcatenationInLoopsInspection extends AbstractInspection {

    /**
     * @noinspection PublicField
     */
    public boolean m_ignoreUnlessAssigned = true;

    @Override
    @Nonnull
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO("String concatenation in a loop");
    }

    //    @Override
    @Nonnull
    protected String buildErrorString(Object... infos) {
        return  "String concatenation in loop";
    }

    @Nonnull
    @Override
    public LocalizeValue getGroupDisplayName() {
        return PERFORMANCE_ISSUES;
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Nonnull
    @Override
    public LuaElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {

            @Override
            public void visitBinaryExpression(
                    LuaBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                if (expression.getRightOperand() == null) {
                    return;
                }
                final LuaPsiElement sign = expression.getOperator();
                final IElementType tokenType = sign.getNode().getFirstChildNode().getElementType();
                if (!tokenType.equals(LuaTokenTypes.CONCAT)) {
                    return;
                }
                if (!ControlFlowUtils.isInLoop(expression)) {
                    return;
                }

                PsiElement e = expression.getParent().getParent();
                if (!(e instanceof LuaAssignmentStatement))
                    return;

                LuaIdentifierList lvalues = ((LuaAssignmentStatement) e).getLeftExprs();

                if (lvalues == null || lvalues.count() != 1)
                    return;

                LuaSymbol id = lvalues.getSymbols()[0];

                if (!id.getText().equals(expression.getLeftOperand().getText()))
                    return;

                holder.registerProblem(expression, buildErrorString(), LocalQuickFix.EMPTY_ARRAY);
            }
        };
    }
}
