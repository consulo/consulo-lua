/*
 * Copyright 2013-2015 must-be.org
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

package consulo.lua.bundle;

import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.Lua.run.kahlua.KahluaCommandLineState;
import com.sylvanaar.idea.Lua.run.lua.LuaCommandLineState;
import consulo.annotation.component.ExtensionImpl;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.process.ExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 10.03.2015
 */
@ExtensionImpl
public class KahluaSdkType extends LuaSdkType
{
	@Nonnull
	public static KahluaSdkType getInstance()
	{
		return EP_NAME.findExtension(KahluaSdkType.class);
	}

	public KahluaSdkType()
	{
		super("KAHLUA_SDK");
	}

	@Override
	public boolean supportsUserAdd()
	{
		return false;
	}

	@Override
	public boolean isValidSdkHome(String path)
	{
		return false;
	}

	@Nullable
	@Override
	public String getVersionString(String sdkHome)
	{
		return null;
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		return null;
	}

	@Nonnull
	@Override
	public String getPresentableName()
	{
		return "Kahlua";
	}

	@Nonnull
	@Override
	public LuaCommandLineState createCommandLinState(LuaRunConfiguration luaRunConfiguration,
			ExecutionEnvironment env,
			boolean isDebugger) throws ExecutionException
	{
		if(isDebugger)
		{
			throw new ExecutionException("Debugger is not supported");
		} return new KahluaCommandLineState(luaRunConfiguration, env);
	}
}
