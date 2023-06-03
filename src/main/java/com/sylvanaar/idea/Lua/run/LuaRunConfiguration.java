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
import consulo.execution.RuntimeConfigurationException;
import consulo.execution.configuration.*;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.awt.EnvironmentVariablesComponent;
import consulo.execution.ui.console.TextConsoleBuilder;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.lua.bundle.BaseLuaSdkType;
import consulo.lua.bundle.LuaSdkType;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ProjectRootManager;
import consulo.process.ExecutionException;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.InvalidDataException;
import consulo.util.xml.serializer.JDOMExternalizerUtil;
import consulo.util.xml.serializer.WriteExternalException;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import org.jdom.Element;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LuaRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonLuaRunConfigurationParams,
		LuaRunConfigurationParams
{
	// common config
	private String interpreterOptions = "";
	private String workingDirectory = "";
	private boolean passParentEnvs = true;
	private Map<String, String> envs = new HashMap<String, String>();

	private boolean overrideSDK = false;
	private String interpreterPath = "";

	// run config
	private String scriptName;
	private String scriptParameters;
	//    private boolean usingKahlua;
	//    private boolean usingLuaJ;

	public LuaRunConfiguration(RunConfigurationModule runConfigurationModule, ConfigurationFactory configurationFactory, String name)
	{
		super(name, runConfigurationModule, configurationFactory);
	}

	@Override
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		return new LuaRunConfigurationEditor(this);
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

	public static void copyParams(CommonLuaRunConfigurationParams from, CommonLuaRunConfigurationParams to)
	{
		to.setEnvs(new HashMap<String, String>(from.getEnvs()));
		to.setInterpreterOptions(from.getInterpreterOptions());
		to.setWorkingDirectory(from.getWorkingDirectory());
		to.setInterpreterPath(from.getInterpreterPath());
		to.setOverrideSDKInterpreter(from.isOverrideSDKInterpreter());
		//        to.setUsingLuaJInterpreter(from.isUsingLuaJInterpreter());
		//to.setPassParentEnvs(from.isPassParentEnvs());
	}

	public static void copyParams(LuaRunConfigurationParams from, LuaRunConfigurationParams to)
	{
		copyParams(from.getCommonParams(), to.getCommonParams());

		to.setScriptName(from.getScriptName());
		to.setScriptParameters(from.getScriptParameters());
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException
	{
		super.readExternal(element);

		// common config
		interpreterOptions = JDOMExternalizerUtil.readField(element, "INTERPRETER_OPTIONS");
		interpreterPath = JDOMExternalizerUtil.readField(element, "INTERPRETER_PATH");
		workingDirectory = JDOMExternalizerUtil.readField(element, "WORKING_DIRECTORY", getProject().getBasePath());

		String str = JDOMExternalizerUtil.readField(element, "PARENT_ENVS");
		if(str != null)
		{
			passParentEnvs = Boolean.parseBoolean(str);
		}
		str = JDOMExternalizerUtil.readField(element, "ALTERNATE_INTERPRETER");
		if(str != null)
		{
			overrideSDK = Boolean.parseBoolean(str);
		}
		EnvironmentVariablesComponent.readExternal(element, envs);

		// ???
		getConfigurationModule().readExternal(element);

		// run config
		scriptName = JDOMExternalizerUtil.readField(element, "SCRIPT_NAME");
		scriptParameters = JDOMExternalizerUtil.readField(element, "PARAMETERS");
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		super.writeExternal(element);

		// common config
		JDOMExternalizerUtil.writeField(element, "INTERPRETER_OPTIONS", interpreterOptions);
		JDOMExternalizerUtil.writeField(element, "INTERPRETER_PATH", interpreterPath);
		JDOMExternalizerUtil.writeField(element, "WORKING_DIRECTORY", workingDirectory);
		JDOMExternalizerUtil.writeField(element, "PARENT_ENVS", Boolean.toString(passParentEnvs));
		JDOMExternalizerUtil.writeField(element, "ALTERNATE_INTERPRETER", Boolean.toString(overrideSDK));

		EnvironmentVariablesComponent.writeExternal(element, envs);

		// ???
		getConfigurationModule().writeExternal(element);

		// run config
		JDOMExternalizerUtil.writeField(element, "SCRIPT_NAME", scriptName);
		JDOMExternalizerUtil.writeField(element, "PARAMETERS", scriptParameters);
	}


	@Override
	public void checkConfiguration() throws RuntimeConfigurationException
	{
		super.checkConfiguration();

		if(overrideSDK)
		{
			if(StringUtil.isEmptyOrSpaces(interpreterPath))
			{
				throw new RuntimeConfigurationException("No interpreter path given.");
			}

			File interpreterFile = new File(interpreterPath);
			if(!interpreterFile.isFile() || !interpreterFile.canRead())
			{
				throw new RuntimeConfigurationException("Interpreter path is invalid or not readable.");
			}
		}
		if(StringUtil.isEmptyOrSpaces(scriptName))
		{
			throw new RuntimeConfigurationException("No script name given.");
		}

		String name = getScriptName();
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

	@Override
	public String getInterpreterOptions()
	{
		return interpreterOptions;
	}

	@Override
	public void setInterpreterOptions(String interpreterOptions)
	{
		this.interpreterOptions = interpreterOptions;
	}

	@Override
	public String getWorkingDirectory()
	{
		return workingDirectory;
	}

	@Override
	public void setWorkingDirectory(String workingDirectory)
	{
		this.workingDirectory = workingDirectory;
	}

	public boolean isPassParentEnvs()
	{
		return passParentEnvs;
	}

	public void setPassParentEnvs(boolean passParentEnvs)
	{
		this.passParentEnvs = passParentEnvs;
	}

	@Override
	public Map<String, String> getEnvs()
	{
		return envs;
	}

	@Override
	public void setEnvs(Map<String, String> envs)
	{
		this.envs = envs;
	}

	@Override
	public String getInterpreterPath()
	{
		return interpreterPath;
	}

	@Override
	public void setInterpreterPath(String path)
	{
		this.interpreterPath = path;
	}

	@Override
	public CommonLuaRunConfigurationParams getCommonParams()
	{
		return this;
	}

	@Override
	public String getScriptName()
	{
		return scriptName;
	}

	@Override
	public void setScriptName(String scriptName)
	{
		this.scriptName = scriptName;
	}

	@Override
	public String getScriptParameters()
	{
		return scriptParameters;
	}

	@Override
	public void setScriptParameters(String scriptParameters)
	{
		this.scriptParameters = scriptParameters;
	}

	@Override
	public boolean isOverrideSDKInterpreter()
	{
		return this.overrideSDK;
	}

	@Override
	public void setOverrideSDKInterpreter(boolean b)
	{
		this.overrideSDK = b;
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