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
import javax.annotation.Nullable;

import consulo.navigation.ItemPresentation;
import consulo.language.psi.PsiElement;
import com.sylvanaar.idea.lua.LuaIcons;
import com.sylvanaar.idea.lua.lang.psi.LuaNamedElement;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiElementImpl;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaAlias;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.types.LuaPrimitiveType;
import com.sylvanaar.idea.lua.lang.psi.types.LuaType;
import com.sylvanaar.idea.lua.lang.psi.types.LuaTypeUtil;
import com.sylvanaar.idea.lua.lang.psi.util.LuaPsiUtils;
import consulo.language.ast.ASTNode;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:52 PM
 */
public abstract class LuaSymbolImpl extends LuaPsiElementImpl implements LuaSymbol {


    public LuaSymbolImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText();
    }

    protected LuaType type = LuaPrimitiveType.ANY;
    @Nonnull
    @Override
    public LuaType getLuaType() {
        return type;
    }

    @Override
    public void setLuaType(LuaType type) {
        this.type = LuaTypeUtil.combineTypes(this.type, type);
    }

    @Override
    public Object evaluate() {
        return null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return getPresentationText();
            }

            @Nullable
            public String getLocationString() {
                String name = getContainingFile().getName();
                return "(in " + name + ")";
            }

            @Nullable
            public Image getIcon() {
                return LuaIcons.LUA_ICON;
            }
        };
    }

    public String getPresentationText() {
        return getName();
    }

    @Override
    public PsiElement replaceWithExpression(LuaExpression newExpr, boolean removeUnnecessaryParentheses) {
        return LuaPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        if (this == another)
            return true;

        PsiElement self = this;

        if (another instanceof LuaReferenceElement)
            another = ((LuaReferenceElement) another).getElement();

        if (this instanceof LuaReferenceElement)
            self = ((LuaReferenceElement) this).getElement();

        if (self == another)
            return true;

        if (!(self instanceof LuaNamedElement))
            return false;

        if (another instanceof LuaAlias) {
            final PsiElement aliasElement = ((LuaAlias) another).getAliasElement();
            if (aliasElement instanceof LuaSymbol) if (isEquivalentTo(aliasElement)) return true;
        }

        if (another instanceof LuaSymbol) {
            String myName = ((LuaNamedElement) self).getName();
            if (myName == null) return false;

            if (this instanceof LuaCompoundReferenceElementImpl) {
                return myName.equals(((LuaSymbol) another).getName());
            } else if (this instanceof LuaReferenceElement) {
                return myName.equals(((LuaSymbol) another).getName()) &&
                       ((LuaSymbol) another).isSameKind((LuaSymbol) ((LuaReferenceElement) this).getElement());
            }

            return myName.equals(((LuaSymbol) another).getName());
        }

        return false;
    }
}
