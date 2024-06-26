/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sylvanaar.idea.lua.lang.psi.impl.symbols;

import javax.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiReference;
import consulo.navigation.NavigationItem;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaFieldIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaCompoundIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.util.collection.ArrayUtil;
import consulo.util.lang.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/5/11
 * Time: 12:35 PM
 */
public class LuaCompoundReferenceElementImpl extends LuaReferenceElementImpl implements LuaReferenceElement, LuaExpression {

    public LuaCompoundReferenceElementImpl(ASTNode node) {
        super(node);



    }

    @Nonnull
    @Override
    public Object[] getVariants() {
        final PsiElement element = getElement();
        assert element instanceof LuaCompoundIdentifier;
        if (((LuaCompoundIdentifier) element).isCompoundDeclaration())
            return ArrayUtil.EMPTY_OBJECT_ARRAY;

        return super.getVariants();
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitCompoundReference(this);
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitCompoundReference(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean isSameKind(LuaSymbol symbol) {
        return symbol instanceof LuaGlobalDeclarationImpl || symbol instanceof LuaFieldIdentifier;
    }

    public PsiElement getNameElement() {
//        return ((LuaCompoundIdentifier)getElement()).getRightSymbol();
        return getElement();
    }

    @Override
    public PsiElement getElement() {
        return  findChildByType(LuaElementTypes.GETTABLE);
    }

    public PsiReference getReference() {
//        final LuaExpression rightSymbol = (LuaExpression) getNameElement();
//        if (rightSymbol instanceof LuaStringLiteralExpressionImpl) {
//            final TextRange textRange =
//                    ((LuaStringLiteralExpressionImpl) rightSymbol).getStringContentTextRange();
//            if (textRange != null)
//                return new PsiReferenceBase.Immediate<PsiElement>(rightSymbol,
//                                    textRange.shiftRight(getTextOffset()), rightSymbol);
//        }

//        return null;
        return this;
    }

    @Nonnull
    @Override
    public PsiReference[] getReferences() {
        return PsiReference.EMPTY_ARRAY;
    }
    //    public TextRange getRangeInElement() {
//        final PsiElement nameElement = ((LuaCompoundIdentifier)getElement()).getRightSymbol();
//        int nameLen = nameElement != null ? nameElement.getTextLength() : 0;
//
//        final int textOffset = nameElement != null ? nameElement.getTextOffset() : 0;
//
//        return TextRange.from(textOffset - getTextOffset(), nameLen);
//    }

    @Override
    @Nonnull
    public TextRange getRangeInElement() {
        LuaExpression e = ((LuaCompoundIdentifier)getElement()).getRightSymbol();
        if (e != null)
            return TextRange.from(e.getTextOffset() - getTextOffset(), e.getTextLength());

        return TextRange.EMPTY_RANGE;
    }


    @Override
    public String toString() {
        return "Compound Reference: " + getText();
    }

//    @Override
//    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state,
//                                       PsiElement lastParent, @NotNull PsiElement place) {
//        return LuaPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
//    }

    @Nonnull
    public String getCanonicalText() {
        LuaCompoundIdentifier element = (LuaCompoundIdentifier) getElement();


        final PsiElement scopeIdentifier = element.getScopeIdentifier();

        final String definedName = element.getDefinedName();
        if (scopeIdentifier instanceof LuaGlobal) {
            final String moduleName = ((LuaGlobal) scopeIdentifier).getModuleName();

            final String name = ((NavigationItem) scopeIdentifier).getName();
            final boolean hasModule = StringUtil.isNotEmpty(moduleName);
            if ("_M".equals(name))
                return moduleName + definedName.substring(2);

            if (hasModule)
                return moduleName + "." + definedName;
        }

        return definedName;
    }

    @Override
    public boolean checkSelfReference(PsiElement element) {
        return element instanceof LuaCompoundIdentifier && ((LuaCompoundIdentifier) element).isCompoundDeclaration();
    }
}
