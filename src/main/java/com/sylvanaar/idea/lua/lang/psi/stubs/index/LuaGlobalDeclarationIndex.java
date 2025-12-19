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

package com.sylvanaar.idea.lua.lang.psi.stubs.index;

import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.stub.StringStubIndexExtension;
import consulo.language.psi.stub.StubIndexKey;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:27 PM
 */
@ExtensionImpl
public class LuaGlobalDeclarationIndex extends StringStubIndexExtension<LuaDeclarationExpression>
{
    public static final StubIndexKey<String, LuaDeclarationExpression> KEY =
            StubIndexKey.createIndexKey("lua.global.name");

    private static final LuaGlobalDeclarationIndex ourInstance = new LuaGlobalDeclarationIndex();

    public static LuaGlobalDeclarationIndex getInstance() {
        return ourInstance;
    }

    @Nonnull
    public StubIndexKey<String, LuaDeclarationExpression> getKey() {
        return KEY;
    }
}
