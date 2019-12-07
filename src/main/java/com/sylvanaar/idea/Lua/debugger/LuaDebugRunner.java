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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.sylvanaar.idea.Lua.run.LuaRunConfiguration;
import consulo.ui.annotation.RequiredUIAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/19/11
 * Time: 6:42 PM
 */
public class LuaDebugRunner extends GenericProgramRunner
{
	@Nullable
	@Override
	@RequiredUIAccess
	protected RunContentDescriptor doExecute(@Nonnull RunProfileState state, @Nonnull ExecutionEnvironment env) throws ExecutionException
	{
		FileDocumentManager.getInstance().saveAllDocuments();

		final ExecutionResult executionResult = state.execute(env.getExecutor(), this);

		XDebugSession session = XDebuggerManager.getInstance(env.getProject()).startSession(env, it -> new LuaDebugProcess(it, executionResult));

		return session.getRunContentDescriptor();
	}

	@Nonnull
	@Override
	public String getRunnerId()
	{
		return "com.sylvanaar.idea.Lua.debugger.LuaDebugRunner";
	}

	@Override
	public boolean canRun(@Nonnull java.lang.String executorId, @Nonnull RunProfile profile)
	{
		return executorId.equals(DefaultDebugExecutor.EXECUTOR_ID) && profile instanceof LuaRunConfiguration;
	}
}
