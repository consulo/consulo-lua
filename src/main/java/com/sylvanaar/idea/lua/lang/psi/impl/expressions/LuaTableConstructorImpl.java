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

package com.sylvanaar.idea.lua.lang.psi.impl.expressions;

import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.lua.lang.luadoc.psi.impl.LuaDocCommentUtil;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaFieldIdentifier;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaKeyValueInitializer;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaTableConstructor;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaStubElementBase;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaAssignmentStatement;
import com.sylvanaar.idea.lua.lang.psi.stubs.api.LuaTableStub;
import com.sylvanaar.idea.lua.lang.psi.types.LuaNamespacedType;
import com.sylvanaar.idea.lua.lang.psi.types.LuaTable;
import com.sylvanaar.idea.lua.lang.psi.types.LuaType;
import com.sylvanaar.idea.lua.lang.psi.types.StubType;
import com.sylvanaar.idea.lua.lang.psi.util.LuaAssignment;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.util.PsiTreeUtil;
import jakarta.annotation.Nonnull;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 16, 2010
 * Time: 10:43:51 PM
 */
public class LuaTableConstructorImpl extends LuaStubElementBase<LuaTableStub> implements LuaTableConstructor {
    LuaType myType;

    public LuaTableConstructorImpl(ASTNode node) {
        super(node);
        myType = new LuaTable();
    }

    public LuaTableConstructorImpl(LuaTableStub stub) {
        super(stub, LuaElementTypes.TABLE_CONSTUCTOR);
        final byte[] encodedType = stub.getEncodedType();
        myType = encodedType != null ? new StubType(encodedType) : new LuaTable();
    }

    @Override
    public String toString() {
        return "Table Constructor (Field Count " + count() + ")";
    }

    public int count() {
        return getLuaExpressions().length;
    }

    public  LuaExpression[] getLuaExpressions() {
        return findChildrenByClass(LuaExpression.class);
    }

    public LuaExpression[] getInitializers() {
        return getLuaExpressions();
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitTableConstructor(this);
    }

    @Override
    public void accept(@Nonnull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitTableConstructor(this);
        } else {
            visitor.visitElement(this);
        }
    }



    @Override
    public PsiElement replaceWithExpression(LuaExpression newCall, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public LuaType getLuaType() {
        if (myType instanceof StubType)
            myType = ((StubType) myType).get();
        return myType;
    }

    @Override
    public void setLuaType(LuaType type) {
        assert false;
    }

    @Override
    public Object evaluate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map getFields() {
        assert getLuaType() instanceof LuaTable;
        return ((LuaTable) getLuaType()).getFieldSet();
    }

    @Override
    public LuaDocComment getDocComment() {
        return LuaDocCommentUtil.findDocComment(this);
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public String getName() {
        if (getStub() != null)
            return null;

        LuaExpressionList exprlist = PsiTreeUtil.getParentOfType(this, LuaExpressionList.class);
        if (exprlist == null) return null;

        PsiElement assignment = exprlist.getParent();

        if (assignment instanceof LuaAssignmentStatement)
            for (LuaAssignment assign : ((LuaAssignmentStatement) assignment).getAssignments())
                if (assign.getValue() == this) {
                    final String name = assign.getSymbol().getName();

                    assert myType instanceof LuaNamespacedType;
                    ((LuaNamespacedType) myType).setNamespace(name);

                    return name;
                }

        return null;
    }


    @Override
    public void inferTypes() {
        LuaTable type = (LuaTable) getLuaType();
        final String name = getName();

        final LuaKeyValueInitializer[] keyValueInitializers = findChildrenByClass(LuaKeyValueInitializer.class);
        for (LuaKeyValueInitializer kv : keyValueInitializers) {
            final LuaExpression key = kv.getFieldKey();
            final LuaType valueType = kv.getFieldValue().getLuaType();
            if (key instanceof LuaStringLiteralExpressionImpl) {
                type.addPossibleElement(((LuaStringLiteralExpressionImpl) key).getStringContent(), valueType);
            } else if (key instanceof LuaFieldIdentifier) {
                LuaType keyType = key.getLuaType();
                if (keyType instanceof LuaNamespacedType)
                    ((LuaNamespacedType) keyType).setNamespace(name);
                if (valueType instanceof LuaNamespacedType)
                    ((LuaNamespacedType) valueType).setNamespace(key.getName());
                type.addPossibleElement(key.getName(), valueType);
            }
        }
    }
}
