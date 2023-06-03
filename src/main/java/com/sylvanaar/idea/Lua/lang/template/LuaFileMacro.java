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

package com.sylvanaar.idea.Lua.lang.template;

import com.sylvanaar.idea.Lua.lang.psi.LuaPsiFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.ExpressionContext;
import consulo.language.editor.template.Result;
import consulo.language.editor.template.TextResult;
import consulo.language.editor.template.macro.Macro;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/25/11
 * Time: 1:35 PM
 */
@ExtensionImpl
public class LuaFileMacro extends Macro {
    @Override
    public String getName() {
        return "currentLuaFile";
    }

    @Override
    public String getPresentableName() {
        return "currentLuaFile()";
    }

    @Nonnull
    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public Result calculateResult(@Nonnull Expression[] expressions, ExpressionContext expressionContext) {
        PsiFile file = PsiDocumentManager.getInstance(
                expressionContext.getProject()).getPsiFile(expressionContext.getEditor().getDocument());

        if (file instanceof LuaPsiFile) {
            return new TextResult(file.getName());
        }

        return null;
    }

    @Override
    public Result calculateQuickResult(@Nonnull Expression[] expressions, ExpressionContext expressionContext) {
        return null;
    }

    @Override
    public LookupElement[] calculateLookupItems(@Nonnull Expression[] expressions, ExpressionContext expressionContext) {
        return new LookupElement[0];
    }
}
