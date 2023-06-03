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

package com.sylvanaar.idea.lua.lang.formatter;

import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.lang.LuaLanguage;
import com.sylvanaar.idea.lua.lang.formatter.blocks.LuaFormattingBlock;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.codeStyle.*;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 13.07.2009
 * Time: 22:51:23
 */
@ExtensionImpl
public class LuaFormattingModelBuilder implements FormattingModelBuilder {
    @Nonnull
    public FormattingModel createModel(FormattingContext formattingContext) {
        PsiElement element = formattingContext.getPsiElement();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();

        ASTNode node = element.getNode();
        assert node != null;
        PsiFile containingFile = element.getContainingFile().getViewProvider().getPsi(LuaFileType.LUA_LANGUAGE);
        ASTNode astNode = containingFile.getNode();
        assert astNode != null;

        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile,
                new LuaFormattingBlock(astNode, null, Indent.getAbsoluteNoneIndent(), null, settings), settings);

    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
