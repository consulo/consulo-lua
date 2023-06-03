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

import consulo.execution.debug.frame.XStackFrame;
import consulo.project.Project;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.evaluation.XDebuggerEvaluator;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/28/11
 * Time: 11:07 AM
 */
public class LuaStackFrame extends XStackFrame
{
    XSourcePosition mySourcePosition = null;
    private Project myProject;
    LuaDebuggerController myController = null;

    LuaStackFrame(Project project, LuaDebuggerController controller, XSourcePosition position) {
        mySourcePosition = position;
        myProject = project;
        myController = controller;
    }

    @Override
    public XSourcePosition getSourcePosition() {
        return mySourcePosition;
    }

    @Override
    public XDebuggerEvaluator getEvaluator() {
        return new LuaDebuggerEvaluator(myProject, this, myController);
    }
}
