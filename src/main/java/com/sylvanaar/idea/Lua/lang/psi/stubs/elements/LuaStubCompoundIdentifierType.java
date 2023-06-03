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

package com.sylvanaar.idea.Lua.lang.psi.stubs.elements;

import java.io.IOException;

import javax.annotation.Nonnull;

import consulo.language.psi.stub.StubOutputStream;
import consulo.index.io.StringRef;
import com.sylvanaar.idea.Lua.lang.psi.impl.symbols.LuaCompoundIdentifierImpl;
import com.sylvanaar.idea.Lua.lang.psi.stubs.LuaStubElementType;
import com.sylvanaar.idea.Lua.lang.psi.stubs.LuaStubUtils;
import com.sylvanaar.idea.Lua.lang.psi.stubs.api.LuaCompoundIdentifierStub;
import com.sylvanaar.idea.Lua.lang.psi.stubs.impl.LuaCompoundIdentifierStubImpl;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaCompoundIdentifier;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaGlobal;
import com.sylvanaar.idea.Lua.lang.psi.types.LuaPrimitiveType;
import com.sylvanaar.idea.Lua.lang.psi.types.LuaType;
import com.sylvanaar.idea.Lua.util.LuaSerializationUtils;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.util.lang.Pair;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/21/11
 * Time: 7:32 PM
 */
public class LuaStubCompoundIdentifierType
    extends LuaStubElementType<LuaCompoundIdentifierStub, LuaCompoundIdentifier> {

    public LuaStubCompoundIdentifierType() {
        super("COMPOUND");
    }

    @Override public String getExternalId() {
        return "Lua.COMPOUND";
    }

    @Override
    public LuaCompoundIdentifier createPsi(@Nonnull LuaCompoundIdentifierStub stub) {
        return new LuaCompoundIdentifierImpl(stub);
    }

    @Override
    public LuaCompoundIdentifierStub createStub(@Nonnull LuaCompoundIdentifier psi, StubElement parentStub) {
        final LuaType luaType = psi.getLuaType();
        final byte[] bytes = luaType instanceof LuaPrimitiveType ? null : LuaSerializationUtils.serialize(luaType);
        final boolean declaration = psi.isCompoundDeclaration() && psi.getScopeIdentifier() instanceof LuaGlobal;
        return new LuaCompoundIdentifierStubImpl(parentStub, StringRef.fromNullableString(psi.getName()), declaration, bytes, luaType);
    }

    @Override
    public void serialize(LuaCompoundIdentifierStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        LuaStubUtils.writeSubstitutableType(stub.getLuaType(), stub.getEncodedType(), dataStream);
        dataStream.writeBoolean(stub.isGlobalDeclaration());
    }

    @Override
    public LuaCompoundIdentifierStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();

        final Pair<LuaType, byte[]> pair = LuaStubUtils.readSubstitutableType(dataStream);
        byte[] typedata = pair.getSecond();
        LuaType type = pair.first;

        boolean isDeclaration = dataStream.readBoolean();

        return new LuaCompoundIdentifierStubImpl(parentStub, ref, isDeclaration, typedata, type);
    }

    @Override
    public void indexStub(LuaCompoundIdentifierStub stub, IndexSink sink) {
//        String name = stub.getName();
//
//        if (StringUtil.isNotEmpty(name) && stub.isGlobalDeclaration()) {
//          sink.occurrence(LuaGlobalDeclarationIndex.KEY, name);
//        }
    }
}
