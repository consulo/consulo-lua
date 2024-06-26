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

import consulo.codeEditor.Editor;
import consulo.language.editor.AutoPopupController;
import consulo.language.editor.completion.lookup.InsertHandler;
import consulo.language.editor.completion.lookup.InsertionContext;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.editor.completion.lookup.LookupItem;
import consulo.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/13/12
 * Time: 10:15 PM
 */
public class LuaInsertHandler implements InsertHandler<LookupElement>
{
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        if (item instanceof LookupItem) {
            final char completionChar = context.getCompletionChar();

            if (completionChar == ':'  || completionChar == '.') {
                final Project project = context.getProject();
                final Editor editor = context.getEditor();
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
            }
        }
    }
}
