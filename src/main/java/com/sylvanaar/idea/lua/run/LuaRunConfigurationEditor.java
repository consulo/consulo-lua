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

import consulo.configurable.ConfigurationException;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import jakarta.annotation.Nullable;

/**
 * Uses code from the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public class LuaRunConfigurationEditor extends SettingsEditor<LuaRunConfiguration> {
    private final LuaRunConfiguration myConfiguration;

    @Nullable
    private LuaRunConfigurationForm myForm;

    public LuaRunConfigurationEditor(LuaRunConfiguration batchRunConfiguration) {
        myConfiguration = batchRunConfiguration;
    }

    @Override
    @RequiredUIAccess
    protected Component createUIComponent() {
        LuaRunConfigurationForm form = new LuaRunConfigurationForm(myConfiguration);
        myForm = form;
        return form.getComponent();
    }

    @Override
    @RequiredUIAccess
    protected void resetEditorFrom(LuaRunConfiguration runConfiguration) {
        LuaRunConfigurationForm form = myForm;
        if (form == null) {
            return;
        }
        LuaRunConfiguration.copyParams(runConfiguration, form);
    }

    @Override
    @RequiredUIAccess
    protected void applyEditorTo(LuaRunConfiguration runConfiguration) throws ConfigurationException {
        LuaRunConfigurationForm form = myForm;
        if (form == null) {
            return;
        }
        LuaRunConfiguration.copyParams(form, runConfiguration);
    }

    @Override
    protected void disposeEditor() {
        myForm = null;
    }
}