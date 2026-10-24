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
package com.sylvanaar.idea.lua.run;

import com.sylvanaar.idea.lua.run.lua.LuaCommandLineState;
import com.sylvanaar.idea.lua.util.LuaModuleUtil;
import consulo.content.bundle.Sdk;
import consulo.execution.CommonProgramRunConfigurationParameters;
import consulo.execution.RuntimeConfigurationException;
import consulo.execution.configuration.*;
import consulo.execution.configuration.fragment.EnvironmentFragmentState;
import consulo.execution.configuration.fragment.ProgramParametersFragmentState;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.WorkingDirectoryFragmentState;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.console.TextConsoleBuilder;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.lua.bundle.BaseLuaSdkType;
import consulo.lua.bundle.LuaSdkType;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ProjectRootManager;
import consulo.process.ExecutionException;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LuaRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonProgramRunConfigurationParameters
{
	public LuaRunConfiguration(RunConfigurationModule runConfigurationModule, ConfigurationFactory configurationFactory, String name)
	{
		super(name, runConfigurationModule, configurationFactory);
	}

	@Override
	protected Class<LuaRunConfigurationOptions> getOptionsClass()
	{
		return LuaRunConfigurationOptions.class;
	}

	@Override
	public LuaRunConfigurationOptions getOptions()
	{
		return (LuaRunConfigurationOptions) super.getOptions();
	}

	@Override
	protected void convertLegacyState(LegacyRunConfigurationConverter converter)
	{
		super.convertLegacyState(converter);

		LuaRunConfigurationLegacyState old = converter.readXml(LuaRunConfigurationLegacyState.class);

		LuaRunConfigurationOptions options = new LuaRunConfigurationOptions();
		options.setScriptName(old.SCRIPT_NAME);
		options.setInterpreterOptions(old.INTERPRETER_OPTIONS);
		options.setInterpreterPath(old.ALTERNATE_INTERPRETER ? old.INTERPRETER_PATH : null);
		converter.setOptions(options);

		converter.addFragment(new ProgramParametersFragmentState(old.PARAMETERS));
		converter.addFragment(new WorkingDirectoryFragmentState(old.WORKING_DIRECTORY));
		converter.addFragment(new EnvironmentFragmentState(converter.readEnvs(), old.PARENT_ENVS));
	}

	@Override
	public List<RunConfigurationFragment<?>> createFragments()
	{
		return List.of(new LuaScriptFragment(), new LuaInterpreterFragment(), new LuaInterpreterOptionsFragment());
	}

	@Override
	public boolean excludeCompileBeforeLaunchOption()
	{
		return true;
	}

	@Override
	public RunProfileState getState(@Nonnull final Executor executor, @Nonnull final ExecutionEnvironment env) throws ExecutionException
	{
		Sdk sdk = getSdk();
		if(sdk == null)
		{
			throw new ExecutionException("SDK if not defined");
		}
		final boolean isDebugger = executor.getId().equals(DefaultDebugExecutor.EXECUTOR_ID);

		LuaSdkType sdkType = (LuaSdkType) sdk.getSdkType();

		LuaCommandLineState state = sdkType.createCommandLinState(this, env, isDebugger);
		if(!(sdkType instanceof BaseLuaSdkType))
		{
			if(isDebugger)
			{
				throw new ExecutionException("Debugging not supported for SDK " + sdk.getName() + ". Please configure a real Lua SDK.");
			}
		}

		TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(getProject());
		textConsoleBuilder.addFilter(new LuaLineErrorFilter(getProject()));

		state.setConsoleBuilder(textConsoleBuilder);
		return state;
	}

	public Sdk getSdk()
	{
		return LuaModuleUtil.findLuaSdk(getConfigurationModule().getModule());
	}

	@Override
	public void checkConfiguration() throws RuntimeConfigurationException
	{
		super.checkConfiguration();

		String interpreterPath = getInterpreterPath();
		if(interpreterPath != null)
		{
			File interpreterFile = new File(interpreterPath);
			if(!interpreterFile.isFile() || !interpreterFile.canRead())
			{
				throw new RuntimeConfigurationException("Interpreter path is invalid or not readable.");
			}
		}

		String name = getScriptName();
		if(StringUtil.isEmptyOrSpaces(name))
		{
			throw new RuntimeConfigurationException("No script name given.");
		}

		final String dir = getWorkingDirectory();
		if(StringUtil.isNotEmpty(dir))
		{
			name = dir + '/' + name;
		}
		final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(name);

		if(file == null)
		{
			throw new RuntimeConfigurationException("Script file does not exist");
		}

		final ProjectRootManager rootManager = ProjectRootManager.getInstance(getProject());

		if(!rootManager.getFileIndex().isInContent(file))
		{
			throw new RuntimeConfigurationException("File is not in the current project");
		}
	}

	@Nullable
	public String getScriptName()
	{
		return getOptions().getScriptName();
	}

	public void setScriptName(@Nullable String scriptName)
	{
		getOptions().setScriptName(scriptName);
	}

	@Nullable
	public String getInterpreterOptions()
	{
		return getOptions().getInterpreterOptions();
	}

	public void setInterpreterOptions(@Nullable String interpreterOptions)
	{
		getOptions().setInterpreterOptions(interpreterOptions);
	}

	/**
	 * @return the interpreter to run instead of the one of the module SDK, {@code null} for the module SDK
	 */
	@Nullable
	public String getInterpreterPath()
	{
		return getOptions().getInterpreterPath();
	}

	public void setInterpreterPath(@Nullable String path)
	{
		getOptions().setInterpreterPath(path);
	}

	@Override
	public Collection<Module> getValidModules()
	{
		Module[] allModules = ModuleManager.getInstance(getProject()).getModules();
		return Arrays.asList(allModules);
	}

	@Override
	protected ModuleBasedConfiguration createInstance()
	{
		return new LuaRunConfiguration(getConfigurationModule(), getFactory(), getName());
	}
}
