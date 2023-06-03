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

package com.sylvanaar.idea.lua.lang.luadoc.completion.filters;

import consulo.language.ast.ASTNode;
import consulo.language.psi.filter.ElementFilter;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocTokenTypes;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocTag;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NonNls;

public class SimpleTagNameFilter implements ElementFilter {
    public boolean isAcceptable(Object element, PsiElement context) {
        if (context == null) return false;
        ASTNode node = context.getNode();
        return node != null && node.getElementType() == LuaDocTokenTypes.LDOC_TAG_NAME &&
               context.getParent() instanceof LuaDocTag;
    }

    public boolean isClassAcceptable(Class hintClass) {
        return true;
    }

    @NonNls
    public String toString() {
        return "Luadoc tagname filter";
    }
}