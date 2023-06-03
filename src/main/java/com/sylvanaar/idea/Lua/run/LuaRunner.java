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

package com.sylvanaar.idea.Lua.run;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.FileDocumentManager;
import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfile;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.executor.DefaultRunExecutor;
import consulo.execution.runner.DefaultProgramRunner;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.RunContentBuilder;
import consulo.execution.ui.RunContentDescriptor;
import consulo.process.ExecutionException;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 21, 2010
 * Time: 12:44:22 AM
 */
@ExtensionImpl
public class LuaRunner extends DefaultProgramRunner
{
  @Nonnull
  @Override
  public String getRunnerId() {
    return "com.sylvanaar.idea.Lua.run.LuaRunner";
  }

  @Override
  public boolean canRun(@Nonnull String executorId, @Nonnull RunProfile profile) {
    return executorId.equals(DefaultRunExecutor.EXECUTOR_ID) && profile instanceof LuaRunConfiguration;
  }

  @Override
  protected RunContentDescriptor doExecute(RunProfileState state, ExecutionEnvironment env) throws ExecutionException {
    FileDocumentManager.getInstance().saveAllDocuments();
    ExecutionResult executionResult = state.execute(env.getExecutor(), this);
    // if (executionResult == null) return null;

    final RunContentBuilder contentBuilder = new RunContentBuilder(executionResult, env);

    return contentBuilder.showRunContent(env.getContentToReuse());
  }
}
