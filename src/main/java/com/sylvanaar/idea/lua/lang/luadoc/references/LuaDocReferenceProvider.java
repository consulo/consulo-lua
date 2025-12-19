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

package com.sylvanaar.idea.lua.lang.luadoc.references;

import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocPsiElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.psi.filter.ElementFilter;
import consulo.language.util.ProcessingContext;
import jakarta.annotation.Nonnull;

/**
 * @author ilyas
 */
public class LuaDocReferenceProvider extends PsiReferenceProvider {
  @Nonnull
  public PsiReference[] getReferencesByElement(PsiElement element) {
    return new PsiReference[0];
  }

  @Nonnull
  public PsiReference[] getReferencesByElement(@Nonnull PsiElement element, @Nonnull ProcessingContext context) {
    //todo review
    return getReferencesByElement(element);
  }

  public static class LuaDocReferenceFilter implements ElementFilter
  {
    public boolean isAcceptable(Object element, PsiElement context) {
      return context instanceof LuaDocPsiElement;
    }

    public boolean isClassAcceptable(Class hintClass) {
      return true;
    }
  }

}
