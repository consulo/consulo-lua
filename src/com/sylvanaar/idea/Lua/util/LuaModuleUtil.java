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

import consulo.lua.module.extension.LuaModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.sylvanaar.idea.Lua.LuaFileType;

/**
 * @author Maxim.Manuylov
 *         Date: 29.04.2010
 */
public class LuaModuleUtil
{
	public static Sdk findLuaSdk(Module module)
	{
		if(module == null)
		{
			return null;
		}

		return ModuleUtilCore.getSdk(module, LuaModuleExtension.class);
	}

	public static boolean isLuaContentFile(ProjectFileIndex myIndex, VirtualFile file)
	{
		final String extension = file.getExtension();
		if(extension == null)
		{
			return false;
		}

		return extension.equals(LuaFileType.DEFAULT_EXTENSION) && (myIndex.isInContent(file) || myIndex.isInLibraryClasses(file));
	}
}
