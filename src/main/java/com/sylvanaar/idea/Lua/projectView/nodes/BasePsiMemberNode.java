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

/*
 * User: anna
 * Date: 17-Jan-2008
 */
package com.sylvanaar.idea.lua.projectView.nodes;

import consulo.project.ui.view.tree.BasePsiNode;
import consulo.project.Project;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import consulo.project.ui.view.tree.ViewSettings;

public abstract class BasePsiMemberNode<T extends LuaPsiElement> extends BasePsiNode<T>{
  protected BasePsiMemberNode(Project project, T value, ViewSettings viewSettings) {
    super(project, value, viewSettings);
  }

  @Override
  protected String calcTooltip() {
    T t = getValue();
    if (t != null && t.isValid()) {
      return t.getPresentationText();
    }
    return super.calcTooltip();
  }
//
//  @Override
//  protected boolean isDeprecated() {
//    final PsiModifierListOwner element = getValue();
//    return element != null && element.isValid() &&
//           element instanceof PsiDocCommentOwner &&
//           ((PsiDocCommentOwner)element).isDeprecated();
//  }
}