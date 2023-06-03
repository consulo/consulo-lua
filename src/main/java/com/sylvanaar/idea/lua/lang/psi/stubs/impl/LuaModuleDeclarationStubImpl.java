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

package com.sylvanaar.idea.lua.lang.psi.stubs.impl;

import javax.annotation.Nonnull;

import consulo.language.psi.stub.StubElement;
import consulo.index.io.StringRef;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaModuleExpression;
import com.sylvanaar.idea.lua.lang.psi.stubs.api.LuaModuleDeclarationStub;
import com.sylvanaar.idea.lua.lang.psi.types.LuaType;
import consulo.language.psi.stub.NamedStubBase;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:10 PM
 */
public class LuaModuleDeclarationStubImpl extends NamedStubBase<LuaModuleExpression> implements LuaModuleDeclarationStub {
    @Nullable
    private StringRef myModule;
    private byte[]    myType;


    public LuaModuleDeclarationStubImpl(StubElement parent, @Nullable StringRef name, @Nullable StringRef module,
										@Nonnull byte[] type) {
        super(parent, LuaElementTypes.MODULE_NAME_DECL, name);
        myModule = module;
        myType = type;
    }

    @Override @Nullable
    public String getModule() {
        return myModule != null ? myModule.getString() : null;
    }

    @Nonnull
    public byte[] getEncodedType() {
        return myType;
    }

    @Nullable
    @Override
    public LuaType getLuaType() {
        return null;
    }
}
