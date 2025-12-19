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
package com.sylvanaar.idea.lua.refactoring.introduce;

import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import consulo.codeEditor.Editor;
import consulo.language.psi.PsiElement;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Maxim.Medvedev
 */
public class LuaIntroduceContext {
  public final Project project;
  public final Editor editor;
  @Nullable public final LuaExpression expression;
  public final PsiElement[] occurrences;
  public final PsiElement scope;
  @Nullable
  public final LuaSymbol var;
  @Nonnull
  public final PsiElement place;

  public LuaIntroduceContext(Project project, Editor editor, LuaExpression expression, PsiElement[] occurrences,
                             PsiElement scope, @Nullable LuaSymbol var) {
    this.project = project;
    this.editor = editor;
    this.expression = expression;
    this.occurrences = occurrences;
    this.scope = scope;
    this.var = var;
    this.place = expression == null ? var : expression;
  }
}
