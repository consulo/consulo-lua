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

package com.sylvanaar.idea.lua.lang.psi.statements;

import javax.annotation.Nonnull;

import com.sylvanaar.idea.lua.lang.InferenceCapable;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.util.LuaAssignment;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;

import javax.annotation.Nullable;

public interface LuaAssignmentStatement extends LuaMaybeDeclarationAssignmentStatement, LuaStatementElement, LuaDeclarationStatement, InferenceCapable {
    public LuaIdentifierList getLeftExprs();
    public LuaExpressionList getRightExprs();

    @Nonnull
    public LuaAssignment[] getAssignments();

    @Nullable
    public LuaExpression getAssignedValue(LuaSymbol symbol);
    
    public IElementType getOperationTokenType();
    public PsiElement getOperatorElement();
}
