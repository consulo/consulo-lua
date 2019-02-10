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

package com.sylvanaar.idea.Lua.codeInsight;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.SeparatorPlacement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.NullableFunction;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.Lua.lang.luadoc.psi.api.LuaDocCommentOwner;
import com.sylvanaar.idea.Lua.lang.psi.LuaFunctionDefinition;
import consulo.annotations.RequiredReadAction;

public class LuaMethodSeparatorMarkerProvider implements LineMarkerProvider
{
	private final DaemonCodeAnalyzerSettings myDaemonSettings;
	private final EditorColorsManager myColorsManager;

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
					LineMarkerInfo<PsiElement> info = new LineMarkerInfo<>(element, range, null, Pass.UPDATE_ALL, NullableFunction.NULL, null, GutterIconRenderer.Alignment.RIGHT);
					EditorColorsScheme scheme = myColorsManager.getGlobalScheme();
					info.separatorColor = scheme.getColor(CodeInsightColors.METHOD_SEPARATORS_COLOR);
					info.separatorPlacement = SeparatorPlacement.TOP;
					return info;
				}
			}
		}
		return null;
	}
}
