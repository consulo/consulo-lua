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

package com.sylvanaar.idea.Lua.lang.psi.impl.statements;

import javax.annotation.Nonnull;

import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.application.util.CachedValue;
import consulo.language.util.IncorrectOperationException;
import com.sylvanaar.idea.Lua.lang.psi.controlFlow.Instruction;
import com.sylvanaar.idea.Lua.lang.psi.controlFlow.impl.ControlFlowBuilder;
import com.sylvanaar.idea.Lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.Lua.lang.psi.impl.LuaPsiElementImpl;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaBlock;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaDeclarationStatement;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaReturnStatement;
import com.sylvanaar.idea.Lua.lang.psi.statements.LuaStatementElement;
import com.sylvanaar.idea.Lua.lang.psi.symbols.LuaIdentifier;
import com.sylvanaar.idea.Lua.lang.psi.util.LuaPsiUtils;
import com.sylvanaar.idea.Lua.lang.psi.visitor.LuaElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 10:17:49 PM
 */
public class LuaBlockImpl extends LuaPsiElementImpl implements LuaBlock {
    public LuaBlockImpl(ASTNode node) {
        super(node);
    }
    
    public void accept(LuaElementVisitor visitor) {
        visitor.visitBlock(this);
    }

    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {

            ((LuaElementVisitor) visitor).visitBlock(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public LuaStatementElement[] getStatements() {
        return findChildrenByClass(LuaStatementElement.class);
    }

    @Override
    public LuaExpression getReturnedValue() {
        // This only works for the last statement in the file
        LuaStatementElement[] stmts = getStatements();
        if (stmts.length==0) return null;

        LuaStatementElement s = stmts[stmts.length-1];
        if (! (s instanceof LuaReturnStatement)) return null;

        return ((LuaReturnStatement) s).getReturnValue();
    }


    //    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
//                                       @NotNull ResolveState resolveState,
//                                       PsiElement lastParent,
//                                       @NotNull PsiElement place) {
//
//        PsiElement parent = place.getParent();
//        while (parent != null && !(parent instanceof LuaPsiFile)) {
//            if (parent == this) {
//                if (!processor.execute(this, resolveState)) return false;
//            }
//
//            parent = parent.getParent();
//        }
//
//        return true;
//    }

//    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
//        if (lastParent != null && lastParent.getParent() == this) {
//
//        }
////        return ResolveUtil.processChildren(this, processor, state, lastParent, place);
//    }

    @Override
    public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place) {
        return LuaPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
    }

    @Override
    public Instruction[] getControlFlow() {
//        java.lang.Thread.dumpStack();

        assert isValid();
        CachedValue<Instruction[]> controlFlow = getUserData(CONTROL_FLOW);
        if (controlFlow == null) {
            controlFlow = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<Instruction[]>() {
                        @Override
                        public Result<Instruction[]> compute() {
                            return Result.create(new ControlFlowBuilder(getProject()).buildControlFlow(LuaBlockImpl.this), getContainingFile());
                        }
                    }, false);
            putUserData(CONTROL_FLOW, controlFlow);
        }

        return controlFlow.getValue();
    }

    @Override
    public LuaStatementElement addStatementBefore(@Nonnull LuaStatementElement statement,
                                                  LuaStatementElement anchor) throws IncorrectOperationException
	{
        return (LuaStatementElement) addBefore(statement, anchor);
    }

    @Override
    public void removeVariable(LuaIdentifier variable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LuaDeclarationStatement addVariableDeclarationBefore(LuaDeclarationStatement declaration, LuaStatementElement anchor) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
