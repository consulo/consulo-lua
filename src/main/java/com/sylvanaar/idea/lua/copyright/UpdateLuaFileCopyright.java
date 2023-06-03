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

package com.sylvanaar.idea.lua.copyright;

import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import consulo.language.copyright.UpdatePsiFileCopyright;
import consulo.language.copyright.config.CopyrightFileConfig;
import consulo.language.copyright.config.CopyrightProfile;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiWhiteSpace;

import javax.annotation.Nonnull;

public class UpdateLuaFileCopyright extends UpdatePsiFileCopyright<CopyrightFileConfig> {
    public UpdateLuaFileCopyright(@Nonnull PsiFile psiFile, @Nonnull CopyrightProfile copyrightProfile) {
        super(psiFile, copyrightProfile);
    }

    @Override
    protected boolean accept() {
        return getFile() instanceof LuaPsiFile;
    }

    @Override
    protected void scanFile() {
        PsiElement first = getFile().getFirstChild();
        PsiElement last = first;
        PsiElement next = first;
        while (next != null) {
            if (next instanceof PsiComment || next instanceof PsiWhiteSpace) {
                next = getNextSibling(next);
            }
            else {
                break;
            }
            last = next;
        }

        if (first != null) {
            checkComments(first, last, true);
        }
        else {
            checkComments(null, null, true);
        }
    }
}
