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

package com.sylvanaar.idea.Lua.run.luaj;

import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.Lua.run.lua.LuaCommandLineState;
import com.sylvanaar.idea.Lua.util.LuaFileUtil;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.process.cmd.GeneralCommandLine;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Aug 28, 2010
 * Time: 6:35:19 PM
 */
public class LuaJExternalCommandLineState extends LuaCommandLineState
{
	public LuaJExternalCommandLineState(RunConfiguration runConfiguration, ExecutionEnvironment env)
	{
		super(runConfiguration, env);
	}

	@Override
	protected GeneralCommandLine generateCommandLine()
	{

		GeneralCommandLine commandLine = new GeneralCommandLine();
		final LuaRunConfiguration cfg = (LuaRunConfiguration) getRunConfiguration();

		commandLine.setExePath("java");

		commandLine.addParameters("-cp", getLuaJJarFile().getPath(), "lua");

		return configureCommandLine(commandLine);
	}

	@Nullable
	public static VirtualFile getLuaJJarFile()
	{
		final VirtualFile directory = LuaFileUtil.getPluginVirtualDirectory();
		if(directory != null)
		{
			final VirtualFile lib = directory.findChild("lib");
			if(lib != null)
			{
				VirtualFile[] children = lib.getChildren();
				if(children != null)
				{
					for(VirtualFile child : children)
					{
						if(child.getName().startsWith("luaj-jse"))
						{
							return child;
						}
					}
				}
			}
		}
		return null;
	}

}
