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

package com.sylvanaar.idea.lua.lang.psi;

import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocCommentOwner;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaParameterList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaBlockStatement;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.types.LuaFunction;
import consulo.document.util.TextRange;
import consulo.navigation.ItemPresentation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 11, 2010
 * Time: 3:32:19 PM
 */
public interface LuaFunctionDefinition extends LuaPsiElement, LuaBlockStatement, ItemPresentation, LuaExpression, LuaDocCommentOwner {
    @Nullable
    String getName();

    @Nullable
    LuaSymbol getIdentifier();

    LuaParameterList getParameters();

    TextRange getRangeEnclosingBlock();

    @Nonnull
    LuaFunction getLuaType();
}
