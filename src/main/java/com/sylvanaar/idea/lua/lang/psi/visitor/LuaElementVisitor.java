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

package com.sylvanaar.idea.lua.lang.psi.visitor;

import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocFieldReference;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocPsiElement;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocReferenceElement;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocTag;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.*;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiKeywordImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiTokenImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.statements.LuaRepeatStatementImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.symbols.LuaCompoundReferenceElementImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.symbols.LuaPsiDeclarationReferenceElementImpl;
import com.sylvanaar.idea.lua.lang.psi.statements.*;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaCompoundIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaParameter;
import consulo.application.progress.ProgressIndicatorProvider;
import consulo.language.psi.PsiElementVisitor;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 7:39:03 AM
 */

public class LuaElementVisitor extends PsiElementVisitor
{
    public void visitElement(LuaPsiElement element) {
        ProgressIndicatorProvider.checkCanceled();
    }

//    @Override
//    public void visitFile(PsiFile e) {
//        if (e instanceof LuaPsiFile)
//            visitFile((LuaPsiFile)e);
//        else
//            visitElement(e);
//    }
//
//    public void visitFile(LuaPsiFile e) {
//            visitElement(e);
//    }


    public void visitFunctionDef(LuaFunctionDefinitionStatement e) {
        visitStatement(e);
    }

    public void visitAssignment(LuaAssignmentStatement e) {
        visitStatement(e);
    }

    public void visitIdentifier(LuaIdentifier e) {
        visitElement(e);
    }

    public void visitStatement(LuaStatementElement e) {
        visitElement(e);
    }

    public void visitNumericForStatement(LuaNumericForStatement e) {
        visitStatement(e);
    }

    public void visitBlock(LuaBlock e) {
        visitElement(e);
    }

    public void visitGenericForStatement(LuaGenericForStatement e) {
        visitStatement(e);
    }

    public void visitIfThenStatement(@Nonnull LuaIfThenStatement e) {
        visitStatement(e);
    }

    public void visitWhileStatement(LuaWhileStatement e) {
        visitStatement(e);
    }

    public void visitParameter(LuaParameter e) {
        visitElement(e);
    }

    public void visitReturnStatement(LuaReturnStatement e) {
        visitStatement(e);
    }

    public void visitReferenceElement(LuaReferenceElement e) {
        visitElement(e);
    }

    public void visitKeyword(LuaPsiKeywordImpl e) {
        visitElement(e);
    }

    public void visitLuaToken(LuaPsiTokenImpl e) {
        visitElement(e);
    }

    public void visitDeclarationStatement(LuaDeclarationStatement e) {
        if (e instanceof LuaAssignmentStatement)
            visitAssignment((LuaAssignmentStatement) e);
        else
            visitStatement(e);
    }

    public void visitDeclarationExpression(LuaDeclarationExpression e) {
        if (e instanceof LuaReferenceElement)
            visitReferenceElement((LuaReferenceElement) e);
        else
            visitElement(e);
    }

    public void visitLiteralExpression(LuaLiteralExpression e) {
        visitElement(e);
    }

    public void visitTableConstructor(LuaTableConstructor e) {
        visitElement(e);
    }

    public void visitUnaryExpression(LuaUnaryExpression e) {
        visitElement(e);
    }

    public void visitBinaryExpression(LuaBinaryExpression e) {
        visitElement(e);
    }

    public void visitFunctionCall(LuaFunctionCallExpression e) {
        visitElement(e);
    }

    public void visitBreakStatement(LuaBreakStatement e) {
        visitStatement(e);
    }

    public void visitRepeatStatement(LuaRepeatStatementImpl e) {
        visitStatement(e);
    }

    public void visitFunctionCallStatement(LuaFunctionCallStatement e) {
        visitStatement(e);
    }

    public void visitCompoundIdentifier(LuaCompoundIdentifier e) {
        visitElement(e);
    }

    public void visitCompoundReference(LuaCompoundReferenceElementImpl e) {
        visitReferenceElement(e);
    }

    public void visitModuleExpression(LuaModuleExpression e) {
        visitDeclarationExpression(e);
    }

    public void visitRequireExpression(LuaRequireExpression e) {
        visitFunctionCall(e);
    }

    public void visitDocTag(LuaDocTag e) {
        visitDocComment(e);
    }

    public void visitDocFieldReference(LuaDocFieldReference e) {
        visitDocComment(e);
    }

    public void visitDoStatement(LuaDoStatement e) {
        visitStatement(e);
    }

    public void visitDocComment(LuaDocPsiElement e) {
        visitElement(e);
    }

    public void visitAnonymousFunction(LuaAnonymousFunctionExpression e) {
        visitElement(e);
    }

    public void visitDocReference(LuaDocReferenceElement e) {
        visitDocComment(e);
    }

    public void visitKeyValueInitializer(LuaKeyValueInitializer e) {
        visitElement(e);
    }

    public void visitParenthesizedExpression(LuaParenthesizedExpression e) {
        visitElement(e);
    }

    public void visitReferenceElement_NoRecurse(LuaPsiDeclarationReferenceElementImpl e) {
    }
}




