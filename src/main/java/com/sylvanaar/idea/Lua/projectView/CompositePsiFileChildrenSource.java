/*
 * Copyright 2012 Jon S Akhtar (Sylvanaar)
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
import consulo.language.psi.PsiElement;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 12/22/11
 * Time: 1:23 PM
 */
public class CompositePsiFileChildrenSource implements LuaPsiFileChildrenSource {
  private final List<LuaPsiFileChildrenSource> mySources;

  public CompositePsiFileChildrenSource(LuaPsiFileChildrenSource... sources) {
    mySources = Arrays.asList(sources);
  }

  @Override
  public void addChildren(LuaPsiFile psiFile, List<PsiElement> children) {
    for (LuaPsiFileChildrenSource source : mySources) {
      source.addChildren(psiFile, children);
    }
  }
}