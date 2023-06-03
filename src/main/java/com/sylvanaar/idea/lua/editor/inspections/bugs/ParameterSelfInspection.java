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

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.psi.PsiElementVisitor;
import com.sylvanaar.idea.lua.editor.inspections.AbstractInspection;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaParameterList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaFunctionDefinitionStatement;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaParameter;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaSymbol;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import org.jetbrains.annotations.Nls;
import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 7/4/11
 * Time: 10:52 AM
 */
public class ParameterSelfInspection extends AbstractInspection {
    @Nls
    @Nonnull
    @Override
    public String getDisplayName() {
       return "Parameter hides implicit self";
    }

    @Override
    public String getStaticDescription() {
        return "Looks for declaration of a parameter self in functions with an implicit self definition";
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
            public void visitFunctionDef(LuaFunctionDefinitionStatement def) {
                super.visitFunctionDef(def);

                // Is this function defined with :
                LuaSymbol name = def.getIdentifier();
                if (!name.getText().contains(":")) return;

                LuaParameterList parameterList = def.getParameters();
                if (parameterList == null) return;

                LuaParameter[] parms = parameterList.getLuaParameters();
                if (parms == null) return;

                for (LuaParameter parm : parms) {
                    if (parm.getText().equals("self"))
                        holder.registerProblem(parm, "Parameter hides implicit self", LocalQuickFix.EMPTY_ARRAY);
                }

            }
        };
    }


}

