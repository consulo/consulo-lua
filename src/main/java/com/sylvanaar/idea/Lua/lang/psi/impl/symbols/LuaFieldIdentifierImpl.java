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

import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.expressions.*;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiElementFactoryImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaStubElementBase;
import com.sylvanaar.idea.lua.lang.psi.impl.expressions.LuaStringLiteralExpressionImpl;
import com.sylvanaar.idea.lua.lang.psi.resolve.LuaResolver;
import com.sylvanaar.idea.lua.lang.psi.stubs.LuaStubUtils;
import com.sylvanaar.idea.lua.lang.psi.stubs.impl.LuaFieldStub;
import com.sylvanaar.idea.lua.lang.psi.stubs.index.LuaGlobalDeclarationIndex;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaCompoundIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.types.*;
import com.sylvanaar.idea.lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.content.scope.ProjectScopes;
import consulo.util.collection.ArrayUtil;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/15/11
 * Time: 1:31 AM
 */
public class LuaFieldIdentifierImpl extends LuaStubElementBase<LuaFieldStub> implements LuaFieldIdentifier {
    public LuaFieldIdentifierImpl(ASTNode node) {
        super(node);
    }

    public LuaFieldIdentifierImpl(LuaFieldStub stub) {
        super(stub, LuaElementTypes.FIELD_NAME);
        type = LuaStubUtils.GetStubOrPrimitiveType(stub);
    }

    @Override
    public String getName() {
        final LuaFieldStub stub = getStub();
        if (stub != null)
            return stub.getName();

        final PsiElement[] children = getChildren();
        if (children.length == 1) {
            final PsiElement child = children[0];
            if (child instanceof LuaStringLiteralExpressionImpl)
                return ((LuaStringLiteralExpressionImpl) child).getStringContent();
        }

        return getText();
    }

    @Override
    public PsiElement setName(@Nonnull @NonNls String name) throws IncorrectOperationException {
        LuaIdentifier node = LuaPsiElementFactoryImpl.getInstance(getProject()).createFieldNameIdentifier(name);
        replace(node);

        return this;
    }

    @Override
    public Object evaluate() {
        return getName();
    }

    private LuaType type = LuaPrimitiveType.ANY;

    @Nonnull
	@Override
    public LuaType getLuaType() {
        if (type instanceof StubType)
            type = ((StubType) type).get();
        return type;
    }

    @Override
    public void setLuaType(LuaType type) {
        this.type = LuaTypeUtil.combineTypes(this.type, type);
        LuaType outerType = null;
        final LuaCompoundIdentifier compositeIdentifier = getCompositeIdentifier();

        if (compositeIdentifier != null)
            outerType = compositeIdentifier.getLeftSymbol().getLuaType();
        else {
            PsiElement e = getParent().getParent();
            if (e instanceof LuaTableConstructor)
                outerType = ((LuaTableConstructor) e).getLuaType();
        }


        if (outerType instanceof LuaTable) {
            final String name = getName();
            if (name != null)
                ((LuaTable) outerType).addPossibleElement(name, this.type);
        }
    }



    @Override
    public PsiElement replaceWithExpression(LuaExpression newExpr, boolean removeUnnecessaryParentheses) {
        return LuaPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    public boolean isSameKind(LuaSymbol identifier) {
        return identifier instanceof LuaFieldIdentifier;
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitIdentifier(this);
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitIdentifier(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public boolean isDeclaration() {
        return isAssignedTo();
    }

    @Override
    public boolean isAssignedTo() {
        LuaCompoundIdentifier v = getCompositeIdentifier();

        if (v == null)
            return true; // the only times fields are not part of a composite identifier are table constructors.

        return v.isAssignedTo();
    }

    @Override public LuaCompoundIdentifier getCompositeIdentifier() {
        if (getParent() instanceof LuaCompoundIdentifier)
            return ((LuaCompoundIdentifier) getParent());

        return null;
    }

    @Override
    public String toString() {
        return "Field: " + getName();
    }

    private static final LuaResolver RESOLVER = new LuaResolver();

    public PsiElement getResolvedReference() { return getReference().resolve();}

    public String getRefCanonicalText() { return getReference().getCanonicalText();}

    @Override
    public PsiReference getReference() {
        final LuaCompoundIdentifier id = getCompositeIdentifier();
        final LuaCompoundReferenceElementImpl ref =
                id != null ? (LuaCompoundReferenceElementImpl) id.getReference() : null;

        return new PsiReference() {
            public PsiElement getElement() {
                final PsiElement[] children = LuaFieldIdentifierImpl.this.getChildren();
                if (children.length == 1) {
                    final PsiElement child = children[0];
                    if (child instanceof LuaStringLiteralExpressionImpl)
                        return child;
                }

                return LuaFieldIdentifierImpl.this;
            }

            public TextRange getRangeInElement() {
                final PsiElement element = getElement();
                if (element instanceof LuaStringLiteralExpressionImpl)
                    return ((LuaStringLiteralExpressionImpl) element).getStringContentTextRange();

                return new TextRange(0, getTextLength());
            }

            @Override
            @Nullable
            public PsiElement resolve() {
                if (ref != null) {
                    final PsiElement element = ref.resolve();
                    if (element instanceof LuaCompoundIdentifier)
                        return ((LuaCompoundIdentifier) element).getRightSymbol();

                    if (element != null)
                        return element;

                    LuaGlobalDeclarationIndex index = LuaGlobalDeclarationIndex.getInstance();
                    Collection<LuaDeclarationExpression> names = index.get(getCanonicalText(), getProject(),
                                                                           ProjectScopes.getAllScope(getProject()));

                    if (names.size() == 1)
                        return names.iterator().next();
                }

                return null;
            }

            @Nonnull
            public String getCanonicalText() {
                String name = getRangeInElement().substring(getText());
                LuaType t = LuaFieldIdentifierImpl.this.getNameSpaceIdentifier().getLuaType();
                if (t instanceof LuaNamespacedType) {
                    String namePrefix = ((LuaNamespacedType) t).getNamespace();
                    if (namePrefix != null)
                        name = String.format("%s.%s", namePrefix, name);
                }

                return name;
            }

            public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
                throw new UnsupportedOperationException();
            }

            public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException {
                throw new UnsupportedOperationException();
            }

            public boolean isReferenceTo(PsiElement element) {
                return resolve() == element;
            }

            @Nonnull
            public Object[] getVariants() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }

            public boolean isSoft() {
                return false;
            }
        };
    }


    //    @Override
//    public int getStartOffsetInParent() {
//        return getTextOffset()-getEnclosingIdentifier().getTextOffset();
//    }

    @Override
    public LuaCompoundIdentifier getEnclosingIdentifier() {
        LuaCompoundIdentifier v = getCompositeIdentifier();

        if (v == null)
            return null; // the only times fields are not part of a composite identifier are table constructors.
        return getCompositeIdentifier().getEnclosingIdentifier();
    }


    public LuaExpression getNameSpaceIdentifier() {
        final LuaCompoundIdentifier compositeIdentifier = getCompositeIdentifier();
        if (compositeIdentifier != null) {
            return compositeIdentifier.getLeftSymbol();
        }

        final LuaKeyValueInitializer initializer = PsiTreeUtil.getParentOfType(this, LuaKeyValueInitializer.class);
        if (initializer != null) {
            final PsiElement parent = initializer.getParent();
            if (parent instanceof LuaTableConstructor)
                return (LuaExpression) parent;
        }

        return this;
    }

    @Override
    public String getDefinedName() {
        return getName();
    }
}
