/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.sylvanaar.idea.Lua.psi.statements.arguments;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.sylvanaar.idea.Lua.psi.LuaElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author ilyas
 */
public interface LuaArgumentLabel extends LuaElement, PsiReference {

  LuaArgumentLabel[] EMPTY_ARRAY = new LuaArgumentLabel[0];

  @NotNull
  PsiElement getNameElement();

  String getName();

  @Nullable
  PsiType getExpectedArgumentType();
}
