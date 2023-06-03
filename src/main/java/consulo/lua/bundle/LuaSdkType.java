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

import com.sylvanaar.idea.lua.LuaIcons;
import com.sylvanaar.idea.lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.lua.run.lua.LuaCommandLineState;
import consulo.container.plugin.PluginManager;
import consulo.content.OrderRootType;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkModificator;
import consulo.content.bundle.SdkType;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.process.ExecutionException;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author VISTALL
 * @since 10.03.2015
 */
public abstract class LuaSdkType extends SdkType {
    public LuaSdkType(@NonNls String name) {
        super(name);
    }

    @Nonnull
    public abstract LuaCommandLineState createCommandLinState(LuaRunConfiguration luaRunConfiguration, ExecutionEnvironment env,
                                                              boolean isDebugger) throws ExecutionException;

    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return type == BinariesOrderRootType.getInstance();
    }

    @Override
    public void setupSdkPaths(Sdk sdk) {
        SdkModificator sdkModificator = sdk.getSdkModificator();

        VirtualFile stdlibrary = LocalFileSystem.getInstance().findFileByIoFile(getStdLibraryDirectory());
        if (stdlibrary != null) {
            sdkModificator.addRoot(stdlibrary, BinariesOrderRootType.getInstance());
        }
        sdkModificator.commitChanges();
    }

    @Nonnull
    public File getStdLibraryDirectory() {
        return new File(PluginManager.getPluginPath(LuaSdkType.class), "stdlibrary");
    }

    @Override
    @Nonnull
    public Image getIcon() {
        return LuaIcons.LUA_ICON;
    }
}
