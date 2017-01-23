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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.Lua.run.lua.LuaCommandLineState;
import com.sylvanaar.idea.Lua.run.luaj.LuaJExternalCommandLineState;

/**
 * @author VISTALL
 * @since 10.03.2015
 */
public class LuaJSdkType extends LuaSdkType
{
	@NotNull
	public static LuaJSdkType getInstance()
	{
		return EP_NAME.findExtension(LuaJSdkType.class);
	}

	public LuaJSdkType()
	{
		super("LUAJ_SDK");
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

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "LuaJ";
	}

	@NotNull
	@Override
	public LuaCommandLineState createCommandLinState(LuaRunConfiguration luaRunConfiguration,
			ExecutionEnvironment env,
			boolean isDebugger) throws ExecutionException
	{
		if(isDebugger)
		{
			throw new ExecutionException("Debugger is not supported");
		}
		return new LuaJExternalCommandLineState(luaRunConfiguration, env);
	}
}
