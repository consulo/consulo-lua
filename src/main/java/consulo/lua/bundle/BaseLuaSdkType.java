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

package consulo.lua.bundle;

import com.sylvanaar.idea.Lua.run.LuaDebugCommandlineState;
import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.Lua.run.lua.LuaCommandLineState;
import com.sylvanaar.idea.Lua.util.LuaSystemUtil;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.util.SystemInfo;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.process.ExecutionException;
import consulo.process.local.ProcessOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Maxim.Manuylov
 * Date: 03.04.2010
 */
@ExtensionImpl
public class BaseLuaSdkType extends LuaSdkType {
    @Nonnull
    public static BaseLuaSdkType getInstance() {
        return EP_NAME.findExtensionOrFail(BaseLuaSdkType.class);
    }

    public BaseLuaSdkType() {
        super("LUA_SDK");
    }

    @Nonnull
    @Override
    public Collection<String> suggestHomePaths() {
        List<String> list = new ArrayList<>();
        if (SystemInfo.isWindows) {
            list.add("C:\\Lua");
        }
        else if (SystemInfo.isLinux) {
            list.add("/usr/bin");
        }
        return list;
    }

    @Override
    public boolean canCreatePredefinedSdks() {
        return true;
    }

    @Override
    public boolean isValidSdkHome(@Nonnull final String path) {
        final File lua = getTopLevelExecutable(path);
        // final File luac = getByteCodeCompilerExecutable(path);

        return lua.canExecute();// && luac.canExecute();
    }

    @Nonnull
    public static File getTopLevelExecutable(@Nonnull final String sdkHome) {
        File executable = getExecutable(sdkHome, "lua");
        if (executable.canExecute()) {
            return executable;
        }

        executable = getExecutable(sdkHome, "lua5.1");
        if (executable.canExecute()) {
            return executable;
        }

        executable = getExecutable(sdkHome, "luajit");
        if (executable.canExecute()) {
            return executable;
        }

        executable = getExecutable(sdkHome, "murgalua");

        return executable;
    }

    @Nonnull
    public static File getByteCodeCompilerExecutable(@Nonnull final String sdkHome) {
        File executable = getExecutable(sdkHome, "luac");
        if (executable.canExecute()) {
            return executable;
        }

        executable = getExecutable(sdkHome, "luac5.1");

        return executable;
    }

    @Override
    @Nonnull
    public String suggestSdkName(@Nullable final String currentSdkName, @Nonnull final String sdkHome) {
        String[] version = getExecutableVersionOutput(sdkHome);
        if (version == null) {
            return "Unknown at " + sdkHome;
        }
        return version[0] + " " + version[1];
    }

    @Override
    @Nullable
    public String getVersionString(@Nonnull final String sdkHome) {
        String[] output = getExecutableVersionOutput(sdkHome);
        if (output == null) {
            return null;
        }
        return output[1];
    }

    private String[] getExecutableVersionOutput(String sdkHome) {
        final String exePath = getTopLevelExecutable(sdkHome).getAbsolutePath();
        final ProcessOutput processOutput;
        try {
            processOutput = LuaSystemUtil.getProcessOutput(sdkHome, exePath, "-v");
        }
        catch (final ExecutionException e) {
            return null;
        }
        if (processOutput.getExitCode() != 0) {
            return null;
        }
        final String stdout = processOutput.getStderr().trim();
        if (stdout.isEmpty()) {
            return null;
        }

        return stdout.split(" ");
    }

    @Nonnull
    @Override
    public String getPresentableName() {
        return "Lua SDK";
    }

    @Nonnull
    private static File getExecutable(@Nonnull final String path, @Nonnull final String command) {
        return new File(path, SystemInfo.isWindows ? command + ".exe" : command);
    }

    @Nonnull
    @Override
    public LuaCommandLineState createCommandLinState(LuaRunConfiguration luaRunConfiguration, ExecutionEnvironment env, boolean isDebugger) {
        if (isDebugger) {
            return new LuaDebugCommandlineState(luaRunConfiguration, env);
        }
        return new LuaCommandLineState(luaRunConfiguration, env);
    }
}