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
package com.sylvanaar.idea.lua.editor.annotator;

import com.sylvanaar.idea.lua.editor.highlighter.LuaHighlightingData;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiElement;
import com.sylvanaar.idea.lua.lang.psi.LuaReferenceElement;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaDeclarationExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaFieldIdentifier;
import com.sylvanaar.idea.lua.lang.psi.impl.symbols.LuaGlobalDeclarationImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.symbols.LuaGlobalUsageImpl;
import com.sylvanaar.idea.lua.lang.psi.impl.symbols.LuaLocalDeclarationImpl;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaExpressionList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaDeclarationStatement;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaLocalDefinitionStatement;
import com.sylvanaar.idea.lua.lang.psi.statements.LuaReturnStatement;
import com.sylvanaar.idea.lua.lang.psi.symbols.*;
import com.sylvanaar.idea.lua.lang.psi.visitor.LuaElementVisitor;
import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.document.util.TextRange;
import consulo.language.editor.annotation.Annotation;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.Annotator;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;

import javax.annotation.Nonnull;


/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 8, 2010
 * Time: 5:45:21 PM
 */
public class LuaAnnotator extends LuaElementVisitor implements Annotator {
    private AnnotationHolder myHolder = null;

    @Override
    public void annotate(@Nonnull PsiElement element, @Nonnull AnnotationHolder holder) {
        if (element instanceof LuaPsiElement) {
            myHolder = holder;
            ((LuaPsiElement) element).accept(this);
            myHolder = null;
        }
    }

    public void visitReturnStatement(LuaReturnStatement stat) {
        super.visitReturnStatement(stat);

        if (stat.isTailCall()) {
            final Annotation a = myHolder.createInfoAnnotation(stat, null);
            a.setTextAttributes(LuaHighlightingData.TAIL_CALL);
        }
    }

    @Override
    public void visitDocReference(LuaDocReferenceElement ref) {
        super.visitDocReference(ref);

        PsiElement e = ref.resolve();

        hilightReference(ref, e);
    }

//    @Override
//    public void visitCompoundReference(LuaCompoundReferenceElementImpl ref) {
//    }

    public void visitReferenceElement(LuaReferenceElement ref) {
        super.visitReferenceElement(ref);
        LuaSymbol e;

        e = (LuaSymbol) ref.resolve();

        if (e != null) {
            hilightReference(ref, e);
        }
    }


    private void hilightReference(PsiReference ref, PsiElement e) {
        if (e instanceof LuaParameter) {
            final Annotation a = myHolder.createInfoAnnotation((PsiElement) ref, null);
            a.setTextAttributes(LuaHighlightingData.PARAMETER);
//        }
//        else if (ref.getElement() instanceof LuaUpvalueIdentifier) {
//            final Annotation a = myHolder.createInfoAnnotation((PsiElement)ref, null);
//            a.setTextAttributes(LuaHighlightingData.UPVAL);
        }
        else if (e instanceof LuaIdentifier) {
            LuaIdentifier id = (LuaIdentifier) e;
            TextAttributesKey attributesKey = null;

            if (id instanceof LuaGlobal) {
                attributesKey = LuaHighlightingData.GLOBAL_VAR;
            }
            else if (id instanceof LuaLocal && !id.getText().equals("...")) {
                attributesKey = LuaHighlightingData.LOCAL_VAR;
            }
            else if (id instanceof LuaFieldIdentifier) {
                attributesKey = LuaHighlightingData.FIELD;
            }

            if (attributesKey != null) {
                final Annotation annotation = myHolder.createInfoAnnotation(ref.getElement(), null);
                annotation.setTextAttributes(attributesKey);
            }
        }
    }

//    @Override
//    public void visitKeyValueInitializer(LuaKeyValueInitializer e) {
//        super.visitKeyValueInitializer(e);
//         e.getFieldKey().setLuaType(e.getFieldValue().getLuaType());
//    }

    @Override
    public void visitDeclarationStatement(LuaDeclarationStatement e) {
        super.visitDeclarationStatement(e);

        if (e instanceof LuaLocalDefinitionStatement) {
            LuaIdentifierList left = ((LuaLocalDefinitionStatement) e).getLeftExprs();
            LuaExpressionList right = ((LuaLocalDefinitionStatement) e).getRightExprs();

            if (right == null || right.count() == 0) {
                return;
            }

            boolean allNil = true;
            for (LuaExpression expr : right.getLuaExpressions())
                if (!expr.getText().equals("nil")) {
                    allNil = false;
                    break;
                }

            if (allNil) {
                int assignment = ((LuaLocalDefinitionStatement) e).getOperatorElement().getTextOffset();
                final Annotation annotation =
                        myHolder.createInfoAnnotation(new TextRange(assignment, right.getTextRange().getEndOffset()),
                                null);
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT);
            }
        }
    }


    public void visitDeclarationExpression(LuaDeclarationExpression dec) {
        if (!(dec.getContext() instanceof LuaParameter)) {
            final Annotation a = myHolder.createInfoAnnotation(dec, null);

            if (dec instanceof LuaLocalDeclarationImpl) {
                a.setTextAttributes(LuaHighlightingData.LOCAL_VAR);
            }
            else if (dec instanceof LuaGlobalDeclarationImpl) {
                a.setTextAttributes(LuaHighlightingData.GLOBAL_VAR);
            }
        }
    }

    public void visitParameter(LuaParameter id) {
        if (id.getTextLength() == 0) {
            return;
        }

        addSemanticHighlight(id, LuaHighlightingData.PARAMETER);
    }

    public void visitIdentifier(LuaIdentifier id) {
        if ((id != null) && id instanceof LuaGlobalUsageImpl) {
            addSemanticHighlight(id, LuaHighlightingData.GLOBAL_VAR);
            return;
        }
        if (id instanceof LuaFieldIdentifier) {
            addSemanticHighlight(id, LuaHighlightingData.FIELD);
            return;
        }
        if (id instanceof LuaUpvalueIdentifier) {
            addSemanticHighlight(id, LuaHighlightingData.UPVAL);
        }
        //        if (id instanceof LuaLocalIdentifier) {
        //            final Annotation annotation = myHolder.createInfoAnnotation(id, null);
        //            annotation.setTextAttributes(LuaHighlightingData.LOCAL_VAR);
        //        }

    }

    private void addSemanticHighlight(LuaIdentifier id, TextAttributesKey key) {
        myHolder.createInfoAnnotation(id, null).setTextAttributes(key);
    }
}
