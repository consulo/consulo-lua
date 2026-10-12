/*
 * Copyright 2012 Jon S Akhtar (Sylvanaar)
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
package com.sylvanaar.idea.lua.lang.psi.presentation;

import com.sylvanaar.idea.lua.LuaIcons;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

/**
 * @author Jon S Akhtar
 * @since 2012-02-17
 */
@ExtensionImpl
public class LuaElementPresentationProvider implements ItemPresentationProvider<LuaPsiElement> {
    @Nonnull
    @Override
    public Class<LuaPsiElement> getItemClass() {
        return LuaPsiElement.class;
    }

    @Override
    public ItemPresentation getPresentation(final LuaPsiElement item) {
        return new ItemPresentation() {
            @Override
            public LocalizeValue getPresentableText() {
                return item.getPresentationText();
            }

            @Override
            @RequiredReadAction
            public String getLocationString() {
                String name = item.getContainingFile().getName();
                return "(in " + name + ")";
            }

            @Override
            public Image getIcon() {
                return LuaIcons.LUA_ICON;
            }
        };
    }
}
