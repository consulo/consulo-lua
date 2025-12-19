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

package com.sylvanaar.idea.lua.lang.psi.impl.lists;

import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.impl.expressions.LuaExpressionImpl;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.types.LuaList;
import com.sylvanaar.idea.lua.lang.psi.types.LuaPrimitiveType;
import com.sylvanaar.idea.lua.lang.psi.types.LuaType;
import consulo.language.ast.ASTNode;
import consulo.language.codeStyle.CodeStyleManager;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.List;

import static com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes.COMMA;


/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 13, 2010
 * Time: 7:18:16 AM
 */
public class LuaExpressionListImpl extends LuaExpressionImpl implements LuaExpressionList {
    public LuaExpressionListImpl(ASTNode node) {
        super(node);
    }

    @Override
    public int count() {
        return getLuaExpressions().size();
    }

    public List<LuaExpression> getLuaExpressions() {
        return Arrays.asList(findChildrenByClass(LuaExpression.class));
    }

    public String toString() {
        return "Expression List (Count " + count() + ")";
    }

    @Nonnull
    @Override
    public LuaType getLuaType() {
        final List<LuaExpression> expressions = getLuaExpressions();
        LuaType[] types = new LuaType[expressions.size()];

        for (int i = 0, expressionsSize = expressions.size(); i < expressionsSize; i++) {
            LuaExpression expression = expressions.get(i);
            
            if (expression != null) {
                if (expression instanceof LuaReferenceElement)
                    expression = (LuaExpression) ((LuaReferenceElement) expression).resolve();

                types[i] = expression == null ? LuaPrimitiveType.ANY : expression.getLuaType();
            }
        }

        if (types.length == 1)
            return types[0];

        return new LuaList(types);
    }

    @Override
    public PsiElement addAfter(@Nonnull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        List<LuaExpression> exprs = getLuaExpressions();

        if (exprs.size() == 0) {
            add(element);
        } else {
            element = super.addAfter(element, anchor);
            final ASTNode astNode = getNode();
            if (anchor != null) {
                astNode.addLeaf(COMMA, ",", element.getNode());
            } else {
                astNode.addLeaf(COMMA, ",", element.getNextSibling().getNode());
            }
            CodeStyleManager.getInstance(getManager().getProject()).reformat(this);
        }

        return element;
    }

    @Override
    public PsiElement addBefore(@Nonnull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        List<LuaExpression> exprs = getLuaExpressions();

        if (exprs.size() == 0) {
            add(element);
        } else {
            element = super.addBefore(element, anchor);
            final ASTNode astNode = getNode();
            if (anchor != null) {
                astNode.addLeaf(COMMA, ",", anchor.getNode());
            } else {
                astNode.addLeaf(COMMA, ",", element.getNode());
            }
            CodeStyleManager.getInstance(getManager().getProject()).reformat(this);
        }

        return element;
    }


}
