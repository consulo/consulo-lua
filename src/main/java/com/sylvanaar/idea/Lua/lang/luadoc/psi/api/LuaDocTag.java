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

package com.sylvanaar.idea.Lua.lang.luadoc.psi.api;

import consulo.language.psi.PsiElement;
import com.sylvanaar.idea.Lua.lang.psi.LuaNamedElement;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author ilyas
 */
public interface LuaDocTag extends LuaDocPsiElement, LuaNamedElement /*, PsiDocTag */ {
    LuaDocTag[] EMPTY_ARRAY = new LuaDocTag[0];

    LuaDocComment getContainingComment();

    @Nullable
    LuaDocTagValueToken getValueElement();

    @Nonnull
    public PsiElement[] getDescriptionElements();

    @Nullable
    LuaDocParameterReference getDocParameterReference();

    LuaDocFieldReference getDocFieldReference();

    PsiElement getNameElement();

    @NonNls
    @Nonnull
    String getName();
}
