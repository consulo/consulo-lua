/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.Lua.lang.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiKeyword;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiToken;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 22, 2010
 * Time: 2:21:32 AM
 */
public class LuaPsiKeywordImpl extends LeafPsiElement implements LuaPsiKeyword, LuaPsiToken {
  public LuaPsiKeywordImpl(IElementType type, CharSequence text) {
    super(type, text);
  }

  public IElementType getTokenType(){
    return getElementType();
  }

  public void accept(@Nonnull PsiElementVisitor visitor){
    if (visitor instanceof LuaElementVisitor) {
      ((LuaElementVisitor)visitor).visitKeyword(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  public String toString(){
    return "LuaKeyword:" + getText();
  }

//  static {
//    for(Field field: PsiKeyword.class.getFields()) {
//      CharTableImpl.staticIntern(field.getName().toLowerCase());
//    }
//  }
}
