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
package com.sylvanaar.idea.lua.findUsages;

import com.sylvanaar.idea.lua.lang.LuaLanguage;
import com.sylvanaar.idea.lua.lang.lexer.LuaLexer;
import com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.lua.lang.psi.LuaNamedElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaFieldIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaLocal;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaParameter;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaUpvalueIdentifier;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.cacheBuilder.DefaultWordsScanner;
import consulo.language.cacheBuilder.WordsScanner;
import consulo.language.findUsage.FindUsagesProvider;
import consulo.language.psi.PsiElement;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;


/**
 * @author ven
 */
@ExtensionImpl
public class LuaFindUsagesProvider implements FindUsagesProvider {
    @Nonnull
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new LuaLexer(),
                LuaTokenTypes.IDENTIFIERS_SET, LuaTokenTypes.COMMENT_SET, LuaTokenTypes.LITERALS_SET) {{
            setMayHaveFileRefsInLiterals(true);
        }};
    }

    public boolean canFindUsagesFor(@Nonnull final PsiElement psiElement) {
        return psiElement instanceof LuaNamedElement;
    }

    @Nonnull
    public String getType(@Nonnull final PsiElement element) {
        if (element instanceof LuaUpvalueIdentifier) {
            return "upvalue";
        }
        if (element instanceof LuaParameter) {
            return "parameter";
        }
        if (element instanceof LuaFieldIdentifier) {
            return "field";
        }
        if (element instanceof LuaLocal) {
            return "local";
        }
        if (element instanceof LuaGlobal) {
            return "global";
        }
        return "identifier";
    }

    @Nonnull
    public String getDescriptiveName(@Nonnull final PsiElement element) {
        return getName(element);
    }

    @Nonnull
    public String getNodeText(@Nonnull final PsiElement element, final boolean useFullName) {
        final StringBuilder sb = new StringBuilder(getType(element));
        if (sb.length() > 0) {
            sb.append(" ");
        }

        sb.append(getName(element));
        return sb.toString();
    }

    @Nonnull
    private String getName(@Nonnull final PsiElement element) {
        if (element instanceof LuaNamedElement) {
            return StringUtil.notNullize(((LuaNamedElement) element).getName());
        }
        return "";
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}