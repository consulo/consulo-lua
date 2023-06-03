/*
 * Copyright 2009 Max Ishchenko
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

package com.sylvanaar.idea.lua.lang.lexer;

import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 06.07.2009
 * Time: 15:51:05
 */
public class LuaElementType extends IElementType
{

  private String debugName = null;

  public LuaElementType(String debugName) {
    super(debugName, LuaFileType.LUA_LANGUAGE);
    this.debugName = debugName;
  }

  public String toString() {
    return debugName;
  }

  public static abstract class PsiCreator extends LuaElementType {
    protected PsiCreator(String debugName) {
      super(debugName);
    }

    public abstract LuaPsiElement createPsi(@Nonnull ASTNode node);
  }

}
