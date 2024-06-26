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

package com.sylvanaar.idea.lua.debugger;

import javax.annotation.Nonnull;

import consulo.execution.debug.XSourcePosition;
import consulo.logging.Logger;
import consulo.execution.debug.evaluation.XDebuggerEvaluator;
import consulo.project.Project;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/15/11
 * Time: 5:07 AM
 */
public class LuaDebuggerEvaluator extends XDebuggerEvaluator {
    private static final Logger log = Logger.getInstance("Lua.LuaDebuggerEvaluator");

    private Project myProject;
    private LuaStackFrame luaStackFrame;
    private LuaDebuggerController myController;

    public LuaDebuggerEvaluator(Project myProject, LuaStackFrame luaStackFrame, LuaDebuggerController myController) {

        this.myProject = myProject;
        this.luaStackFrame = luaStackFrame;
        this.myController = myController;
    }

    @Override
    public void evaluate(@Nonnull String expression, XEvaluationCallback callback,
						 @Nullable XSourcePosition expressionPosition) {

        log.debug("evaluating: " + expression);
        myController.execute(new LuaDebuggerController.CodeExecutionRequest("return " + expression, callback));
    }
}
