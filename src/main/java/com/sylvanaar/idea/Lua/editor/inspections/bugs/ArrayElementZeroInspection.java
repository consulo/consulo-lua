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

package com.sylvanaar.idea.Lua.editor.inspections.bugs;

import javax.annotation.Nonnull;

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElementVisitor;
import com.sylvanaar.idea.Lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.Lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.Lua.lang.psi.expressions.LuaLiteralExpression;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaCompoundIdentifier;
import com.sylvanaar.idea.Lua.lang.psi.types.LuaPrimitiveType;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import org.jetbrains.annotations.Nls;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/13/11
 * Time: 7:51 PM
 */
public class ArrayElementZeroInspection  extends AbstractInspection {
    @Nls
    @Nonnull
    @Override
    public String getDisplayName() {
        return "Use of element 0";
    }

    @Override
    public String getStaticDescription() {
        return "Lua arrays are 1 based. This checks for array access of element 0.";
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
            @Override
            public void visitCompoundIdentifier(LuaCompoundIdentifier e) {
                super.visitCompoundIdentifier(e);

                LuaExpression symbol = e.getRightSymbol();

                if (symbol instanceof LuaLiteralExpression) {
                    if (symbol.getLuaType() == LuaPrimitiveType.NUMBER && symbol.getText().equals("0"))
                        holder.registerProblem(e, "Use of element 0", LocalQuickFix.EMPTY_ARRAY);
                }
            }
        };
    }


}

