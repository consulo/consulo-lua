/*
 * Copyright 2012 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.luaj;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.lua.icon.LuaIconGroup;
import consulo.lua.module.extension.LuaModuleExtension;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowFactory;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;


/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: May 7, 2010
 * Time: 8:02:20 PM
 */
@ExtensionImpl
public class LuaJInterpreterWindowFactory implements ToolWindowFactory {
    @Nonnull
    @Override
    public String getId() {
        return "LuaJ";
    }

    @Override
    public boolean validate(@Nonnull Project project) {
        return ModuleExtensionHelper.getInstance(project).hasModuleExtension(LuaModuleExtension.class);
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
//        System.setProperty("luaj.debug", "true");

        LuaJInterpreter shell = new LuaJInterpreter();
//        ScriptEngineFactory f = shell.getEngine().getFactory();
//        shell.getTerminal().appendInfo(String.format("Engine name: %s%nEngine Version: %s%nLanguageName: %s%nLanguage Version: %s%n%n%n%n",
//                                                     f.getEngineName(), f.getEngineVersion(),  f.getLanguageName(), f.getLanguageVersion()));
//
        shell.getTerminal().appendInfo("Useful shortcuts:\n" +
                "Ctrl-enter -- execute script\n" +
                "Ctrl-space -- autocomplete global variables\n" +
                "Ctrl-p -- show definition (if available)\n" +
                "Ctrl-up/down -- browse input history\n" +
                ""
        );

        toolWindow.getComponent().add(shell);
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
        return LocalizeValue.localizeTODO("LuaJ");
    }
}
