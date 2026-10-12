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
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;

/**
 * @author Jon S Akhtar
 * @since 2011-01-23
 */
public abstract class LuaSymbolImpl extends LuaPsiElementImpl implements LuaSymbol {
    public LuaSymbolImpl(ASTNode node) {
        super(node);
    }

    @Override
    @RequiredReadAction
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
            @Override
            @RequiredReadAction
            public LocalizeValue getPresentableText() {
                return getPresentationText();
            }

            @Override
            @RequiredReadAction
            public @Nullable String getLocationString() {
                String name = getContainingFile().getName();
                return "(in " + name + ")";
            }

            @Override
            public @Nullable Image getIcon() {
                return LuaIcons.LUA_ICON;
            }
        };
    }

    @Override
    @RequiredReadAction
    public LocalizeValue getPresentationText() {
        return LocalizeValue.ofNullable(getName());
    }

    @Override
    @RequiredWriteAction
    public PsiElement replaceWithExpression(LuaExpression newExpr, boolean removeUnnecessaryParentheses) {
        return LuaPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    @RequiredReadAction
    public boolean isEquivalentTo(PsiElement that) {
        if (this == that) {
            return true;
        }

        PsiElement self = this;

        if (that instanceof LuaReferenceElement thatRef) {
            that = thatRef.getElement();
        }

        if (this instanceof LuaReferenceElement thisRef) {
            self = thisRef.getElement();
        }

        if (self == that) {
            return true;
        }

        if (!(self instanceof LuaNamedElement thisNamedElem)) {
            return false;
        }

        if (that instanceof LuaAlias thatAlias
            && thatAlias.getAliasElement() instanceof LuaSymbol thatSymbol
            && isEquivalentTo(thatSymbol)) {
            return true;
        }

        if (that instanceof LuaSymbol thatSymbol) {
            String myName = thisNamedElem.getName();
            if (myName == null) {
                return false;
            }

            if (this instanceof LuaCompoundReferenceElementImpl) {
                return myName.equals(thatSymbol.getName());
            }
            else if (this instanceof LuaReferenceElement thisRef) {
                return myName.equals(thatSymbol.getName())
                    && thatSymbol.isSameKind((LuaSymbol) thisRef.getElement());
            }

            return myName.equals(thatSymbol.getName());
        }

        return false;
    }
}
