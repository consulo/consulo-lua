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

import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.statements.*;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import jakarta.annotation.Nonnull;


class CyclomaticComplexityVisitor extends LuaElementVisitor {
  private int complexity = 1;

  public void visitElement(LuaPsiElement GrElement) {
    int oldComplexity = 0;
    if (GrElement instanceof LuaFunctionDefinitionStatement) {
      oldComplexity = complexity;
    }
    super.visitElement(GrElement);

    if (GrElement instanceof LuaFunctionDefinitionStatement) {
      complexity = oldComplexity;
    }
  }

  public void visitNumericForStatement(@Nonnull LuaNumericForStatement statement) {
    super.visitNumericForStatement(statement);
    complexity++;
  }

  public void visitGenericForStatement(@Nonnull LuaGenericForStatement statement) {
    super.visitGenericForStatement(statement);
    complexity++;
  }
    
  public void visitIfThenStatement(@Nonnull LuaIfThenStatement statement) {
    super.visitIfThenStatement(statement);
    complexity++;

    complexity += statement.getElseIfConditions().length;
  }

//  public void visitConditionalExpression(LuaConditionalExpression expression) {
//    super.visitConditionalExpression(expression);
//    complexity++;
//  }

  public void visitWhileStatement(@Nonnull LuaWhileStatement statement) {
    super.visitWhileStatement(statement);
    complexity++;
  }

  public int getComplexity() {
    return complexity;
  }
}