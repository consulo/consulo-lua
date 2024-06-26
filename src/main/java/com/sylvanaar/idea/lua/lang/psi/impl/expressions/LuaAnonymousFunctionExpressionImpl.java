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
package com.sylvanaar.idea.lua.lang.psi.impl.expressions;

import static com.sylvanaar.idea.lua.lang.parser.LuaElementTypes.BLOCK;
import static com.sylvanaar.idea.lua.lang.parser.LuaElementTypes.PARAMETER_LIST;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.document.util.TextRange;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.navigation.ItemPresentation;
import consulo.language.psi.PsiElement;
import com.sylvanaar.idea.lua.LuaIcons;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.lua.lang.luadoc.psi.impl.LuaDocCommentUtil;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaAnonymousFunctionExpression;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaParameterList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaAssignmentStatement;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaBlock;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaLocalDefinitionStatement;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaParameter;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.types.LuaFunction;
import com.sylvanaar.idea.lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import com.sylvanaar.idea.lua.util.LuaAtomicNotNullLazyValue;
import consulo.language.ast.ASTNode;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 4, 2010
 * Time: 7:44:04 AM
 */
public class LuaAnonymousFunctionExpressionImpl extends LuaExpressionImpl
    implements LuaAnonymousFunctionExpression {
    LuaAtomicNotNullLazyValue<LuaFunction> myLazyType = new LuaAtomicNotNullLazyValue<LuaFunction>() {
            @Nonnull
            @Override
            protected LuaFunction compute() {
                LuaFunction type = new LuaFunction();
                acceptLuaChildren(LuaAnonymousFunctionExpressionImpl.this,
                    new LuaPsiUtils.LuaBlockReturnVisitor(type));

                return type;
            }
        };

    public LuaAnonymousFunctionExpressionImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitAnonymousFunction(this);
    }

    @Override
    public void accept(@Nonnull
							   PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitAnonymousFunction(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public LuaParameterList getParameters() {
        return (LuaParameterList) findChildByType(PARAMETER_LIST);
    }

    @Override
    public LuaBlock getBlock() {
        return (LuaBlock) findChildByType(BLOCK);
    }

    @Override
    public TextRange getRangeEnclosingBlock() {
        final PsiElement rparen = findChildByType(LuaElementTypes.RPAREN);

        if (rparen == null) {
            return getTextRange();
        }

        return TextRange.create(rparen.getTextOffset() + 1,
            getTextRange().getEndOffset());
    }

    @Override
    public ItemPresentation getPresentation() {
        return LuaPsiUtils.getFunctionPresentation(this);
    }

    @Override
    public String getPresentableText() {
        LuaSymbol id = getIdentifier();

        return (id != null) ? id.getText() : null;
    }

    @Override
    public String getLocationString() {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Image getIcon() {
        return LuaIcons.LUA_FUNCTION;
    }

    @Override
    public String getPresentationText() {
        return getPresentableText();
    }

    public boolean processDeclarations(@Nonnull
											   PsiScopeProcessor processor, @Nonnull
											   ResolveState resolveState, PsiElement lastParent, @Nonnull
    PsiElement place) {
        if ((lastParent != null) && (lastParent.getParent() == this)) {
            final LuaParameter[] params = getParameters().getLuaParameters();

            for (LuaParameter param : params) {
                if (!processor.execute(param, resolveState)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public LuaFunction getLuaType() {
        return myLazyType.getValue();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        myLazyType.drop();
    }

    @Override
    public String getName() {
        LuaSymbol id = getIdentifier();

        return (id != null) ? id.getName() : null;
    }

    @Override
    public LuaSymbol getIdentifier() {
        LuaExpressionList exprlist = PsiTreeUtil.getParentOfType(this,
                LuaExpressionList.class);

        if (exprlist == null) {
            return null;
        }

        int idx = exprlist.getLuaExpressions().indexOf(this);

        if (idx < 0) {
            return null;
        }

        PsiElement assignment = exprlist.getParent();

        LuaIdentifierList idlist = null;

        if (assignment instanceof LuaAssignmentStatement) {
            idlist = ((LuaAssignmentStatement) assignment).getLeftExprs();
        }

        if (assignment instanceof LuaLocalDefinitionStatement) {
            idlist = ((LuaAssignmentStatement) assignment).getLeftExprs();
        }

        if ((idlist != null) && (idlist.count() > idx)) {
            return idlist.getSymbols()[idx];
        }

        return null;
    }

    @Nullable
    @Override
    public LuaDocComment getDocComment() {
        return LuaDocCommentUtil.findDocComment(this);
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }
}
