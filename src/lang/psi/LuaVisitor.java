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

package com.sylvanaar.idea.Lua.lang.psi;

import com.intellij.psi.PsiElementVisitor;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaFunctionDefinitionStatement;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 7:39:03 AM
 */

public class LuaVisitor extends PsiElementVisitor {
    public void visitFile(LuaPsiFile file) {
        visitElement(file);
    }

    public void visitFunctionDef(LuaFunctionDefinitionStatement functionDef) {
        visitElement(functionDef);
    }

    public void visitIdentifier(LuaIdentifier id) {
        visitElement(id);
    }

}

