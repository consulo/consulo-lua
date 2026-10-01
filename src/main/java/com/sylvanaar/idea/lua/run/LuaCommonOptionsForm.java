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

import consulo.execution.localize.ExecutionLocalize;
import consulo.execution.ui.awt.EnvironmentVariablesTextFieldWithBrowseButton;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileChooser.FileChooserTextBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.lua.localize.LuaLocalize;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.Space;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;

import java.util.Map;

/**
 * User: jansorg
 * Date: 10.07.2009
 * Time: 21:43:12
 */
public class LuaCommonOptionsForm implements CommonLuaRunConfigurationParams {
    private final CheckBox myUseSdkCheckBox;
    private final FileChooserTextBoxBuilder.Controller myInterpreterPath;
    private final TextBoxWithExpandAction myInterpreterOptions;
    private final FileChooserTextBoxBuilder.Controller myWorkingDirectory;
    private final EnvironmentVariablesTextFieldWithBrowseButton myEnvironmentVariables;
    private final Component myComponent;

    @RequiredUIAccess
    public LuaCommonOptionsForm(LuaRunConfiguration luaRunConfiguration) {
        Project project = luaRunConfiguration.getProject();

        myInterpreterPath = FileChooserTextBoxBuilder.create(project)
            .dialogTitle(LuaLocalize.runConfigurationSelectInterpreterTitle())
            .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor())
            .build();

        myUseSdkCheckBox = CheckBox.create(LuaLocalize.runConfigurationUseModuleSdk());
        myUseSdkCheckBox.addValueListener(event -> updateInterpreterOptionsWidgets());

        myInterpreterOptions = TextBoxWithExpandAction.create(
            PlatformIconGroup.actionsShow(),
            "",
            ParametersListUtil.DEFAULT_LINE_PARSER,
            ParametersListUtil.DEFAULT_LINE_JOINER
        );

        myWorkingDirectory = FileChooserTextBoxBuilder.create(project)
            .dialogTitle(LuaLocalize.runConfigurationSelectWorkingDirectoryTitle())
            .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFolderDescriptor())
            .build();

        myEnvironmentVariables = new EnvironmentVariablesTextFieldWithBrowseButton();

        FormBuilder builder = FormBuilder.create();
        builder.addLabeled(
            LuaLocalize.runConfigurationInterpreterPathLabel(),
            DockLayout.create(Space.SMALL).left(myUseSdkCheckBox).center(myInterpreterPath.getComponent())
        );
        builder.addLabeled(LuaLocalize.runConfigurationInterpreterOptionsLabel(), myInterpreterOptions);
        builder.addLabeled(LuaLocalize.runConfigurationWorkingDirectoryLabel(), myWorkingDirectory.getComponent());
        builder.addLabeled(
            LocalizeValue.join(ExecutionLocalize.environmentVariablesComponentTitle(), LocalizeValue.colon()),
            myEnvironmentVariables.getComponent()
        );
        myComponent = builder.build();
    }

    @RequiredUIAccess
    private void updateInterpreterOptionsWidgets() {
        myInterpreterPath.getComponent().setEnabled(!isUseSdkSelected());
    }

    @RequiredUIAccess
    private boolean isUseSdkSelected() {
        return Boolean.TRUE.equals(myUseSdkCheckBox.getValue());
    }

    @Override
    @RequiredUIAccess
    public String getInterpreterOptions() {
        return StringUtil.notNullize(myInterpreterOptions.getValue());
    }

    @Override
    @RequiredUIAccess
    public void setInterpreterOptions(String options) {
        myInterpreterOptions.setValue(StringUtil.notNullize(options));
    }

    @Override
    @RequiredUIAccess
    public String getWorkingDirectory() {
        return StringUtil.notNullize(myWorkingDirectory.getValue());
    }

    @Override
    @RequiredUIAccess
    public void setWorkingDirectory(String workingDirectory) {
        myWorkingDirectory.setValue(StringUtil.notNullize(workingDirectory));
    }

    @Override
    public Map<String, String> getEnvs() {
        return myEnvironmentVariables.getEnvs();
    }

    @Override
    @RequiredUIAccess
    public void setEnvs(Map<String, String> envs) {
        myEnvironmentVariables.setEnvs(envs);
    }

    @Override
    public boolean isPassParentEnvs() {
        return myEnvironmentVariables.isPassParentEnvs();
    }

    @Override
    @RequiredUIAccess
    public void setPassParentEnvs(boolean passParentEnvs) {
        myEnvironmentVariables.setPassParentEnvs(passParentEnvs);
    }

    @Override
    @RequiredUIAccess
    public String getInterpreterPath() {
        return StringUtil.notNullize(myInterpreterPath.getValue());
    }

    @Override
    @RequiredUIAccess
    public void setInterpreterPath(String path) {
        myInterpreterPath.setValue(StringUtil.notNullize(path));
    }

    @Override
    @RequiredUIAccess
    public boolean isOverrideSDKInterpreter() {
        return !isUseSdkSelected();
    }

    @Override
    @RequiredUIAccess
    public void setOverrideSDKInterpreter(boolean b) {
        myUseSdkCheckBox.setValue(!b);
        updateInterpreterOptionsWidgets();
    }

    public Component getComponent() {
        return myComponent;
    }
}
