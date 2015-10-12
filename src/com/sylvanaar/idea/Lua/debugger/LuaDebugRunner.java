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

package com.sylvanaar.idea.Lua.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/19/11
 * Time: 6:42 PM
 */
public class LuaDebugRunner extends GenericProgramRunner {
  private static final Logger log = Logger.getInstance("Lua.LuaDebugRunner");

  ExecutionResult executionResult;

  private final XDebugProcessStarter processStarter = new XDebugProcessStarter() {
    @NotNull
    @Override
    public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
      return new LuaDebugProcess(session, executionResult);
    }
  };

  @Nullable
  @Override
  @RequiredReadAction
  protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
    FileDocumentManager.getInstance().saveAllDocuments();

    if (log.isDebugEnabled()) log.debug("Starting LuaDebugProcess");

    executionResult = state.execute(env.getExecutor(), this);

    XDebugSession session = XDebuggerManager.getInstance(env.getProject()).startSession(env, processStarter);

    return session.getRunContentDescriptor();
  }

  @NotNull
  @Override
  public String getRunnerId() {
    return "com.sylvanaar.idea.Lua.debugger.LuaDebugRunner";
  }

  @Override
  public boolean canRun(@NotNull java.lang.String executorId, @NotNull RunProfile profile) {
    if (!(executorId.equals(DefaultDebugExecutor.EXECUTOR_ID) && profile instanceof LuaRunConfiguration))
      return false;

    return true;
  }
}
