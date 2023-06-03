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

package com.sylvanaar.idea.lua.lang.formatter;

import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.ui.setting.CodeStyleAbstractConfigurable;
import consulo.language.codeStyle.ui.setting.CodeStyleAbstractPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 10/1/11
 * Time: 12:47 PM
 */
public class LuaCodeStyleConfigurable extends CodeStyleAbstractConfigurable
{
    public LuaCodeStyleConfigurable(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        super(settings, originalSettings, "Lua");
    }

    @Override
    protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new LuaCodeStyleMainPanel(getCurrentSettings(), settings);
    }

    @Override
    public String getHelpTopic() {
        return null;
    }
}
