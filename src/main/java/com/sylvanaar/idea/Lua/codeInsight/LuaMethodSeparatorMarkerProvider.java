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

package com.sylvanaar.idea.lua.codeInsight;

import com.sylvanaar.idea.lua.lang.LuaLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.CodeInsightColors;
import consulo.codeEditor.markup.GutterIconRenderer;
import consulo.colorScheme.EditorColorsScheme;
import consulo.language.Language;
import consulo.language.editor.DaemonCodeAnalyzerSettings;
import consulo.language.editor.gutter.LineMarkerInfo;
import consulo.colorScheme.EditorColorsManager;
import consulo.codeEditor.markup.SeparatorPlacement;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocCommentOwner;
import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.editor.Pass;
import consulo.language.editor.gutter.LineMarkerProvider;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ExtensionImpl
public class LuaMethodSeparatorMarkerProvider implements LineMarkerProvider
{
	private final DaemonCodeAnalyzerSettings myDaemonSettings;
	private final EditorColorsManager myColorsManager;

	@Inject
	public LuaMethodSeparatorMarkerProvider(DaemonCodeAnalyzerSettings daemonSettings, EditorColorsManager colorsManager)
	{
		myDaemonSettings = daemonSettings;
		myColorsManager = colorsManager;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public LineMarkerInfo getLineMarkerInfo(@Nonnull PsiElement element)
	{
		if(myDaemonSettings.SHOW_METHOD_SEPARATORS)
		{
			if(element instanceof LuaDocComment)
			{
				LuaDocCommentOwner owner = ((LuaDocComment) element).getOwner();

				if(owner instanceof LuaFunctionDefinition)
				{
					TextRange range = new TextRange(element.getTextOffset(), owner.getTextRange().getEndOffset());
					LineMarkerInfo<PsiElement> info = new LineMarkerInfo<>(element, range, null, Pass.UPDATE_ALL, element1 -> null, null, GutterIconRenderer.Alignment.RIGHT);
					EditorColorsScheme scheme = myColorsManager.getGlobalScheme();
					info.separatorColor = scheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR);
					info.separatorPlacement = SeparatorPlacement.TOP;
					return info;
				}
			}
		}
		return null;
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return LuaLanguage.INSTANCE;
	}
}
