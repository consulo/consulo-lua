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
package com.sylvanaar.idea.lua.lang.structure.impl;

import consulo.fileEditor.structureView.tree.TreeElement;
import consulo.navigation.ItemPresentation;
import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.structure.LuaStructureViewTreeElement;
import com.sylvanaar.idea.lua.lang.structure.itemsPresentations.impl.LuaFunctionItemPresentation;
import consulo.fileEditor.structureView.StructureViewTreeElement;

public class LuaFunctionStructureViewElement extends LuaStructureViewTreeElement {

  public LuaFunctionStructureViewElement(LuaPsiElement element) {
    super(element);
  }

  public ItemPresentation getPresentation() {
    return new LuaFunctionItemPresentation((LuaFunctionDefinition) myElement);
  }

  public TreeElement[] getChildren() {
    return StructureViewTreeElement.EMPTY_ARRAY;
  }


}
