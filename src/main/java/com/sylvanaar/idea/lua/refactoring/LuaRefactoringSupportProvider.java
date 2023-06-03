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

package com.sylvanaar.idea.lua.refactoring;

// Does not work with version 9 and 10 at the same time


import com.sylvanaar.idea.lua.lang.LuaLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.refactoring.action.RefactoringActionHandler;
import com.sylvanaar.idea.lua.lang.psi.LuaNamedElement;
import com.sylvanaar.idea.lua.refactoring.introduce.LuaIntroduceVariableHandler;
import consulo.language.editor.refactoring.RefactoringSupportProvider;
import consulo.language.psi.PsiElement;
import jakarta.annotation.Nonnull;

/**
* Created by IntelliJ IDEA.
* User: Jon S Akhtar
* Date: Jun 12, 2010
* Time: 4:38:09 AM
*/
@ExtensionImpl
public class LuaRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isSafeDeleteAvailable(PsiElement element) {
        return element instanceof LuaNamedElement;
    }

    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new LuaIntroduceVariableHandler();
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
