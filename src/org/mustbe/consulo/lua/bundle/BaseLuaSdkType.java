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

package org.mustbe.consulo.lua.bundle;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.consulo.lombok.annotations.LazyInstance;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.SmartList;
import com.sylvanaar.idea.Lua.run.LuaDebugCommandlineState;
import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.Lua.run.lua.LuaCommandLineState;
import com.sylvanaar.idea.Lua.util.LuaSystemUtil;

/**
 * @author Maxim.Manuylov
 *         Date: 03.04.2010
 */
public class BaseLuaSdkType extends LuaSdkType
{
	@NotNull
	@LazyInstance
	public static BaseLuaSdkType getInstance()
	{
		return EP_NAME.findExtension(BaseLuaSdkType.class);
	}

	public BaseLuaSdkType()
	{
		super("LUA_SDK");
	}

	@NotNull
	@Override
	public Collection<String> suggestHomePaths()
	{
		List<String> list = new SmartList<String>();
		if(SystemInfo.isWindows)
		{
			list.add("C:\\Lua");
		}
		else if(SystemInfo.isLinux)
		{
			list.add("/usr/bin");
		}
		return list;
	}

	@Override
	public boolean canCreatePredefinedSdks()
	{
		return true;
	}

	@Override
	public boolean isValidSdkHome(@NotNull final String path)
	{
		final File lua = getTopLevelExecutable(path);
		// final File luac = getByteCodeCompilerExecutable(path);

		return lua.canExecute();// && luac.canExecute();
	}

	@NotNull
	public static File getTopLevelExecutable(@NotNull final String sdkHome)
	{
		File executable = getExecutable(sdkHome, "lua");
		if(executable.canExecute())
		{
			return executable;
		}

		executable = getExecutable(sdkHome, "lua5.1");
		if(executable.canExecute())
		{
			return executable;
		}

		executable = getExecutable(sdkHome, "luajit");
		if(executable.canExecute())
		{
			return executable;
		}

		executable = getExecutable(sdkHome, "murgalua");

		return executable;
	}

	@NotNull
	public static File getByteCodeCompilerExecutable(@NotNull final String sdkHome)
	{
		File executable = getExecutable(sdkHome, "luac");
		if(executable.canExecute())
		{
			return executable;
		}

		executable = getExecutable(sdkHome, "luac5.1");

		return executable;
	}

	@Override
	@NotNull
	public String suggestSdkName(@Nullable final String currentSdkName, @NotNull final String sdkHome)
	{
		String[] version = getExecutableVersionOutput(sdkHome);
		if(version == null)
		{
			return "Unknown at " + sdkHome;
		}
		return version[0] + " " + version[1];
	}

	@Override
	@Nullable
	public String getVersionString(@NotNull final String sdkHome)
	{
		return getExecutableVersionOutput(sdkHome)[1];
	}

	private String[] getExecutableVersionOutput(String sdkHome)
	{
		final String exePath = getTopLevelExecutable(sdkHome).getAbsolutePath();
		final ProcessOutput processOutput;
		try
		{
			processOutput = LuaSystemUtil.getProcessOutput(sdkHome, exePath, "-v");
		}
		catch(final ExecutionException e)
		{
			return null;
		}
		if(processOutput.getExitCode() != 0)
		{
			return null;
		}
		final String stdout = processOutput.getStderr().trim();
		if(stdout.isEmpty())
		{
			return null;
		}

		return stdout.split(" ");
	}

	@NotNull
	@Override
	@NonNls
	public String getPresentableName()
	{
		return "Lua SDK";
	}

	@NotNull
	private static File getExecutable(@NotNull final String path, @NotNull final String command)
	{
		return new File(path, SystemInfo.isWindows ? command + ".exe" : command);
	}

	@NotNull
	@Override
	public LuaCommandLineState createCommandLinState(LuaRunConfiguration luaRunConfiguration, ExecutionEnvironment env, boolean isDebugger)
	{
		if(isDebugger)
		{
			return new LuaDebugCommandlineState(luaRunConfiguration, env);
		}
		return new LuaCommandLineState(luaRunConfiguration, env);
	}
}