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
package com.sylvanaar.idea.lua.lang.psi.impl;

import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.lang.InferenceCapable;
import com.sylvanaar.idea.lua.lang.psi.*;
import com.sylvanaar.idea.lua.lang.psi.controlFlow.Instruction;
import com.sylvanaar.idea.lua.lang.psi.controlFlow.impl.ControlFlowBuilder;
import com.sylvanaar.idea.lua.lang.psi.expressions.*;
import com.sylvanaar.idea.lua.lang.psi.statements.*;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaCompoundIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaIdentifier;
import com.sylvanaar.idea.lua.lang.psi.symbols.LuaLocalIdentifier;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaRecursiveElementVisitor;
import com.sylvanaar.idea.lua.util.LuaAtomicNotNullLazyValue;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFileEx;
import consulo.language.psi.PsiModificationTracker;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.util.IncorrectOperationException;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.*;

/**
 * @author Jon S Akhtar
 * @since 2010-04-10
 */
public class LuaPsiFileImpl extends LuaPsiFileBaseImpl implements LuaPsiFile, PsiFileEx, LuaPsiFileBase, LuaExpressionCodeFragment {
    private static final Logger log = Logger.getInstance("Lua.LuaPsiFileImp");
    private PsiElement myContext = null;

    public LuaPsiFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, LuaFileType.LUA_LANGUAGE);
    }

    @Nonnull
    @Override
    public FileType getFileType() {
        return LuaFileType.LUA_FILE_TYPE;
    }

    @Override
    @RequiredReadAction
    public String toString() {
        return "Lua script: " + getName();
    }

    @Override
    @RequiredWriteAction
    public LuaStatementElement addStatementBefore(@Nonnull LuaStatementElement statement, LuaStatementElement anchor)
        throws IncorrectOperationException {
        return (LuaStatementElement) addBefore(statement, anchor);
    }

    Modules moduleStatements = new Modules();

    @Override
    public LuaModuleExpression getModuleAtOffset(int offset) {
        List<LuaModuleExpression> modules = moduleStatements.getValue();
        if (modules.size() == 0) {
            return null;
        }

        LuaModuleExpression module = null;
        for (LuaModuleExpression m : modules) {
            if (m.getIncludedTextRange().contains(offset)) {
                module = module == null ? m :
                    m.getIncludedTextRange().getStartOffset() >
                        module.getIncludedTextRange().getStartOffset() ? m : module;
            }
        }

        return module;
    }

    @Override
    public void setContext(PsiElement e) {
        myContext = e;
    }


    @Override
    public PsiElement getContext() {
        if (myContext != null) {
            return myContext;
        }

        return super.getContext();
    }

    @Override
    @Nullable
    public String getModuleNameAtOffset(int offset) {
        LuaModuleExpression module = getModuleAtOffset(offset);
        return module == null ? null : module.getGlobalEnvironmentName();
    }

    @Override
    public void clearCaches() {
        super.clearCaches();
        moduleStatements.drop();
        functionDefs.drop();
        putUserData(CONTROL_FLOW, null);
    }

    @Override
    @RequiredReadAction
    public LuaExpression getReturnedValue() {
        // This only works for the last statement in the file
        LuaStatementElement[] stmts = getStatements();
        if (stmts.length == 0) {
            return null;
        }

        LuaStatementElement s = stmts[stmts.length - 1];
        if (!(s instanceof LuaReturnStatement)) {
            return null;
        }

        return ((LuaReturnStatement) s).getReturnValue();
    }

    @Override
    @RequiredReadAction
    public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) {
        PsiElement run = lastParent == null ? getLastChild() : lastParent.getPrevSibling();
        if (run != null && run.getParent() != this) {
            run = null;
        }
        while (run != null) {
            if (!run.processDeclarations(processor, state, null, place)) {
                return false;
            }
            run = run.getPrevSibling();
        }

        return true;
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitFile(this);
    }

    @Override
    @RequiredReadAction
    public void acceptChildren(LuaElementVisitor visitor) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof LuaPsiElement childElem) {
                childElem.accept(visitor);
            }

            child = child.getNextSibling();
        }
    }

    @Override
    @RequiredReadAction
    public LocalizeValue getPresentationText() {
        return LocalizeValue.ofNullable(getName());
    }

    @Override
    public Assignable[] getSymbolDefs() {
        final Set<Assignable> decls = new HashSet<>();

        LuaElementVisitor v = new LuaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationExpression(LuaDeclarationExpression e) {
                super.visitDeclarationExpression(e);
                if (!(e instanceof LuaLocalIdentifier)) {
                    decls.add(e);
                }
            }

            @Override
            public void visitCompoundIdentifier(LuaCompoundIdentifier e) {
                super.visitCompoundIdentifier(e);

                if (e.isAssignedTo()) {
                    decls.add(e);
                }
            }
        };

        v.visitElement(this);

        return decls.toArray(new Assignable[decls.size()]);
    }


    @Override
    public LuaStatementElement[] getAllStatements() {
        final List<LuaStatementElement> stats = new ArrayList<>();

        LuaElementVisitor v = new LuaRecursiveElementVisitor() {
            @Override
            public void visitElement(LuaPsiElement e) {
                super.visitElement(e);
                if (e instanceof LuaStatementElement) {
                    stats.add((LuaStatementElement) e);
                }
            }
        };

        v.visitElement(this);

        return stats.toArray(new LuaStatementElement[stats.size()]);
    }

    @Override
    @RequiredReadAction
    public LuaStatementElement[] getStatements() {
        return findChildrenByClass(LuaStatementElement.class);
    }

    LuaFunctionDefinitionNotNullLazyValue functionDefs = new LuaFunctionDefinitionNotNullLazyValue();

    @Override
    public LuaFunctionDefinition[] getFunctionDefs() {
        return functionDefs.getValue();
    }

    @Override
    @RequiredReadAction
    public synchronized Instruction[] getControlFlow() {
        assert isValid();
        if (getStub() != null) {
            return EMPTY_CONTROL_FLOW;
        }

        return CachedValuesManager.getManager(getProject()).getCachedValue(
            this,
            CONTROL_FLOW,
            () -> {
                Instruction[] value =
                    new ControlFlowBuilder(getProject()).buildControlFlow(LuaPsiFileImpl.this);

                if (value == null || value.length > MAX_CONTROL_FLOW_LEN) {
                    value = EMPTY_CONTROL_FLOW;
                }

                return CachedValueProvider.Result.create(
                    value,
                    value == EMPTY_CONTROL_FLOW ? null : PsiModificationTracker.MODIFICATION_COUNT
                );
            },
            false
        );
    }

    @Override
    public void removeVariable(LuaIdentifier variable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LuaDeclarationStatement addVariableDeclarationBefore(
        LuaDeclarationStatement declaration,
        LuaStatementElement anchor
    ) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @RequiredReadAction
    public void inferTypes() {
        log.debug("start infer " + getName());
        final LuaPsiManager m = LuaPsiManager.getInstance(getProject());
        LuaElementVisitor v = new LuaRecursiveElementVisitor() {
            @Override
            public void visitElement(LuaPsiElement element) {
                super.visitElement(element);
                if (element instanceof InferenceCapable inferenceCapable && element != LuaPsiFileImpl.this) {
                    m.queueInferences(inferenceCapable);
                }
            }
        };

        acceptChildren(v);
        log.debug("end infer " + getName());
    }

    private class LuaFunctionDefinitionNotNullLazyValue extends LuaAtomicNotNullLazyValue<LuaFunctionDefinition[]> {
        @Nonnull
        @Override
        protected LuaFunctionDefinition[] compute() {
            final List<LuaFunctionDefinition> funcs = new ArrayList<>();

            LuaElementVisitor v = new LuaRecursiveElementVisitor() {
                @Override
                public void visitFunctionDef(LuaFunctionDefinitionStatement e) {
                    super.visitFunctionDef(e);
                    funcs.add(e);
                }

                @Override
                public void visitAnonymousFunction(LuaAnonymousFunctionExpression e) {
                    super.visitAnonymousFunction(e);
                    if (e.getName() != null) {
                        funcs.add(e);
                    }
                }
            };

            v.visitElement(LuaPsiFileImpl.this);

            return funcs.toArray(new LuaFunctionDefinition[funcs.size()]);
        }
    }

    // Only looks at the current block
    private class LuaModuleVisitor extends LuaElementVisitor {
        private final Collection<LuaModuleExpression> list;

        public LuaModuleVisitor(Collection<LuaModuleExpression> list) {
            this.list = list;
        }

        @Override
        public void visitFunctionCallStatement(LuaFunctionCallStatement e) {
            super.visitFunctionCallStatement(e);
            e.acceptChildren(this);
        }

        @Override
        public void visitModuleExpression(LuaModuleExpression e) {
            super.visitModuleExpression(e);
            list.add(e);
        }
    }

    private class Modules extends LuaAtomicNotNullLazyValue<List<LuaModuleExpression>> {
        @Nonnull
        @Override
        @RequiredReadAction
        protected List<LuaModuleExpression> compute() {
            List<LuaModuleExpression> val = new ArrayList<>();
            acceptChildren(new LuaModuleVisitor(val));
            return val;
        }
    }
}
