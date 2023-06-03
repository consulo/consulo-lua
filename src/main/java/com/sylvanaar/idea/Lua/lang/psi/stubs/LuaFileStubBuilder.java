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

import javax.annotation.Nonnull;

import com.sylvanaar.idea.lua.lang.psi.*;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.DefaultStubBuilder;
import consulo.language.psi.stub.StubElement;
import consulo.logging.Logger;


public class LuaFileStubBuilder extends DefaultStubBuilder
{
    private static final Logger log = Logger.getInstance("Lua.FileStubBuilder");

    protected StubElement createStubForFile(@Nonnull PsiFile file) {
        if (file instanceof LuaPsiFile) {
//            log.debug("CREATE File stub: " + file.getName());
            return new LuaFileStub((LuaPsiFile) file);
        }
        return super.createStubForFile(file);
    }
}