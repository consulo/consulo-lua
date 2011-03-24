/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: LuaFacetType.java, Class: LuaFacetType
 * Last modified: 2010-02-11
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sylvanaar.idea.Lua.settings.facet;


import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.sylvanaar.idea.Lua.LuaIcons;
import com.sylvanaar.idea.Lua.module.LuaModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LuaFacetType extends FacetType<LuaFacet, LuaFacetConfiguration> {
    public static final FacetTypeId<LuaFacet> ID = new FacetTypeId<LuaFacet>("Lua");
    public static final LuaFacetType INSTANCE = new LuaFacetType();

    public LuaFacetType() {
        super(ID, "Lua", "Lua");
    }

    @Override
    public LuaFacetConfiguration createDefaultConfiguration() {
        return null;
    }

    @Override
    public LuaFacet createFacet(@NotNull Module module, String name,
                                 @NotNull LuaFacetConfiguration configuration,
                                 @Nullable Facet underlyingFacet) {
        return new LuaFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
         return !(moduleType instanceof LuaModuleType);
    }

    @Override
    public Icon getIcon() {
        return LuaIcons.LUA_ICON;
    }
}