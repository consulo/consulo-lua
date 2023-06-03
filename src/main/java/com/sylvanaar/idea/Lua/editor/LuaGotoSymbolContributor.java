/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.editor;

import com.sylvanaar.idea.lua.lang.psi.LuaPsiManager;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.resolve.ResolveUtil;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.ide.navigation.GotoSymbolContributor;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.FindSymbolParameters;
import consulo.language.psi.stub.IdFilter;
import consulo.navigation.NavigationItem;
import consulo.project.Project;
import consulo.project.content.scope.ProjectAwareFileFilter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 11/23/10
 * Time: 3:38 PM
 */
@ExtensionImpl
public class LuaGotoSymbolContributor implements GotoSymbolContributor {
    @Override
    public void processNames(@Nonnull Processor<String> processor, @Nonnull SearchScope searchScope, @Nullable IdFilter idFilter) {
        ProjectAwareFileFilter fileFilter = (ProjectAwareFileFilter) searchScope;

        final Collection<LuaDeclarationExpression> globals = LuaPsiManager.getInstance(fileFilter.getProject()).getFilteredGlobalsCache();

        for (LuaDeclarationExpression global : globals) {
            processor.process(global.getDefinedName());
        }
    }

    @Override
    public void processElementsWithName(@Nonnull String s, @Nonnull Processor<NavigationItem> processor, @Nonnull FindSymbolParameters findSymbolParameters) {
        boolean includeNonProjectItems = findSymbolParameters.isSearchInLibraries();
        Project project = findSymbolParameters.getProject();

        GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);

        final Collection<LuaDeclarationExpression> globals = ResolveUtil.getFilteredGlobals(project, scope);

        for (LuaDeclarationExpression global : globals) {
            if (!includeNonProjectItems && !scope.contains(global.getContainingFile().getVirtualFile())) {
                continue;
            }

            if (global.getDefinedName().startsWith(s)) {
                processor.process(new BaseNavigationItem(global, global.getDefinedName(), IconDescriptorUpdaters.getIcon(global, 0)));
            }
        }
    }
}
