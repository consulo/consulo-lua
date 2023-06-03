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

package com.sylvanaar.idea.Lua.lang.luadoc.completion;

import com.sylvanaar.idea.Lua.lang.LuaLanguage;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.LuaDocTag;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.AddSpaceInsertHandler;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.StandardPatterns;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class LuaDocCompletionData extends CompletionContributor {
    private static final String[] DOC_TAGS =
            {"author", "copyright", "field", "param", "release", "return", "see", "usage", "class",
                    "description", "name"
            };

    public LuaDocCompletionData() {
        extend(CompletionType.BASIC, StandardPatterns.psiElement().withParent(LuaDocTag.class), (completionParameters, processingContext, completionResultSet) -> {
            for (String docTag : DOC_TAGS) {
                completionResultSet.accept(LookupElementBuilder.create(docTag).withInsertHandler(AddSpaceInsertHandler.INSTANCE));
            }
        });
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
