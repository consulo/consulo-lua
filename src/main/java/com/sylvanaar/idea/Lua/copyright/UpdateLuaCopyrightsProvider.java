/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

/*
 * User: anna
 * Date: 30-Nov-2009
 */
package com.sylvanaar.idea.lua.copyright;

import com.sylvanaar.idea.lua.LuaFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.copyright.UpdateCopyrightsProvider;
import consulo.language.copyright.UpdatePsiFileCopyright;
import consulo.language.copyright.config.CopyrightFileConfig;
import consulo.language.copyright.config.CopyrightProfile;
import consulo.language.copyright.ui.TemplateCommentPanel;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;

@ExtensionImpl
public class UpdateLuaCopyrightsProvider extends UpdateCopyrightsProvider<CopyrightFileConfig> {
    @Nonnull
    @Override
    public FileType getFileType() {
        return LuaFileType.LUA_FILE_TYPE;
    }

    @Nonnull
    @Override
    public UpdatePsiFileCopyright<CopyrightFileConfig> createInstance(@Nonnull PsiFile file, @Nonnull CopyrightProfile copyrightProfile) {
        return new UpdateLuaFileCopyright(file, copyrightProfile);
    }

    @Nonnull
    @Override
    public CopyrightFileConfig createDefaultOptions() {
        CopyrightFileConfig options = new CopyrightFileConfig();

        options.setFiller("=");
        options.setBlock(false);
        options.setPrefixLines(false);
        options.setFileTypeOverride(CopyrightFileConfig.USE_TEXT);

        return options;
    }

    @Nonnull
    @Override
    public TemplateCommentPanel createConfigurable(@Nonnull Project project, @Nonnull TemplateCommentPanel parentPane, @Nonnull FileType fileType) {
        return new TemplateCommentPanel(fileType, parentPane, project);
    }
}