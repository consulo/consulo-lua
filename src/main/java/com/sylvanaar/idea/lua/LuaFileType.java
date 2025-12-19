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
package com.sylvanaar.idea.lua;

import com.sylvanaar.idea.lua.lang.LuaLanguage;
import consulo.language.Language;
import consulo.language.file.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.lua.localize.LuaLocalize;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import consulo.virtualFileSystem.fileType.FileNameMatcherFactory;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 04.07.2009
 * Time: 1:03:43
 */
public class LuaFileType extends LanguageFileType {
    public static final LuaFileType LUA_FILE_TYPE = new LuaFileType();

    public static final Language LUA_LANGUAGE = LuaLanguage.INSTANCE;
    // public static final Icon LUA_FILE_TYPE = LuaIcons.LUA_ICON_16x16;
    @NonNls
    public static final String DEFAULT_EXTENSION = "lua";
    public static final String LUA = "Lua";

    public static final FileNameMatcher[] EXTENSION_FILE_NAME_MATCHERS;

    static {
        FileNameMatcherFactory factory = FileNameMatcherFactory.getInstance();
        EXTENSION_FILE_NAME_MATCHERS = new FileNameMatcher[] {
            factory.createExtensionFileNameMatcher(LuaFileType.DEFAULT_EXTENSION),
            factory.createExtensionFileNameMatcher("doclua"),
            factory.createExtensionFileNameMatcher("wlua")
        };
    }

    private LuaFileType() {
        super(LuaLanguage.INSTANCE);
    }

    /**
     * Creates a language file type for the specified language.
     *
     * @param language The language used in the files of the type.
     */
    protected LuaFileType(@Nonnull Language language) {
        super(language);
    }

    @Nonnull
    public String getId() {
        return LUA;
    }

    @Nonnull
    public LocalizeValue getDescription() {
        return LuaLocalize.luaFiletype();
    }

    @Nonnull
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    public Image getIcon() {
        return com.sylvanaar.idea.lua.LuaIcons.LUA_ICON;
    }
}



