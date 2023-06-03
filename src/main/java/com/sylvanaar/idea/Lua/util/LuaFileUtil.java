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

package com.sylvanaar.idea.Lua.util;

import com.sylvanaar.idea.Lua.LuaFileType;
import consulo.container.plugin.PluginManager;
import consulo.content.ContentIterator;
import consulo.util.io.FileUtil;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileFilter;
import consulo.virtualFileSystem.internal.matcher.ExtensionFileNameMatcher;
import consulo.virtualFileSystem.util.VirtualFileUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Maxim.Manuylov
 * Date: 07.04.2010
 */
public class LuaFileUtil {
    @Nonnull
    public static String getPathToDisplay(final VirtualFile file) {
        if (file == null) {
            return "";
        }
        return FileUtil.toSystemDependentName(file.getPath());
    }


    @Nullable
    public static VirtualFile getPluginVirtualDirectory() {
        return LocalFileSystem.getInstance().findFileByIoFile(PluginManager.getPluginPath(LuaFileUtil.class));
    }

    public static boolean iterateRecursively(@Nullable final VirtualFile root, @Nonnull final ContentIterator processor) {
        return root != null && VirtualFileUtil.iterateChildrenRecursively(root, VirtualFileFilter.ALL, processor);
    }

    public static boolean iterateLuaFilesRecursively(@Nullable final VirtualFile root, @Nonnull final ContentIterator processor) {
        return root != null && VirtualFileUtil.iterateChildrenRecursively(root, LUA_FILE_FILTER, processor);
    }

    static VirtualFileFilter LUA_FILE_FILTER = new VirtualFileFilter() {
        @Override
        public boolean accept(VirtualFile file) {
            if (file.isDirectory()) {
                return true;
            }

            for (ExtensionFileNameMatcher matcher : LuaFileType.EXTENSION_FILE_NAME_MATCHERS) {
                if (matcher.accept(file.getName())) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return "LUA";
        }
    };
}
