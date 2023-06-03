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
package com.sylvanaar.idea.Lua.lang.psi.impl.symbols;

import javax.annotation.Nonnull;

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import com.sylvanaar.idea.Lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.Lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.Lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.Lua.lang.psi.impl.LuaPsiElementFactoryImpl;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaLocalIdentifier;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaParameter;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaUpvalueIdentifier;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.ast.ASTNode;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;

public class LuaParameterImpl extends LuaLocalDeclarationImpl implements LuaParameter {
    public LuaParameterImpl(@Nonnull
									ASTNode node) {
        super(node);
    }

    public String toString() {
        return "Parameter: " + getText();
    }

    @Override
    public LuaFunctionDefinition getDeclaringFunction() {
        return (LuaFunctionDefinition) getNode().getTreeParent().getTreeParent()
                .getPsi();
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitParameter(this);
    }

    @Override
    public void accept(@Nonnull
                       PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitParameter(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean isVarArgs() {
        return (getNode().getElementType() == LuaElementTypes.ELLIPSIS);
    }

    @Override
    public String getDefinedName() {
        return getName();
    }


    @Override
    public PsiElement setName(@Nonnull String s) {
        LuaDeclarationExpression decl = LuaPsiElementFactoryImpl.getInstance(getProject()).createParameterNameIdentifier(s);

        return replace(decl);
    }


    @Override
    public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place) {
        return processor.execute(this, state);
    }


    @Override
    public boolean isSameKind(LuaSymbol identifier) {
        return identifier instanceof LuaUpvalueIdentifier || identifier instanceof LuaLocalIdentifier;
    }
}
