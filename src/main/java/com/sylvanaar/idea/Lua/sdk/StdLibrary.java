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

package com.sylvanaar.idea.Lua.sdk;

import consulo.virtualFileSystem.VirtualFile;
import com.sylvanaar.idea.Lua.util.LuaFileUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/9/11
 * Time: 3:07 AM
 */
public class StdLibrary
{
	public static final String DEBUG_LIBRARY = "remdebug";
	public static final String LISTING_GENERATOR = "listing";

	public static VirtualFile getDebugModuleLocation()
	{
		VirtualFile dir = LuaFileUtil.getPluginVirtualDirectory();

		if(dir == null)
		{
			return null;
		}
		return dir.findChild(DEBUG_LIBRARY);
	}

	public static VirtualFile getListingModuleLocation()
	{
		VirtualFile dir = LuaFileUtil.getPluginVirtualDirectory();

		if(dir == null)
		{
			return null;
		}
		return dir.findChild(LISTING_GENERATOR);
	}
}
