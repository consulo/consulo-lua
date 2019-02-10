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
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.util.NullableFunction;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaReturnStatement;
import consulo.annotations.RequiredReadAction;
import consulo.ui.image.Image;

public class LuaTailCallLineMarkerProvider extends LineMarkerProviderDescriptor implements DumbAware
{
	private final NullableFunction<PsiElement, String> tailCallTooltip = psiElement -> "Tail Call: " + psiElement.getText();

	@RequiredReadAction
	@Override
	public LineMarkerInfo getLineMarkerInfo(@Nonnull final PsiElement element)
	{
		if(element instanceof LuaReturnStatement)
		{
			LuaReturnStatement e = (LuaReturnStatement) element;

			if(e.isTailCall())
			{
				return new LineMarkerInfo<>(element, element.getTextRange(), AllIcons.Gutter.RecursiveMethod, Pass.LINE_MARKERS, tailCallTooltip, null, GutterIconRenderer.Alignment.LEFT);
			}
		}
		return null;
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return AllIcons.Gutter.RecursiveMethod;
	}

	@Nullable
	@Override
	public String getName()
	{
		return "Tail call";
	}
}