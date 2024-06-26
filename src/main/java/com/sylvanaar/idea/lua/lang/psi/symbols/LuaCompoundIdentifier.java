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

package com.sylvanaar.idea.lua.lang.psi.symbols;

import consulo.language.psi.PsiElement;
import consulo.language.psi.StubBasedPsiElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.stubs.api.LuaCompoundIdentifierStub;
import consulo.navigation.NavigationItem;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/30/11
 * Time: 10:31 PM
 */
public interface LuaCompoundIdentifier extends LuaIdentifier, LuaDeclarationExpression, NavigationItem,
        StubBasedPsiElement<LuaCompoundIdentifierStub> {
    LuaCompoundIdentifier getEnclosingIdentifier();

    PsiElement getScopeIdentifier();

    LuaExpression getLeftSymbol();

    LuaExpression getRightSymbol();

    boolean isCompoundDeclaration();

    String getOperator();
}
