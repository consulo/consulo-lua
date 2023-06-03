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

package com.sylvanaar.idea.lua.editor.inspections.bugs;

import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.util.PsiTreeUtil;
import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaBlock;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaGlobal;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 7/4/11
 * Time: 10:11 AM
 */
public class GlobalCreationOutsideOfMainChunk extends AbstractInspection {
    @Nls
    @Nonnull
    @Override
    public String getDisplayName() {
       return "Suspicious global creation";
    }

    @Override
    public String getStaticDescription() {
        return "Looks for creation of globals in scopes other than the main chunk";
    }

    @Nonnull
    @Override
    public String getGroupDisplayName() {
        return PROBABLE_BUGS;
    }

    @Nonnull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {
            List<String> validGlobals = new ArrayList<String>();
            public void visitDeclarationExpression(LuaDeclarationExpression var) {
                super.visitDeclarationExpression(var);

                if (var instanceof LuaGlobal) {
                    LuaBlock block = PsiTreeUtil.getParentOfType(var, LuaBlock.class);
                    if (block == null) return;

                    if (block instanceof LuaPsiFile) {
                        validGlobals.add(var.getName());
                        return;
                    }

                    if (!validGlobals.contains(var.getName()))
                        holder.registerProblem(var, "Suspicious global creation ("+var.getName()+")", LocalQuickFix.EMPTY_ARRAY);
                }
            }
        };
    }
}
