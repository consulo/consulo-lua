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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import consulo.lua.debugger.breakpoint.LuaLineBreakpointProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/26/11
 * Time: 3:04 PM
 */
public class LuaLineBreakpointType extends XLineBreakpointType<LuaLineBreakpointProperties>
{
	@Nonnull
	public static LuaLineBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(LuaLineBreakpointType.class);
	}

	private final LuaDebuggerEditorsProvider myEditorsProvider = new LuaDebuggerEditorsProvider();

	public LuaLineBreakpointType()
	{
		super("lua-line-breakpoint-type", "Lua Line Breakpoints");
	}

	@Override
	public LuaLineBreakpointProperties createBreakpointProperties(@Nonnull VirtualFile file, int line)
	{
		return new LuaLineBreakpointProperties();
	}

	@Nullable
	@Override
	public XDebuggerEditorsProvider getEditorsProvider(@Nonnull XLineBreakpoint<LuaLineBreakpointProperties> breakpoint, @Nonnull Project project)
	{
		return myEditorsProvider;
	}
}
