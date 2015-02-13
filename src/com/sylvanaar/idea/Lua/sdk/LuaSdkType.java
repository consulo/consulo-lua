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

package com.sylvanaar.idea.Lua.sdk;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;

import org.consulo.lua.module.extension.LuaModuleExtension;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.types.BinariesOrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.SmartList;
import com.sylvanaar.idea.Lua.LuaIcons;
import com.sylvanaar.idea.Lua.util.LuaSystemUtil;

/**
 * @author Maxim.Manuylov
 *         Date: 03.04.2010
 */
public class LuaSdkType extends SdkType
{
	@NotNull
	public static LuaSdkType getInstance()
	{
		return EP_NAME.findExtension(LuaSdkType.class);
	}

	public LuaSdkType()
	{
		super("LUA_SDK");
	}

	@Override
	@NotNull
	public Icon getIcon()
	{
		return LuaIcons.LUA_ICON;
	}

	@Nullable
	@Override
	public Icon getGroupIcon()
	{
		return getIcon();
	}

	public static Sdk findLuaSdk(Module module)
	{
		if(module == null)
		{
			return null;
		}

		return ModuleUtilCore.getSdk(module, LuaModuleExtension.class);
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

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == BinariesOrderRootType.getInstance();
	}

	@Override
	public void setupSdkPaths(@NotNull final Sdk sdk)
	{
		final SdkModificator sdkModificator = sdk.getSdkModificator();

		sdkModificator.addRoot(StdLibrary.getStdFileLocation(), BinariesOrderRootType.getInstance());

		sdkModificator.commitChanges();
	}

	@NotNull
	private static File getExecutable(@NotNull final String path, @NotNull final String command)
	{
		return new File(path, SystemInfo.isWindows ? command + ".exe" : command);
	}
}