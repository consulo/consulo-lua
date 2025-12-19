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

package com.sylvanaar.idea.lua.run.kahlua;

import com.sylvanaar.idea.lua.kahlua.KahLuaInterpreterWindowFactory;
import com.sylvanaar.idea.lua.run.LuaRunConfiguration;
import com.sylvanaar.idea.lua.run.LuaRunConfigurationParams;
import com.sylvanaar.idea.lua.run.lua.LuaCommandLineState;
import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.execution.ExecutionResult;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.ProgramRunner;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.TextConsoleBuilder;
import consulo.logging.Logger;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.project.ui.wm.ToolWindowManager;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Aug 28, 2010
 * Time: 6:35:19 PM
 */
public class KahluaCommandLineState extends LuaCommandLineState {
    private static final Logger log = Logger.getInstance("Lua.KahluaCommandLineState");

    public KahluaCommandLineState(LuaRunConfiguration runConfiguration, ExecutionEnvironment env) {
        super(runConfiguration, env);
    }

    public ExecutionResult execute(@Nonnull final Executor executor,
                                   @Nonnull ProgramRunner runner) throws ExecutionException {
        log.info("execute " + executor.getActionName());

        final ProcessHandler processHandler = startProcess();
        final TextConsoleBuilder builder = getConsoleBuilder();
        final ConsoleView console = builder != null ? builder.getConsole() : null;
        if (console != null) {
            console.attachToProcess(processHandler);
        }

        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(
                ((LuaRunConfigurationParams) getRunConfiguration()).getScriptName());

        final String text;
        if (file != null) {
            final Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                text = document.getText();
            }
            else {
                text = "";
            }
        }
        else {
            text = "";
        }

        if (KahLuaInterpreterWindowFactory.INSTANCE != null) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(getExecutionEnvironment().getProject()).getToolWindow(KahLuaInterpreterWindowFactory.ID);

            toolWindow.activate(KahLuaInterpreterWindowFactory.INSTANCE.getRunnableExecution(text), true);
        }

        return new KahluaExecutionResult(console, createActions(console, processHandler, executor));
    }

    @Nonnull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        log.info("startProcess");
        return new KahluaProcessHandler();
    }

}
