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

import consulo.lua.icon.LuaIconGroup;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Apr 3, 2010
 * Time: 2:30:48 AM
 */
public interface LuaIcons {
    final Image LUA_ICON = LuaIconGroup.lua();
    final Image LUA_FUNCTION = PlatformIconGroup.nodesFunction();
}


