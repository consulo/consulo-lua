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

package com.sylvanaar.idea.lua.lang;

import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.document.Document;
import consulo.language.editor.action.TypedHandlerDelegate;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.virtualFileSystem.fileType.FileType;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/30/11
 * Time: 6:45 AM
 */
@ExtensionImpl
public class LuaTypedInsideBlockDelegate extends TypedHandlerDelegate {
    boolean preserveParen = false;

    @Override
    public Result beforeCharTyped(char c, Project project, Editor editor, PsiFile file, FileType fileType) {
        if (!(file instanceof LuaPsiFile)) {
            return Result.CONTINUE;
        }
        if (c != ')' && c != '(') {
            return Result.CONTINUE;
        }

        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement e1 = file.findElementAt(caretOffset);

        preserveParen = (e1 != null && e1.getText().equals(")"));

        return super.beforeCharTyped(c, project, editor, file,
                fileType);
    }

    @Override
    public Result charTyped(char c, final Project project, final Editor editor, final PsiFile file) {
        if (!(file instanceof LuaPsiFile)) {
            return Result.CONTINUE;
        }
        if (c != ')' && c != '(') {
            return Result.CONTINUE;
        }

        Document document = editor.getDocument();
        int caretOffset = editor.getCaretModel().getOffset();

        PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);

        PsiElement e = file.findElementAt(caretOffset - 1);

        if (!(e instanceof PsiComment)) {

            PsiElement e1 = file.findElementAt(caretOffset);
//            PsiElement e2 = file.findElementAt(caretOffset+1);

            // This handles the case where we are already inside parens.
            // for example a(b,c function(|) where | is the cursor
            if (c == '(' && e1 != null && e1.getText().equals(")")) {
                e = e1;
                c = ')';
            }

            if (c == ')' && e != null && e.getContext() instanceof LuaFunctionDefinition) {
                document.insertString(e.getTextOffset() + 1, preserveParen ? " end)" : " end");
                return Result.STOP;
            }
        }
        return super.charTyped(c, project, editor, file);
    }
}
