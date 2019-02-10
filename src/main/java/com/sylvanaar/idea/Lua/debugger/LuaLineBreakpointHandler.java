package com.sylvanaar.idea.Lua.debugger;

import javax.annotation.Nonnull;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;

public class LuaLineBreakpointHandler extends XBreakpointHandler {
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
