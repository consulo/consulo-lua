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

import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.ProgressManager;
import consulo.application.progress.Task;
import consulo.execution.ExecutionResult;
import consulo.execution.debug.XDebugProcess;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.breakpoint.XBreakpoint;
import consulo.execution.debug.breakpoint.XBreakpointHandler;
import consulo.execution.debug.evaluation.XDebuggerEditorsProvider;
import consulo.execution.ui.ExecutionConsole;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.logging.Logger;
import consulo.process.ProcessHandler;
import consulo.ui.ex.awt.Messages;
import jakarta.annotation.Nonnull;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/19/11
 * Time: 7:40 PM
 */
public class LuaDebugProcess extends XDebugProcess {
    private static final Logger log = Logger.getInstance("Lua.LuaDebugProcess");
    final LuaDebuggerController controller;
    LuaLineBreakpointHandler lineBreakpointHandler;
    private boolean         myClosing;
    private ExecutionResult executionResult;
    private ConsoleView     myExecutionConsole;

    /**
     * @param session pass <code>session</code> parameter of {@link com.intellij.xdebugger
     *                .XDebugProcessStarter#start} method to this constructor
     * @param result
     */
    protected LuaDebugProcess(@Nonnull XDebugSession session, ExecutionResult result) {
        super(session);
        lineBreakpointHandler = new LuaLineBreakpointHandler(this);

        controller = new LuaDebuggerController(session);

        executionResult = result;
    }

    @Nonnull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return new LuaDebuggerEditorsProvider();
    }

    @Override
    public void startStepOver() {
        controller.stepOver();
    }

    @Override
    public void startStepInto() {
        controller.stepInto();
    }

    @Override
    public void startStepOut() {
    }

    @Override
    public void stop() {
        myClosing = true;

        if (executionResult != null) executionResult.getProcessHandler().destroyProcess();

        controller.terminate();
    }

    @Override
    public void resume() {
        controller.resume();
    }

    @Override
    public void runToPosition(@Nonnull XSourcePosition position) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ProcessHandler doGetProcessHandler() {
        return executionResult.getProcessHandler();
    }

    @Nonnull
    @Override
    public ExecutionConsole createConsole() {
        myExecutionConsole = (ConsoleView) executionResult.getExecutionConsole();

        controller.setConsole(myExecutionConsole);
        return myExecutionConsole;
    }

    public void printToConsole(String text, ConsoleViewContentType contentType) {
        myExecutionConsole.print(text, contentType);
    }

    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        return new XBreakpointHandler<?>[]{lineBreakpointHandler};
    }

    public void sessionInitialized() {
        super.sessionInitialized();
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "Connecting to debugger", false) {

            public void run(@Nonnull ProgressIndicator indicator) {
                indicator.setText("Connecting to debugger...");
                log.debug("connecting");
                try {
                    controller.waitForConnect();

                    log.debug("connected");
                    indicator.setText("... Debugger connected");

                    getSession().rebuildViews();

                    registerBreakpoints();

                    controller.resume();
                } catch (final Exception e) {

                    if (executionResult != null && executionResult.getProcessHandler() != null)
                        executionResult.getProcessHandler().destroyProcess();

                    if (!myClosing) SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            Messages.showErrorDialog(
                                    (new StringBuilder()).append("Unable to establish connection with debugger:\n")
                                                         .append(e.getMessage()).toString(), "Connecting to Debugger");
                        }
                    });
                }
            }
        });
    }


    java.util.List<XBreakpoint> installedBreaks = new ArrayList<XBreakpoint>();

    private synchronized void registerBreakpoints() {

        log.debug("registering pending breakpoints");

        for (XBreakpoint b : installedBreaks) {
            while (!controller.isReady()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    return;
                }
            }

            controller.addBreakPoint(b);
        }

        installedBreaks.clear();
    }

    public synchronized void addBreakPoint(XBreakpoint pos) {
        log.debug("add breakpoint " + pos.toString());
        if (controller.isReady()) controller.addBreakPoint(pos);
        else installedBreaks.add(pos);
    }

    public synchronized void removeBreakPoint(XBreakpoint pos) {
        log.debug("remove breakpoint " + pos.toString());
        // if (controller.isReady())
        controller.removeBreakPoint(pos);
    }
}
