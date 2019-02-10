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
package com.sylvanaar.idea.Lua.copyright;

import javax.annotation.Nonnull;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.maddyhome.idea.copyright.CopyrightProfile;
import com.maddyhome.idea.copyright.psi.UpdateCopyrightsProvider;
import com.maddyhome.idea.copyright.psi.UpdatePsiFileCopyright;
import com.maddyhome.idea.copyright.ui.TemplateCommentPanel;
import consulo.copyright.config.CopyrightFileConfig;

public class UpdateLuaCopyrightsProvider extends UpdateCopyrightsProvider<CopyrightFileConfig>
{
	@Nonnull
	@Override
	public UpdatePsiFileCopyright<CopyrightFileConfig> createInstance(@Nonnull PsiFile file, @Nonnull CopyrightProfile copyrightProfile)
	{
		return new UpdateLuaFileCopyright(file, copyrightProfile);
	}

	@Nonnull
	@Override
	public CopyrightFileConfig createDefaultOptions()
	{
		CopyrightFileConfig options = new CopyrightFileConfig();

		options.setFiller("=");
		options.setBlock(false);
		options.setPrefixLines(false);
		options.setFileTypeOverride(CopyrightFileConfig.USE_TEXT);

		return options;
	}

	@Nonnull
	@Override
	public TemplateCommentPanel createConfigurable(@Nonnull Project project, @Nonnull TemplateCommentPanel parentPane, @Nonnull FileType fileType)
	{
		return new TemplateCommentPanel(fileType, parentPane, project);
	}
}