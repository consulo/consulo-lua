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

package com.sylvanaar.idea.Lua.lang.psi.types;

import com.intellij.openapi.diagnostic.Logger;
import com.sylvanaar.idea.Lua.lang.psi.LuaNamedElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 9/18/11
 * Time: 3:08 AM
 */
public class LuaTable extends LuaType {
    Logger log = Logger.getInstance("Lua.LuaTable");
    
    private Map<Object, LuaType> hash = new HashMap<Object, LuaType>();

    @Override
    public String toString() {
        return "Table: " + getEncodedAsString();
    }

    LuaTable guard = null;

    @Override
    public String getEncodedAsString() {
        if (guard == this) return "!RECURSION!";
        guard = this;
        StringBuilder sb = new StringBuilder();

        sb.append('{');
        for(Map.Entry<Object, LuaType> type : hash.entrySet()) {
            final LuaType value = type.getValue();
            if (value != null && value != this)
                sb.append('@').append(type.getKey().toString()).append('=').append(value.getEncodedAsString());
        }
        sb.append('}');

        guard = null;
        return sb.toString();
    }


    public void addPossibleElement(Object key, LuaType type) {
        assert type != null : "Null type for " + key;

        if (key instanceof LuaNamedElement)
            key = ((LuaNamedElement) key).getName();

        LuaType current = hash.get(key);
        if (current != null)
            hash.put(key, LuaType.combineTypes(current, type));
        else
            hash.put(key, type);

        log.debug("New Element of Table: " + toString() + " " + key + " " + type);
    }

    public Map<?,?> getFieldSet() {
        return hash;
    }

    public void reset() {
     //   hash.clear();
    }
}