/*
 * Copyright 2012 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.lang.psi.types;

import com.sylvanaar.idea.lua.lang.InferenceCapable;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiManager;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaAssignmentStatement;
import com.sylvanaar.idea.lua.lang.psi.util.LuaAssignment;
import consulo.language.psi.PsiFileEx;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.Project;
import consulo.util.lang.ObjectUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/7/12
 * Time: 4:52 AM
 */
public class InferenceUtil {
    public static void inferAssignment(LuaAssignmentStatement statement) {
        boolean canAssign = false;

        for (LuaAssignment a : statement.getAssignments()) {
            final LuaExpression value = a.getValue();

            if (value instanceof InferenceCapable) {
                ((InferenceCapable) value).inferTypes();
            }
        }
    }

    public static void requeueIfPossible(InferenceCapable element) {
        final Boolean userData = ObjectUtil.notNull(element.getContainingFile().getUserData(PsiFileEx.BATCH_REFERENCE_PROCESSING), false);
        if (!userData && !PsiTreeUtil.hasErrorElements(element)) {
            final Project project = element.getProject();
            if (project != null) {
                LuaPsiManager.getInstance(project).queueInferences(element);
            }
        }
    }
}
