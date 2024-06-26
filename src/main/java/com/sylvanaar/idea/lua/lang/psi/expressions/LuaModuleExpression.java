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

package com.sylvanaar.idea.lua.lang.psi.expressions;

import consulo.document.util.TextRange;
import consulo.language.psi.StubBasedPsiElement;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.stubs.api.LuaModuleDeclarationStub;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/7/11
 * Time: 11:14 AM
 */
public interface LuaModuleExpression extends LuaFunctionCallExpression, LuaGlobal,
        LuaDeclarationExpression, LuaReferenceElement, StubBasedPsiElement<LuaModuleDeclarationStub> {
    TextRange getIncludedTextRange();

    boolean isSeeAll();
}
