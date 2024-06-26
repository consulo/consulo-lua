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

package com.sylvanaar.idea.lua.lang.psi.stubs;

import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.psi.stub.PsiFileStub;
import consulo.logging.Logger;
import consulo.language.psi.stub.PsiFileStubImpl;
import consulo.index.io.StringRef;
import com.sylvanaar.idea.lua.lang.parser.LuaParserDefinition;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;


public class LuaFileStub extends PsiFileStubImpl<LuaPsiFile> implements PsiFileStub<LuaPsiFile>
{
    private static final Logger log = Logger.getInstance("Lua.StubFile");
    private StringRef myName;

    public LuaFileStub(LuaPsiFile file) {
        super(file);
        myName = StringRef.fromString(file.getName());
        log.debug("FROM PSI: "+getName());
    }

    public LuaFileStub(StringRef name) {
        super(null);
        myName = name;
        log.debug("FROM NAME: "+getName());
    }

    public IStubFileElementType getType() {
        return LuaParserDefinition.LUA_FILE;
    }

    public String getName() {
        return myName.toString();
    }

    @Override
    public LuaPsiFile getPsi() {
//        log.debug("STUB->PSI: " + myName);
        return super.getPsi();
    }
}