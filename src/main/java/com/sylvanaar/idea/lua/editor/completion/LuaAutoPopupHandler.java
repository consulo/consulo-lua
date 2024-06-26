/*
 * Copyright 2012 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.editor.completion;

import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.AutoPopupController;
import consulo.language.editor.action.TypedHandlerDelegate;
import consulo.language.psi.PsiFile;
import consulo.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/15/12
 * Time: 4:49 PM
 */
@ExtensionImpl
public class LuaAutoPopupHandler extends TypedHandlerDelegate {
    @Override
    public Result checkAutoPopup(char charTyped, Project project, Editor editor, PsiFile file) {
        if (!(file instanceof LuaPsiFile)) {
            return Result.CONTINUE;
        }

        if (charTyped == ':' || charTyped == '.') {
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
            return Result.STOP;
        }

        return Result.CONTINUE;
    }
}
