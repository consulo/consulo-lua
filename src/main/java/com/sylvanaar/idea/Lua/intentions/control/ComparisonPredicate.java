/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sylvanaar.idea.lua.intentions.control;

import com.sylvanaar.idea.lua.intentions.base.ErrorUtil;
import com.sylvanaar.idea.lua.intentions.base.PsiElementPredicate;
import com.sylvanaar.idea.lua.intentions.utils.ComparisonUtils;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaBinaryExpression;
import consulo.language.psi.PsiElement;


class ComparisonPredicate implements PsiElementPredicate {

  public boolean satisfiedBy(PsiElement element) {
    if (!(element instanceof LuaBinaryExpression)) {
      return false;
    }
    final LuaBinaryExpression expression = (LuaBinaryExpression) element;
    if (!ComparisonUtils.isComparison(expression)) {
      return false;
    }
    return !ErrorUtil.containsError(element);
  }
}
