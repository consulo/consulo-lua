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
import com.sylvanaar.idea.lua.lang.psi.statements.LuaReturnStatement;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.AllIcons;
import consulo.application.dumb.DumbAware;
import consulo.codeEditor.markup.GutterIconRenderer;
import consulo.language.Language;
import consulo.language.editor.Pass;
import consulo.language.editor.gutter.LineMarkerInfo;
import consulo.language.editor.gutter.LineMarkerProviderDescriptor;
import consulo.language.psi.PsiElement;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

@ExtensionImpl
public class LuaTailCallLineMarkerProvider extends LineMarkerProviderDescriptor implements DumbAware {
    private final Function<PsiElement, String> tailCallTooltip = psiElement -> "Tail Call: " + psiElement.getText();

    @RequiredReadAction
    @Override
    public LineMarkerInfo getLineMarkerInfo(@Nonnull final PsiElement element) {
        if (element instanceof LuaReturnStatement) {
            LuaReturnStatement e = (LuaReturnStatement) element;

            if (e.isTailCall()) {
                return new LineMarkerInfo<>(element, element.getTextRange(), AllIcons.Gutter.RecursiveMethod, Pass.LINE_MARKERS, tailCallTooltip, null, GutterIconRenderer.Alignment.LEFT);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return AllIcons.Gutter.RecursiveMethod;
    }

    @Nullable
    @Override
    public String getName() {
        return "Tail call";
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}