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

import com.sylvanaar.idea.lua.LuaIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.ConfigurationType;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.configuration.RunConfigurationModule;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class LuaConfigurationType implements ConfigurationType
{
  private final ConfigurationFactory myFactory;
  
  public LuaConfigurationType() {
    myFactory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new LuaRunConfiguration(new RunConfigurationModule(project), this, "");
            }
    };
  }
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO("Lua Script");
    }

    public LocalizeValue getConfigurationTypeDescription() {
        return LocalizeValue.localizeTODO("Lua run configuration");
    }

    public Image getIcon() {
        return LuaIcons.LUA_ICON;
    }

    @Nonnull
    public String getId() {
        return "#com.sylvanaar.idea.Lua.run.LuaConfigurationType";
    }

    public static LuaConfigurationType getInstance() {
        return Application.get().getExtensionPoint(ConfigurationType.class).findExtensionOrFail(LuaConfigurationType.class);
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }
}