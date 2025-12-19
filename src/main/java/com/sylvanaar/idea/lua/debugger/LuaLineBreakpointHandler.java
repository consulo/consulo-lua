package com.sylvanaar.idea.lua.debugger;

import consulo.execution.debug.breakpoint.XBreakpoint;
import consulo.execution.debug.breakpoint.XBreakpointHandler;
import jakarta.annotation.Nonnull;

public class LuaLineBreakpointHandler extends XBreakpointHandler
{
    protected LuaDebugProcess myDebugProcess;



    public LuaLineBreakpointHandler(LuaDebugProcess debugProcess) {
        super(LuaLineBreakpointType.class);
        myDebugProcess = debugProcess;
    }

    public void registerBreakpoint(@Nonnull XBreakpoint xBreakpoint) {
        myDebugProcess.addBreakPoint(xBreakpoint);
    }

    public void unregisterBreakpoint(@Nonnull XBreakpoint xBreakpoint, boolean temporary) {
        myDebugProcess.removeBreakPoint(xBreakpoint);
    }

    

}
