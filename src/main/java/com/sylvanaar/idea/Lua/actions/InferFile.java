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

package com.sylvanaar.idea.lua.actions;

import consulo.language.editor.PlatformDataKeys;
import com.sylvanaar.idea.lua.lang.InferenceCapable;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiManager;
import consulo.language.editor.LangDataKeys;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/27/12
 * Time: 2:44 PM
 */
public class InferFile extends AnAction
{
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(LangDataKeys.PROJECT);
        if (project == null) return;
        
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(e.getData(PlatformDataKeys.EDITOR).getDocument());

        if (file instanceof LuaPsiFile)
            LuaPsiManager.getInstance(project).queueInferences((InferenceCapable) file);
    }
}
