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
import consulo.annotation.component.ExtensionImpl;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/17/12
 * Time: 6:20 AM
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
            public String getPresentableText() {
                return item.getPresentationText();
            }

            @Nullable
            public String getLocationString() {
                String name = item.getContainingFile().getName();
                return "(in " + name + ")";
            }

            @Nullable
            public Image getIcon() {
                return LuaIcons.LUA_ICON;
            }
        };
    }
}
