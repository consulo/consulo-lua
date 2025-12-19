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

package com.sylvanaar.idea.lua.kahlua;

import consulo.localize.LocalizeValue;
import consulo.lua.icon.LuaIconGroup;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowFactory;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

//import se.krka.kahlua.converter.KahluaConverterManager;
//import se.krka.kahlua.converter.KahluaEnumConverter;
//import se.krka.kahlua.converter.KahluaNumberConverter;
//import se.krka.kahlua.converter.KahluaTableConverter;
//import se.krka.kahlua.j2se.J2SEPlatform;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: May 7, 2010
 * Time: 8:02:20 PM
 */
public class KahLuaInterpreterWindowFactory implements ToolWindowFactory {
    public static final String ID = "kahlua";

    public static KahluaInterpreter INSTANCE = null;

    @Nonnull
    @Override
    public String getId() {
        return ID;
    }

    @RequiredUIAccess
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
//        final Platform platform = new J2SEPlatform();
//        final KahluaTable env = platform.newEnvironment();
//
//        KahluaConverterManager manager = new KahluaConverterManager();
//        KahluaNumberConverter.install(manager);
//        KahluaEnumConverter.install(manager);
//        new KahluaTableConverter(platform).install(manager);
//
//        KahluaTable staticBase = platform.newTable();
//        env.rawset("Java", staticBase);
//
//        KahluaInterpreter shell = new KahluaInterpreter(platform, env);

//        INSTANCE = shell;
//
//        shell.getTerminal().appendInfo("Useful shortcuts:\n" +
//                "Ctrl-enter -- execute script\n" +
//                "Ctrl-space -- autocomplete global variables\n" +
//                "Ctrl-p -- show definition (if available)\n" +
//                "Ctrl-up/down -- browse input history\n" +
//                ""
//        );
//
//        toolWindow.getComponent().add(shell);
    }

    @Nonnull
    @Override
    public ToolWindowAnchor getAnchor() {
        return ToolWindowAnchor.RIGHT;
    }

    @Nonnull
    @Override
    public Image getIcon() {
        return LuaIconGroup.logo_13x13();
    }

    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO("Kahlua");
    }
}
