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

package com.sylvanaar.idea.lua.refactoring;

import consulo.language.editor.refactoring.rename.NameSuggestionProvider;
import consulo.language.editor.refactoring.rename.SuggestedNameInfo;
import consulo.language.psi.PsiElement;
import jakarta.annotation.Nullable;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 7/9/11
 * Time: 6:13 AM
 */
public class LuaNameSuggestionProvider implements NameSuggestionProvider {
    @Override
    public SuggestedNameInfo getSuggestedNames(final PsiElement element, @Nullable PsiElement nameSuggestionContext,
											   Set<String> result) {
        if (nameSuggestionContext == null) nameSuggestionContext = element;

        

        return null;
    }
}