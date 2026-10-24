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
import consulo.localize.LocalizeValue;
import consulo.lua.localize.LuaLocalize;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;

/**
 * Options of the Lua interpreter, shown once set.
 *
 * @author VISTALL
 * @since 2026-10-24
 */
class LuaInterpreterOptionsFragment implements RunConfigurationFragment<LuaRunConfiguration> {
    @Override
    public String getId() {
        return "luaInterpreterOptions";
    }

    @Override
    public LocalizeValue getName() {
        return LuaLocalize.runConfigurationFragmentInterpreterOptions();
    }

    @Override
    public boolean isVisibleByDefault(LuaRunConfiguration target) {
        return target.getInterpreterOptions() != null;
    }

    @Override
    public void applyHidden(LuaRunConfiguration target) {
        target.setInterpreterOptions(null);
    }

    @RequiredUIAccess
    @Override
    public RunConfigurationFragmentEditor<LuaRunConfiguration> createEditor(Project project, Disposable parent) {
        TextBoxWithExpandAction interpreterOptions = TextBoxWithExpandAction.create(
            PlatformIconGroup.actionsShow(),
            "",
            ParametersListUtil.DEFAULT_LINE_PARSER,
            ParametersListUtil.DEFAULT_LINE_JOINER
        );

        return new RunConfigurationFragmentEditor<>() {
            @RequiredUIAccess
            @Override
            public void addRows(RunConfigurationFragmentLayout layout) {
                layout.addLabeled(LuaLocalize.runConfigurationInterpreterOptionsLabel(), interpreterOptions);
            }

            @RequiredUIAccess
            @Override
            public void reset(LuaRunConfiguration target) {
                interpreterOptions.setValue(StringUtil.notNullize(target.getInterpreterOptions()));
            }

            @RequiredUIAccess
            @Override
            public void apply(LuaRunConfiguration target) {
                target.setInterpreterOptions(interpreterOptions.getValue());
            }
        };
    }
}
