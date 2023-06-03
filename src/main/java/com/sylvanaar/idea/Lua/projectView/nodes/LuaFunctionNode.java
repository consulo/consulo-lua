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
package com.sylvanaar.idea.lua.projectView.nodes;

import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.ui.view.tree.ViewSettings;
import consulo.ui.ex.tree.PresentationData;
import consulo.project.Project;
import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.util.SymbolUtil;
import consulo.util.lang.StringUtil;

import java.util.Collection;

import javax.annotation.Nullable;

public class LuaFunctionNode extends BasePsiMemberNode<LuaFunctionDefinition> {
    public LuaFunctionNode(Project project, LuaFunctionDefinition value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    public Collection<AbstractTreeNode> getChildrenImpl() {
        return null;
    }

    @Override
    protected void updateImpl(PresentationData data) {
        data.setPresentableText(StringUtil.notNullize(getFunctionName(getValue()), "<anon>"));
    }

    @Nullable
    String getFunctionName(LuaFunctionDefinition f) {        
        if (f == null) return null;
        LuaSymbol i = f.getIdentifier();
        if (i == null) return null;
        final String name = SymbolUtil.getFullSymbolName(i);
        if (name == null) return null;
        return name;
    }
    
    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }



    @Override
    public String getTitle() {
        final LuaFunctionDefinition function = getValue();
        return function != null ? StringUtil.notNullize(getFunctionName(function), "<anon>") : super.getTitle();
    }
}
