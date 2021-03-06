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
package com.sylvanaar.idea.Lua.run;

import javax.annotation.Nonnull;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import com.sylvanaar.idea.Lua.LuaIcons;
import consulo.ui.image.Image;

public class LuaConfigurationType implements ConfigurationType {
  private final ConfigurationFactory myFactory;
  
  public LuaConfigurationType() {
    myFactory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new LuaRunConfiguration(new RunConfigurationModule(project), this, "");
            }
    };
  }
    public String getDisplayName() {
        return "Lua Script";
    }

    public String getConfigurationTypeDescription() {
        return "Lua run configuration";
    }

    public Image getIcon() {
        return LuaIcons.LUA_ICON;
    }

    @Nonnull
    public String getId() {
        return "#com.sylvanaar.idea.Lua.run.LuaConfigurationType";
    }

    public static LuaConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), LuaConfigurationType.class);
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }
}