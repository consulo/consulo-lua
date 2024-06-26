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

package com.sylvanaar.idea.lua.lang.psi;

import com.sylvanaar.idea.lua.lang.InferenceCapable;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaModuleExpression;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.PsiFileWithStubSupport;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 13, 2010
 * Time: 7:29:34 PM
 */
public interface LuaPsiFile extends LuaPsiFileBase, PsiFile, PsiFileWithStubSupport, InferenceCapable {
    @Nullable
    String getModuleNameAtOffset(int offset);

    @Nullable
    LuaModuleExpression getModuleAtOffset(int offset);

    void setContext(PsiElement e);
}
