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

package com.sylvanaar.idea.lua.editor.highlighter;

import com.sylvanaar.idea.lua.LuaFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.wolfAnalyzer.WolfFileProblemFilter;
import consulo.virtualFileSystem.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 11/7/12
 * Time: 12:58 AM
 */
@ExtensionImpl
public class LuaProblemFileHighlightFilter implements WolfFileProblemFilter {
    @Override
    public boolean isToBeHighlighted(VirtualFile virtualFile) {
        return virtualFile.getFileType() == LuaFileType.LUA_FILE_TYPE;
    }
}
