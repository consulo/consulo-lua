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

package com.sylvanaar.idea.lua.run;

import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileChooser.FileChooserTextBoxBuilder;
import consulo.lua.localize.LuaLocalize;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.ui.Component;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.layout.LabeledLayout;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;

/**
 * The configuration user interface to configure a new Lua run configuration.
 * <p/>
 * User: jansorg
 * Date: 10.07.2009
 * Time: 21:30:48
 */
public class LuaRunConfigurationForm implements LuaRunConfigurationParams {
    private final FileChooserTextBoxBuilder.Controller myScriptName;
    private final TextBoxWithExpandAction myScriptParameters;
    private final LuaCommonOptionsForm myCommonOptionsForm;
    private final Component myComponent;

    @RequiredUIAccess
    public LuaRunConfigurationForm(LuaRunConfiguration luaRunConfiguration) {
        myScriptName = FileChooserTextBoxBuilder.create(luaRunConfiguration.getProject())
            .dialogTitle(LuaLocalize.runConfigurationSelectScriptTitle())
            .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor())
            .build();

        myScriptParameters = TextBoxWithExpandAction.create(
            PlatformIconGroup.actionsShow(),
            "",
            ParametersListUtil.DEFAULT_LINE_PARSER,
            ParametersListUtil.DEFAULT_LINE_JOINER
        );

        myCommonOptionsForm = new LuaCommonOptionsForm(luaRunConfiguration);

        FormBuilder builder = FormBuilder.create();
        builder.addLabeled(LuaLocalize.runConfigurationScriptNameLabel(), myScriptName.getComponent());
        builder.addLabeled(LuaLocalize.runConfigurationScriptParametersLabel(), myScriptParameters);

        VerticalLayout panel = VerticalLayout.create();
        panel.add(builder.build());
        panel.add(LabeledLayout.create(
            LuaLocalize.runConfigurationCommonOptions(),
            DockLayout.create().center(myCommonOptionsForm.getComponent())
        ));
        myComponent = panel;
    }

    @Override
    public CommonLuaRunConfigurationParams getCommonParams() {
        return myCommonOptionsForm;
    }

    @Override
    @RequiredUIAccess
    public String getScriptName() {
        return StringUtil.notNullize(myScriptName.getValue());
    }

    @Override
    @RequiredUIAccess
    public void setScriptName(String scriptName) {
        myScriptName.setValue(StringUtil.notNullize(scriptName));
    }

    @Override
    @RequiredUIAccess
    public String getScriptParameters() {
        return StringUtil.notNullize(myScriptParameters.getValue());
    }

    @Override
    @RequiredUIAccess
    public void setScriptParameters(String scriptParameters) {
        myScriptParameters.setValue(StringUtil.notNullize(scriptParameters));
    }

    public Component getComponent() {
        return myComponent;
    }
}
