/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import consulo.language.psi.ResolveResult;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.util.IncorrectOperationException;
import consulo.util.lang.StringUtil;
import consulo.language.psi.resolve.ResolveCache;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.util.PsiTreeUtil;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaLiteralExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaModuleExpression;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaStubElementBase;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaFunctionArguments;
import com.sylvanaar.idea.lua.lang.psi.resolve.LuaResolver;
import com.sylvanaar.idea.lua.lang.psi.resolve.ResolveUtil;
import com.sylvanaar.idea.lua.lang.psi.stubs.api.LuaModuleDeclarationStub;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.types.*;
import com.sylvanaar.idea.lua.lang.psi.util.SymbolUtil;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.ast.ASTNode;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/7/11
 * Time: 11:21 AM
 */
public class LuaModuleExpressionImpl extends LuaStubElementBase<LuaModuleDeclarationStub> implements LuaModuleExpression {
    public LuaModuleExpressionImpl(ASTNode node) {
        super(node);
        type = new LuaTable();
    }

//    @Override
//    public PsiElement getParent() {
//        return getDefinitionParent();
//    }

    public LuaModuleExpressionImpl(LuaModuleDeclarationStub stub) {
        super(stub, LuaElementTypes.MODULE_NAME_DECL);
        type = new StubType(stub.getEncodedType());
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitModuleExpression(this);
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitModuleExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }

//    public void acceptChildren(LuaElementVisitor visitor) {
//
//    }

    @Override
    public String toString() {
        String name = getDefinedName();
        return "Module: " + (name != null ? name : "err");
    }

    PsiElement getNameElement() {
        LuaExpressionList argumentList = getArgumentList();

        if (argumentList == null || argumentList.count() == 0) return null;

        return argumentList.getLuaExpressions().get(0);
    }

    @Override
    @Nullable
    public String getModuleName() {
        final LuaModuleDeclarationStub stub = getStub();
        if (stub != null) {
            return stub.getModule();
        }

        if (!isValid()) return null;

        LuaPsiFile file = (LuaPsiFile) getContainingFile();
        if (file == null)
            return null;

        return file.getModuleNameAtOffset(getTextOffset());
    }

    @Override
    public String getGlobalEnvironmentName() {
        return SymbolUtil.getGlobalEnvironmentName(this);
    }

    @Nonnull
    @Override
    public IStubElementType getElementType() {
        return LuaElementTypes.MODULE_NAME_DECL;
    }

    @Override
    public String getName() {
        final LuaModuleDeclarationStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement expression = getNameElement();

        LuaLiteralExpression lit = null;

        if (expression instanceof LuaLiteralExpression) lit = (LuaLiteralExpression) expression;

        String name = null;

        if (lit != null && lit.getLuaType() == LuaPrimitiveType.STRING) {
            name = (String) lit.getValue();
        } else if (expression instanceof LuaSymbol &&
                StringUtil.notNullize(((LuaSymbol) expression).getName()).equals("...")) {

            LuaPsiFile file = PsiTreeUtil.getParentOfType(this, LuaPsiFile.class);

            if (file != null) {
                name = file.getName();
                if (name.endsWith(".lua")) {
                    name = name.substring(0, name.length() - 4);
                }
            }
        }

        final LuaType luaType = getLuaType();
        assert luaType instanceof LuaNamespacedType;
        ((LuaNamespacedType) luaType).setNamespace(name);

        if (name == null)
            name = "<VARIABLE>";
        return name;
    }

    @Override
    public PsiElement setName(@NonNls @Nonnull String name) throws IncorrectOperationException
	{
        return null;
    }

    @Override
    public String getDefinedName() {
        return getGlobalEnvironmentName();
    }

    public boolean processDeclarations(@Nonnull PsiScopeProcessor processor,
                                       @Nonnull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @Nonnull PsiElement place) {

        if (!processor.execute(this, resolveState)) return false;


        return true;
    }


    @Override
    public boolean isSameKind(LuaSymbol symbol) {
        return symbol instanceof LuaGlobal;
    }

    @Override
    public boolean isAssignedTo() {
        return true;
    }

    @Override
    public PsiElement replaceWithExpression(LuaExpression newCall, boolean b) {
        return null;
    }

    LuaType type = new LuaTable();

    @Nonnull
    @Override
    public LuaType getLuaType() {
        if (type instanceof StubType)
            type = ((StubType) type).get();
//        CachedValue<LuaType> type = getUserData(CALCULATED_TYPE);
//        if (type == null) {
//            type = CachedValuesManager.getManager(getProject()).createCachedValue(
//                    new CachedValueProvider<LuaType>() {
//                        @Override
//                        public Result<LuaType> compute() {
//                            return new Result<LuaType>(new LuaTable());
//                        }
//                    }, false);
//            putUserData(CALCULATED_TYPE, type);
//        }
//
//        return type.getValue();

        return type;
    }

    @Override
    public void setLuaType(LuaType type) {
      //  throw new IncorrectOperationException("Cant set the type of module");
    }

    @Override
    public Object evaluate() {
        return getLuaType();
    }

    @Override
    public TextRange getIncludedTextRange() {
        return new TextRange(getTextOffset() + getTextLength(), getContainingFile().getTextRange().getEndOffset());
    }

    @Override
    public boolean isSeeAll() {
        LuaExpressionList argumentList = getArgumentList();

        if (argumentList == null) return false;

        final List<LuaExpression> parms = argumentList.getLuaExpressions();

        if (parms.size() < 2) return false;

        if (parms.get(1).getText().equals("package.seeall"))
            return true;

        return false;
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Nonnull
    public TextRange getRangeInElement() {
        PsiElement e = getNameElement();
        if (e != null)
            return TextRange.from(e.getTextOffset() - getTextOffset(), e.getTextLength());

        return TextRange.EMPTY_RANGE;
    }

    @Nullable
    public PsiElement resolve() {
        ResolveResult[] results = ResolveCache.getInstance(getProject()).resolveWithCaching(this, RESOLVER, true, false);
        return results.length == 1 ? results[0].getElement() : null;
    }

    @Nonnull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
        return ResolveCache.getInstance(getProject()).resolveWithCaching(this, RESOLVER, true, incompleteCode);
    }

    private static final LuaResolver RESOLVER = new LuaResolver();

    @Nonnull
    public String getCanonicalText() {
        return StringUtil.notNullize(getGlobalEnvironmentName());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
	{
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException
	{
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return getManager().areElementsEquivalent(element, resolve());
    }

    @Nonnull
    @Override
    public Object[] getVariants() {
        return ResolveUtil.getVariants(this);
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    @Override
    @Nullable
    public LuaExpressionList getArgumentList() {
        final LuaFunctionArguments arguments = findChildByClass(LuaFunctionArguments.class);

        return arguments == null ? null : arguments.getExpressions();
    }

    @Override
    public LuaReferenceElement getFunctionNameElement() {
        return findChildByClass(LuaReferenceElement.class);
    }

    @Override
    public boolean checkSelfReference(PsiElement element) {
        return true;
    }
}
