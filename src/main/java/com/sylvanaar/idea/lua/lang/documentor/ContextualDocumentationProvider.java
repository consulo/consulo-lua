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

package com.sylvanaar.idea.lua.lang.documentor;

import com.sylvanaar.idea.lua.lang.LuaLanguage;
import com.sylvanaar.idea.lua.lang.psi.LuaNamedElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaStatementElement;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaAlias;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.documentation.LanguageDocumentationProvider;
import consulo.language.psi.PsiElement;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/29/11
 * Time: 8:26 AM
 */
@ExtensionImpl(id = "lua.context")
public class ContextualDocumentationProvider implements LanguageDocumentationProvider {
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        // only time we see the two eleements equal is when we call for documentation
        // during parameter info display
        if (element == originalElement) return null;
        PsiElement s = element;

        while (!(s instanceof LuaStatementElement) && s != null) {
            s = s.getContext();
        }
        String s2 = s != null ? s.getText() : null;

        if (s2 == null) return null;

        String label = "[" + element.getContainingFile().getName() + "] \r\n";

        return label + s2.split("[\n\r]")[0];
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof LuaExpression) {
            StringBuilder result = new StringBuilder();

            if (element instanceof LuaNamedElement) {
                result.append("Name: ").append(((LuaNamedElement) element).getName()).append("  ").append(element.getTextRange()).append("<br/>");

                if (element instanceof LuaGlobal)
                    result.append("Global Name: ").append(((LuaGlobal) element).getGlobalEnvironmentName()).append(
                            "<br/>");

                if (element instanceof LuaAlias)
                    result.append("Aliases: ").append(((LuaAlias) element).getAliasElement())
                          .append("<br/>");

            } else
                result.append("Text: ").append(element.getText()).append("<br/>");

            result.append("Type: ").append(((LuaExpression) element).getLuaType()).append("<br/>");

            if (element != originalElement)
                result.append("Original Element: ").append(originalElement.getText()).append("  ").append(originalElement.getTextRange()).append("<br/>");

            return result.toString();
        }

        return null;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
