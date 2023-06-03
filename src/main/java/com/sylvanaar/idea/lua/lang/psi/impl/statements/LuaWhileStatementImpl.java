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

import consulo.language.psi.PsiElementVisitor;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiToken;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaConditionalExpression;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaBlock;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaWhileStatement;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.ast.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class LuaWhileStatementImpl extends LuaStatementElementImpl implements LuaWhileStatement {

    public LuaWhileStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitWhileStatement(this);
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitWhileStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public LuaConditionalExpression getCondition() {
        return findChildByClass(LuaConditionalExpression.class);
    }

    @Override
    public LuaPsiToken getLParenth() {
        return null;
    }

    @Override
    public LuaPsiToken getRParenth() {
        return null;
    }

    @Override
    public LuaBlock getBlock() {
        return findChildByClass(LuaBlock.class);
    }


}
