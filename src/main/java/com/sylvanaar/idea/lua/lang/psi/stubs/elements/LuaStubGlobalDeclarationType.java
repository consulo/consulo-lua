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
package com.sylvanaar.idea.lua.lang.psi.stubs.elements;

import java.io.IOException;

import javax.annotation.Nonnull;

import consulo.index.io.StringRef;
import consulo.logging.Logger;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import com.sylvanaar.idea.lua.lang.psi.impl.symbols.LuaGlobalDeclarationImpl;
import com.sylvanaar.idea.lua.lang.psi.stubs.LuaStubElementType;
import com.sylvanaar.idea.lua.lang.psi.stubs.LuaStubUtils;
import com.sylvanaar.idea.lua.lang.psi.stubs.api.LuaGlobalDeclarationStub;
import com.sylvanaar.idea.lua.lang.psi.stubs.impl.LuaGlobalDeclarationStubImpl;
import com.sylvanaar.idea.lua.lang.psi.stubs.index.LuaGlobalDeclarationIndex;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobalDeclaration;
import com.sylvanaar.idea.lua.lang.psi.types.LuaPrimitiveType;
import com.sylvanaar.idea.lua.lang.psi.types.LuaType;
import com.sylvanaar.idea.lua.util.LuaSerializationUtils;
import consulo.util.lang.Pair;
import consulo.util.lang.StringUtil;


/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:01 PM
 */
public class LuaStubGlobalDeclarationType extends LuaStubElementType<LuaGlobalDeclarationStub, LuaGlobalDeclaration> {
    private static final Logger log = Logger.getInstance("Lua.StubGlobal");

    public LuaStubGlobalDeclarationType() {
        this("GLOBAL");
    }

    public LuaStubGlobalDeclarationType(String s) {
        super(s);
    }

    @Override
    public String getExternalId() {
        return "Lua.GLOBAL";
    }

    @Override
    public LuaGlobalDeclaration createPsi(@Nonnull
    LuaGlobalDeclarationStub stub) {
        return new LuaGlobalDeclarationImpl(stub);
    }

    @Override
    public LuaGlobalDeclarationStub createStub(
        @Nonnull
    LuaGlobalDeclaration psi, StubElement parentStub) {
        final LuaType luaType = psi.getLuaType();
        final byte[] bytes = (luaType instanceof LuaPrimitiveType) ? null
                                                                   : LuaSerializationUtils.serialize(luaType);

        return new LuaGlobalDeclarationStubImpl(parentStub,
            StringRef.fromNullableString(psi.getName()),
            StringRef.fromNullableString(psi.getModuleName()), bytes, luaType);
    }

    @Override
    public void serialize(LuaGlobalDeclarationStub stub,
        StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeName(stub.getModule());
        LuaStubUtils.writeSubstitutableType(stub.getLuaType(),
            stub.getEncodedType(), dataStream);
    }

    @Override
    public LuaGlobalDeclarationStub deserialize(StubInputStream dataStream,
        StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        StringRef mref = dataStream.readName();

        final Pair<LuaType, byte[]> pair = LuaStubUtils.readSubstitutableType(dataStream);
        byte[] typedata = pair.getSecond();
        LuaType type = pair.first;

        return new LuaGlobalDeclarationStubImpl(parentStub, ref, mref,
            typedata, type);
    }

    @Override
    public void indexStub(LuaGlobalDeclarationStub stub, IndexSink sink) {
        String module = stub.getModule();
        final String stubName = stub.getName();

        if (StringUtil.isNotEmpty(stubName)) {
            String name = StringUtil.isEmpty(module) ? stubName
                                                     : (module + '.' +
                stubName);
            //            log.debug("sink: " + name);
            assert name != null;
            sink.occurrence(LuaGlobalDeclarationIndex.KEY, name);
        }
    }
}
