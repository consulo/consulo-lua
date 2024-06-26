/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.lang.psi.search;

import javax.annotation.Nonnull;

import consulo.module.Module;
import consulo.module.content.ProjectRootManager;
import consulo.project.Project;
import consulo.module.content.ProjectFileIndex;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.scope.GlobalSearchScope;
import com.sylvanaar.idea.lua.util.LuaModuleUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/15/11
 * Time: 10:01 AM
 */
public class LuaSourceFilterScope extends GlobalSearchScope
{
    private final GlobalSearchScope myDelegate;
    private final ProjectFileIndex myIndex;

    public LuaSourceFilterScope(final GlobalSearchScope delegate, final Project project) {
        myDelegate = delegate;
        myIndex = ProjectRootManager.getInstance(project).getFileIndex();
    }

    public boolean contains(final VirtualFile file) {
        if (myDelegate != null && !myDelegate.contains(file)) {
            return false;
        }

        return LuaModuleUtil.isLuaContentFile(myIndex, file);
    }

    public int compare(final VirtualFile file1, final VirtualFile file2) {
        return myDelegate != null ? myDelegate.compare(file1, file2) : 0;
    }

    public boolean isSearchInModuleContent(@Nonnull final Module aModule) {
        return myDelegate == null || myDelegate.isSearchInModuleContent(aModule);
    }

    public boolean isSearchInLibraries() {
        return myDelegate == null || myDelegate.isSearchInLibraries();
    }
}
