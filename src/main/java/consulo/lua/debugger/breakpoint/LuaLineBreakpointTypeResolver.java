/*
 * Copyright 2013-2016 must-be.org
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

package consulo.lua.debugger.breakpoint;

import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.debugger.LuaLineBreakpointType;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaStatementElement;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.Document;
import consulo.execution.debug.breakpoint.XLineBreakpointType;
import consulo.execution.debug.breakpoint.XLineBreakpointTypeResolver;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
@ExtensionImpl
public class LuaLineBreakpointTypeResolver implements XLineBreakpointTypeResolver {
    @Nullable
    @Override
    @RequiredReadAction
    public XLineBreakpointType<?> resolveBreakpointType(@Nonnull Project project, @Nonnull VirtualFile virtualFile, int line) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);

        assert psiFile != null;

        if (!(psiFile instanceof LuaPsiFile)) {
            return null;
        }

        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);

        assert document != null;

        int start = document.getLineStartOffset(line);
        int end = document.getLineEndOffset(line);

        for (LuaStatementElement stat : ((LuaPsiFile) psiFile).getAllStatements()) {
            if (stat.getTextOffset() >= start && stat.getTextOffset() < end) {
                return LuaLineBreakpointType.getInstance();
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public FileType getFileType() {
        return LuaFileType.LUA_FILE_TYPE;
    }
}
