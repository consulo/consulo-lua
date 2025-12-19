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

import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaModuleExpression;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiElementFactoryImpl;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobalDeclaration;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobalIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.util.SymbolUtil;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.util.IncorrectOperationException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/24/11
 * Time: 12:26 AM
 */
public class LuaGlobalUsageImpl extends LuaIdentifierImpl implements LuaGlobalIdentifier {
    public LuaGlobalUsageImpl(ASTNode node) {
        super(node);
    }

    @Override
    public boolean isAssignedTo() {
        return false;
    }

    @Override
    public boolean isSameKind(LuaSymbol identifier) {
        return identifier instanceof LuaGlobalDeclaration || identifier instanceof LuaModuleExpression;
    }

    @Override
    public PsiElement setName(@Nonnull @NonNls String name) throws IncorrectOperationException
	{
        LuaIdentifier node = LuaPsiElementFactoryImpl.getInstance(getProject()).createGlobalNameIdentifier(name);
        replace(node);

        return this;
    }

    @Override
    public PsiReference getReference() {
        return (PsiReference) getParent();
    }

    @Override
    @Nullable
    public String getModuleName() {
        if (!isValid()) return null;
        
        LuaPsiFile file = (LuaPsiFile) getContainingFile();
        if (file == null) return null;

        return file.getModuleNameAtOffset(getTextOffset());
    }

    @Override
    public String getGlobalEnvironmentName() {
        return SymbolUtil.getGlobalEnvironmentName(this);
    }

    @Override
    public String toString() {
        return "Global: " + getGlobalEnvironmentName();
    }
}
