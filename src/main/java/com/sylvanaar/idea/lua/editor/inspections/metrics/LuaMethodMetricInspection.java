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
package com.sylvanaar.idea.lua.editor.inspections.metrics;


import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;

import javax.annotation.Nonnull;

public abstract class LuaMethodMetricInspection extends AbstractInspection {

    @SuppressWarnings({"PublicField", "WeakerAccess"})
    public int m_limit = getDefaultLimit();

    protected abstract int getDefaultLimit();

    protected abstract String getConfigurationLabel();

    protected int getLimit() {
        return m_limit;
    }

    @Nonnull
    public String getGroupDisplayName() {
        return METHOD_METRICS;
    }

//    public JComponent createOptionsPanel() {
//        final String configurationLabel = getConfigurationLabel();
//        return new consulo.ide.impl.idea.codeInspection.ui.SingleIntegerFieldOptionsPanel(configurationLabel, this, "m_limit");
//    }
}