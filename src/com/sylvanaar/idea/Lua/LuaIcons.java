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

package com.sylvanaar.idea.Lua;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Apr 3, 2010
 * Time: 2:30:48 AM
 */
public interface LuaIcons {

	final Icon LUA_ICON            = IconLoader.findIcon("/icons/Lua.png");
    final Icon LUA_FUNCTION        = IconLoader.findIcon("/icons/function.png");
    final Icon LUA_TOOLWINDOW_ICON = IconLoader.findIcon("/icons/logo_13x13.png");

    final Icon LUA_IDEA_MODULE_ICON = IconLoader.findIcon("/icons/logo_24x24.png");
}

