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

package com.sylvanaar.idea.lua.lang.psi.impl.statements;

import javax.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.resolve.PsiScopeProcessor;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaFunctionCallExpression;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaFunctionCallStatement;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;

//import com.sylvanaar.idea.Lua.lang.psi.expressions.LuaFunctionIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class LuaFunctionCallStatementImpl extends LuaStatementElementImpl implements LuaFunctionCallStatement {

    public LuaFunctionCallStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public LuaFunctionCallExpression getInvokedExpression() {
        return findChildByClass(LuaFunctionCallExpression.class);
    }

    @Override
    public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state,
									   PsiElement lastParent, @Nonnull PsiElement place) {
        return getInvokedExpression().processDeclarations(processor, state, lastParent, place);
    }

    @Override
    public LuaExpressionList getArgumentList() {
        return getInvokedExpression().getArgumentList();
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitFunctionCallStatement(this);
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitFunctionCallStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public String toString() {
        LuaFunctionCallExpression e = getInvokedExpression();

        return "Stmt: " + e.toString();
    }
}
