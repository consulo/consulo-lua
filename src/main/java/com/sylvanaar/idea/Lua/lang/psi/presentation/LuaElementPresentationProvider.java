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

package com.sylvanaar.idea.Lua.lang.psi.presentation;

import javax.annotation.Nullable;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.sylvanaar.idea.Lua.LuaIcons;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiElement;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/17/12
 * Time: 6:20 AM
 */
public class LuaElementPresentationProvider implements ItemPresentationProvider<LuaPsiElement> {
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
