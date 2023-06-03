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

import com.sylvanaar.idea.lua.LuaFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.execution.RunnerAndConfigurationSettings;
import consulo.execution.action.ConfigurationContext;
import consulo.execution.action.Location;
import consulo.execution.action.RuntimeConfigurationProducer;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;

/**
 * This class is based on code of the intellij-batch plugin.
 *
 * @author wibotwi, jansorg, sylvanaar
 */
@ExtensionImpl
public class LuaRunConfigurationProducer extends RuntimeConfigurationProducer implements Cloneable {
    private PsiFile sourceFile = null;

    public LuaRunConfigurationProducer() {
        super(LuaConfigurationType.getInstance());
    }

    @Override
    public PsiElement getSourceElement() {
        return sourceFile;
    }

    @Override
    protected RunnerAndConfigurationSettings createConfigurationByElement(Location location, ConfigurationContext configurationContext) {
        sourceFile = location.getPsiElement().getContainingFile();

        if (sourceFile != null && sourceFile.getFileType().equals(LuaFileType.LUA_FILE_TYPE)) {
            Project project = sourceFile.getProject();
            RunnerAndConfigurationSettings settings = cloneTemplateConfiguration(project, configurationContext);

            VirtualFile file = sourceFile.getVirtualFile();

            LuaRunConfiguration runConfiguration = (LuaRunConfiguration) settings.getConfiguration();
            if (file != null) {
                runConfiguration.setName(file.getName());

                runConfiguration.setScriptName(file.getName());
                final VirtualFile dir = configurationContext.getProject().getBaseDir();
                if (dir != null)
                    runConfiguration.setWorkingDirectory(dir.getPath());
            }

            Module module = ModuleUtilCore.findModuleForPsiElement(location.getPsiElement());
            if (module != null) {
                runConfiguration.setModule(module);
            }

            if (StringUtil.isEmptyOrSpaces(runConfiguration.getInterpreterPath())) {
                runConfiguration.setOverrideSDKInterpreter(false);
            }

            return settings;
        }

        return null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}