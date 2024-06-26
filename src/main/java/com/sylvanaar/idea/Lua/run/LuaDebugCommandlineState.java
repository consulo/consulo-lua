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

package com.sylvanaar.idea.lua.run;

import com.sylvanaar.idea.lua.run.lua.LuaCommandLineState;
import com.sylvanaar.idea.lua.sdk.*;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.cmd.ParametersList;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/26/11
 * Time: 1:58 AM
 */
public class LuaDebugCommandlineState extends LuaCommandLineState {
    public LuaDebugCommandlineState(RunConfiguration runConfiguration, ExecutionEnvironment env) {
        super(runConfiguration, env);
    }

    @Override
    protected GeneralCommandLine configureCommandLine(GeneralCommandLine commandLine) {
        final LuaRunConfigurationParams configuration = (LuaRunConfigurationParams) getRunConfiguration();

        // '%s -e "package.path=%s" -l debug %s'
        // TODO: can we use any of the arguments? commandLine.getParametersList().addParametersString(getRunConfiguration().getInterpreterOptions());

        final String remDebugPath = StdLibrary.getDebugModuleLocation().getPath();
        final ParametersList params = commandLine.getParametersList();

        params.addParametersString("-e");
        params.add("package.path=[[" + remDebugPath + "/?.lua;]]  ..  package.path");
        params.addParametersString("-l remdebug");

        params.addParametersString(configuration.getScriptName());

        return commandLine;
    }
}
