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

package com.sylvanaar.idea.lua.lang.psi.lists;

import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaParameter;
import consulo.language.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 14, 2010
 * Time: 6:41:07 PM
 */
public interface LuaParameterList extends  LuaPsiElement {
    PsiElement getLeftParen();

    PsiElement getRightParen();

    public LuaParameter[] getLuaParameters();

    public int getParameterIndex(LuaParameter psiparameter);

    public int getParametersCount();
}
