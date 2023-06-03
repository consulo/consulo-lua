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
package com.sylvanaar.idea.Lua.findUsages;

import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.scope.LocalSearchScope;
import consulo.language.psi.search.ReferencesSearch;
import consulo.language.psi.search.SearchSession;
import consulo.project.util.query.QueryExecutorBase;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LuaAliasedElementSearcher extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    public LuaAliasedElementSearcher() {
        super(true);
    }

    @Override
    public void processQuery(@Nonnull ReferencesSearch.SearchParameters parameters, @Nonnull Processor<? super PsiReference> consumer) {
        final PsiElement target = parameters.getElementToSearch();
      if (!(target instanceof PsiNamedElement)) {
        return;
      }

        final String name = ((PsiNamedElement) target).getName();
      if (name == null || StringUtil.isEmptyOrSpaces(name)) {
        return;
      }

        final SearchScope scope = parameters.getEffectiveSearchScope();

        final consulo.language.psi.search.SearchRequestCollector collector = parameters.getOptimizer();
        final SearchSession session = collector.getSearchSession();

        collector.searchWord(name, scope, consulo.language.psi.search.UsageSearchContext.IN_CODE, true, new MyProcessor(target, null, session));
    }

    private static class MyProcessor extends consulo.language.psi.search.RequestResultProcessor {
        private final PsiElement myTarget;
        private final SearchSession mySession;

        MyProcessor(PsiElement target, @Nullable String prefix, SearchSession session) {
            super(target, prefix);
            myTarget = target;
            mySession = session;
        }

        @Override
        public boolean processTextOccurrence(final PsiElement element, int offsetInElement, Processor<? super PsiReference> consumer) {
            String alias = element.getText();
          if (alias == null) {
            return true;
          }

            final PsiReference reference = element.getReference();
            if (reference == null) {
                return true;
            }
            if (!reference.isReferenceTo(myTarget)) {
                return true;
            }

            final consulo.language.psi.search.SearchRequestCollector collector = new consulo.language.psi.search.SearchRequestCollector(mySession);
            final SearchScope fileScope = new LocalSearchScope(element.getContainingFile());
            collector.searchWord(alias, fileScope, consulo.language.psi.search.UsageSearchContext.IN_CODE, true, myTarget);

            return consulo.language.psi.search.PsiSearchHelper.SERVICE.getInstance(element.getProject()).processRequests(collector, consumer);
        }
    }

}
