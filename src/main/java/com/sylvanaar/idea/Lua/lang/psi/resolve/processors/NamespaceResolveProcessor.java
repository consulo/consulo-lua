/*
 * Copyright 2013 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.Lua.lang.psi.resolve.processors;

import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.ResolveState;
import consulo.util.dataholder.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/26/13
 * Time: 1:50 AM
 */
public class NamespaceResolveProcessor extends ResolveProcessor {
    public NamespaceResolveProcessor(String name) {
        super(name);
    }

    @Override
    public boolean execute(@Nonnull PsiElement element, ResolveState state) {

        return true;
    }

    @Nullable
    @Override
    public <T> T getHint(@Nonnull Key<T> hintKey) {
        return null;
    }
}
