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

import consulo.execution.debug.frame.*;
import consulo.execution.debug.icon.ExecutionDebugIconGroup;
import consulo.logging.Logger;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/15/11
 * Time: 5:34 AM
 */
public class LuaDebugValue extends XValue {
    private static final Logger log = Logger.getInstance("Lua.LuaDebuggerController");

    String myValueAsString;
    String myTypeName;

    LuaDebugValue(String typeName, String stringValue) {
        myValueAsString = stringValue;
        myTypeName = typeName;
    }

    @Override
    public void computePresentation(@Nonnull XValueNode node, @Nonnull XValuePlace place) {
        node.setPresentation(ExecutionDebugIconGroup.nodeValue(), myTypeName, myValueAsString, false);
    }

    @Override
    public XValueModifier getModifier() {
        return super.getModifier();
    }

    @Override
    public void computeSourcePosition(@Nonnull XNavigatable navigatable) {
        super.computeSourcePosition(
                navigatable);
    }
}
