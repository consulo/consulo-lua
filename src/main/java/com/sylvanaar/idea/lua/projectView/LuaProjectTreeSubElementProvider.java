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

package com.sylvanaar.idea.lua.projectView;

import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.projectView.nodes.LuaFileTreeNode;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.project.Project;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.ui.view.tree.TreeStructureProvider;
import consulo.project.ui.view.tree.ViewSettings;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 12/22/11
 * Time: 12:58 PM
 */
@ExtensionImpl
public class LuaProjectTreeSubElementProvider implements TreeStructureProvider, DumbAware {
    private final Project myProject;

    @Inject
    public LuaProjectTreeSubElementProvider(Project project) {
        myProject = project;
    }

    @Override
    public Collection<AbstractTreeNode> modify(AbstractTreeNode parent, Collection<AbstractTreeNode> children, ViewSettings settings) {
        ArrayList<AbstractTreeNode> result = new ArrayList<AbstractTreeNode>();
        for (final AbstractTreeNode child : children) {
            Object o = child.getValue();

            if (o instanceof LuaPsiFile) {
                result.add(new LuaFileTreeNode(myProject, (LuaPsiFile) o, settings));
                continue;
            }

            result.add(child);
        }
        return result;
    }
}
