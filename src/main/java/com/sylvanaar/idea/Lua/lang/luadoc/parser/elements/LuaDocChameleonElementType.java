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

package com.sylvanaar.idea.lua.lang.luadoc.parser.elements;

import com.sylvanaar.idea.lua.LuaFileType;
import consulo.language.Language;
import consulo.language.ast.ILazyParseableElementType;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;

public abstract class LuaDocChameleonElementType extends ILazyParseableElementType
{
  public LuaDocChameleonElementType(@NonNls String debugName) {
    super(debugName);
  }

  @Nonnull
  public Language getLanguage() {
    return LuaFileType.LUA_FILE_TYPE.getLanguage();
  }

}
