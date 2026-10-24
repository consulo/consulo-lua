/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sylvanaar.idea.lua.run;

import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileChooser.FileChooserTextBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.lua.localize.LuaLocalize;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;

/**
 * The script a {@link LuaRunConfiguration} runs.
 *
 * @author VISTALL
 * @since 2026-10-24
 */
class LuaScriptFragment implements RunConfigurationFragment<LuaRunConfiguration> {
    @Override
    public String getId() {
        return "luaScript";
    }

    @Override
    public LocalizeValue getName() {
        return LuaLocalize.runConfigurationFragmentScript();
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @RequiredUIAccess
    @Override
    public RunConfigurationFragmentEditor<LuaRunConfiguration> createEditor(Project project, Disposable parent) {
        FileChooserTextBoxBuilder.Controller scriptName = FileChooserTextBoxBuilder.create(project)
            .dialogTitle(LuaLocalize.runConfigurationSelectScriptTitle())
            .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor())
            .uiDisposable(parent)
            .build();

        return new RunConfigurationFragmentEditor<>() {
            @RequiredUIAccess
            @Override
            public void addRows(RunConfigurationFragmentLayout layout) {
                layout.addLabeled(LuaLocalize.runConfigurationScriptNameLabel(), scriptName.getComponent());
            }

            @RequiredUIAccess
            @Override
            public void reset(LuaRunConfiguration target) {
                scriptName.setValue(FileUtil.toSystemDependentName(StringUtil.notNullize(target.getScriptName())), false);
            }

            @RequiredUIAccess
            @Override
            public void apply(LuaRunConfiguration target) {
                target.setScriptName(FileUtil.toSystemIndependentName(StringUtil.notNullize(scriptName.getValue()).trim()));
            }
        };
    }
}
