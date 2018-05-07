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

import java.io.File;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.sylvanaar.idea.Lua.LuaIcons;
import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.Lua.run.lua.LuaCommandLineState;
import consulo.roots.types.BinariesOrderRootType;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 10.03.2015
 */
public abstract class LuaSdkType extends SdkType
{
	public LuaSdkType(@NonNls String name)
	{
		super(name);
	}

	@NotNull
	public abstract LuaCommandLineState createCommandLinState(LuaRunConfiguration luaRunConfiguration, ExecutionEnvironment env,
			boolean isDebugger) throws ExecutionException;

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == BinariesOrderRootType.getInstance();
	}

	@Override
	public void setupSdkPaths(Sdk sdk)
	{
		SdkModificator sdkModificator = sdk.getSdkModificator();

		VirtualFile stdlibrary = LocalFileSystem.getInstance().findFileByIoFile(getStdLibraryFile());
		if(stdlibrary != null)
		{
			sdkModificator.addRoot(stdlibrary, BinariesOrderRootType.getInstance());
		}
		sdkModificator.commitChanges();
	}

	@NotNull
	public File getStdLibraryFile()
	{
		PluginClassLoader classLoader = (PluginClassLoader) getClass().getClassLoader();

		IdeaPluginDescriptor plugin = PluginManager.getPlugin(classLoader.getPluginId());

		assert plugin != null;

		return new File(plugin.getPath(), "stdlibrary");
	}

	@Override
	@NotNull
	public Image getIcon()
	{
		return LuaIcons.LUA_ICON;
	}
}
