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
package com.sylvanaar.idea.lua.lang.structure.itemsPresentations.impl;

import com.sylvanaar.idea.lua.LuaIcons;
import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.lua.lang.structure.LuaElementPresentation;
import com.sylvanaar.idea.lua.lang.structure.itemsPresentations.LuaItemPresentation;
import consulo.application.util.NotNullLazyValue;
import consulo.colorScheme.TextAttributesKey;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class LuaFunctionItemPresentation extends LuaItemPresentation {

    private final NotNullLazyValue<String> myPresentableText = new NotNullLazyValue<String>() {

        @Nonnull
        @Override
        protected String compute() {
            return LuaElementPresentation.
                    getFunctionPresentableText(((LuaFunctionDefinition) myElement));
        }
    };

    private final NotNullLazyValue<String> myLocationText = new NotNullLazyValue<String>() {
        @Nonnull
        @Override
        protected String compute() {
            return LuaElementPresentation.
                    getFunctionLocationText(((LuaFunctionDefinition) myElement));
        }
    };
    TextAttributesKey textKey =
            TextAttributesKey.createTextAttributesKey(LuaFunctionItemPresentation.class.toString());

    public LuaFunctionItemPresentation(LuaFunctionDefinition myElement) {
        super(myElement);

    }

    public String getPresentableText() {
        return myPresentableText.getValue();
    }

    @Nullable
    public String getLocationString() {
        return myLocationText.getValue();
    }

    @Nullable
    public Image getIcon() {
        return LuaIcons.LUA_FUNCTION;
    }
}
