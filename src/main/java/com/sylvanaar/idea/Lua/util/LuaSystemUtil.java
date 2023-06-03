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

package com.sylvanaar.idea.Lua.util;

import consulo.application.util.SystemInfo;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.base.SourcesOrderRootType;
import consulo.content.bundle.Sdk;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.local.ExecUtil;
import consulo.process.local.ProcessOutput;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowManager;
import consulo.ui.ex.content.Content;
import consulo.ui.ex.content.ContentManager;
import consulo.ui.ex.toolWindow.ContentManagerWatcher;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.util.dataholder.Key;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

/**
 * @author Maxim.Manuylov
 * Date: 03.04.2010
 */
public class LuaSystemUtil {
    private static final Key<ConsoleView> CONSOLE_VIEW_KEY = Key.create("LuaConsoleView");
    public static final int STANDARD_TIMEOUT = 10 * 1000;

    @Nonnull
    public static ProcessOutput getProcessOutput(@Nonnull final String workDir, @Nonnull final String exePath,
                                                 @Nonnull final String... arguments) throws ExecutionException {
        return getProcessOutput(STANDARD_TIMEOUT, workDir, exePath, arguments);
    }

    @Nonnull
    public static ProcessOutput getProcessOutput(final int timeout, @Nonnull final String workDir,
                                                 @Nonnull final String exePath,
                                                 @Nonnull final String... arguments) throws ExecutionException {
        if (!new File(workDir).isDirectory() || !new File(exePath).canExecute()) {
            return new ProcessOutput();
        }

        final GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setWorkDirectory(workDir);
        cmd.setExePath(exePath);
        cmd.addParameters(arguments);

        return execute(cmd, timeout);
    }

    @Nonnull
    public static ProcessOutput execute(@Nonnull final GeneralCommandLine cmd) throws ExecutionException {
        return execute(cmd, STANDARD_TIMEOUT);
    }

    @Nonnull
    public static ProcessOutput execute(@Nonnull final GeneralCommandLine cmd,
                                        final int timeout) throws ExecutionException {
        return ExecUtil.execAndGetOutput(cmd, timeout);
    }

    public static void addStdPaths(@Nonnull final GeneralCommandLine cmd, @Nonnull final Sdk sdk) {
        final List<VirtualFile> files = new ArrayList<VirtualFile>();
        files.addAll(Arrays.asList(sdk.getRootProvider().getFiles(SourcesOrderRootType.getInstance())));
        files.addAll(Arrays.asList(sdk.getRootProvider().getFiles(BinariesOrderRootType.getInstance())));
        final Set<String> paths = new HashSet<String>();
        for (final VirtualFile file : files) {
            paths.add(LuaFileUtil.getPathToDisplay(file));
        }
        for (final String path : paths) {
            cmd.addParameter("-I");
            cmd.addParameter(path);
        }
    }

    public static String getPATHenvVariableName() {
        if (SystemInfo.isWindows) {
            return "Path";
        }
        if (SystemInfo.isUnix) {
            return "PATH";
        }
        else {
            return null;
        }
    }

    public static String appendToPATHenvVariable(String path, String additionalPath) {
        assert additionalPath != null;
        String pathValue;
        if (StringUtil.isEmpty(path)) {
            pathValue = additionalPath;
        }
        else {
            pathValue =
                    (new StringBuilder()).append(path).append(File.pathSeparatorChar).append(additionalPath).toString();
        }
        return FileUtil.toSystemDependentName(pathValue);
    }

    public static String prependToPATHenvVariable(String path, String additionalPath) {
        assert additionalPath != null;
        String pathValue;
        if (StringUtil.isEmpty(path)) {
            pathValue = additionalPath;
        }
        else {
            pathValue =
                    (new StringBuilder()).append(additionalPath).append(File.pathSeparatorChar).append(path).toString();
        }
        return FileUtil.toSystemDependentName(pathValue);
    }

    final static String toolWindowId = "Lua Console Output";

    public static void printMessageToConsole(@Nonnull Project project, @Nonnull String s,
                                             @Nonnull ConsoleViewContentType contentType) {
        activateConsoleToolWindow(project);
        final ConsoleView consoleView = project.getUserData(CONSOLE_VIEW_KEY);

        if (consoleView != null) {
            consoleView.print(s + '\n', contentType);
        }
    }

    public static void clearConsoleToolWindow(@Nonnull Project project) {
        final ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = manager.getToolWindow(toolWindowId);
        if (toolWindow == null) {
            return;
        }
        toolWindow.getContentManager().removeAllContents(false);
        toolWindow.hide(null);
    }

    private static void activateConsoleToolWindow(@Nonnull Project project) {
        final ToolWindowManager manager = ToolWindowManager.getInstance(project);


        ToolWindow toolWindow = manager.getToolWindow(toolWindowId);
        if (toolWindow == null) {
            toolWindow = manager.registerToolWindow(toolWindowId, true, ToolWindowAnchor.BOTTOM);
            ContentManagerWatcher.watchContentManager(toolWindow, toolWindow.getContentManager());
        }

        Content[] contents = toolWindow.getContentManager().getContents();
        if (contents.length == 0) {
            final ConsoleView console = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
            
            project.putUserData(CONSOLE_VIEW_KEY, console);
            ContentManager contentManager = toolWindow.getContentManager();
            contentManager.addContent(contentManager.getFactory().createContent(console.getComponent(), "", false));
        }


        toolWindow.show(null);
    }
}
