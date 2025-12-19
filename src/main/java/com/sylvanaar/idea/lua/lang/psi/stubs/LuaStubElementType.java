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

import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubSerializer;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/23/11
 * Time: 8:19 PM
 */
public abstract class LuaStubElementType<S extends StubElement, T extends LuaPsiElement>
        extends IStubElementType<S, T>  implements StubSerializer<S> {

    protected LuaStubElementType(@NonNls @Nonnull String debugName) {
        super(debugName, LuaFileType.LUA_LANGUAGE);
    }


    @Override
    public String getExternalId() {
        return getLanguage().getID() + '.' + toString();
    }
}
