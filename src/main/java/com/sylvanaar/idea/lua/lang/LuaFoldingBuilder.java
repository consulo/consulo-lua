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

package com.sylvanaar.idea.lua.lang;

import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocComment;
import com.sylvanaar.idea.lua.lang.psi.LuaFunctionDefinition;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaTableConstructor;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.application.progress.ProgressManager;
import consulo.document.Document;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.editor.folding.CodeFoldingSettings;
import consulo.language.editor.folding.FoldingBuilder;
import consulo.language.editor.folding.FoldingDescriptor;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes.LONGCOMMENT;
import static com.sylvanaar.idea.lua.lang.parser.LuaElementTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 10, 2010
 * Time: 2:54:53 PM
 */
@ExtensionImpl
public class LuaFoldingBuilder implements FoldingBuilder, DumbAware {
    @Nonnull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@Nonnull ASTNode node, @Nonnull Document document) {
        final List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        final ASTNode fnode = node;
        final Document fdoc = document;

        appendDescriptors(fnode, fdoc, descriptors);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }


    private void appendDescriptors(final ASTNode node, final Document document, final List<FoldingDescriptor> descriptors) {
        if (node == null) {
            return;
        }

        ProgressManager.checkCanceled();

        try {
            if (isFoldableNode(node)) {
                TextRange textRange = node.getTextRange();

                // If Multi-line
                if ((textRange.getEndOffset() <= document.getTextLength()) &&
                        (document.getLineNumber(textRange.getStartOffset()) !=
                                document.getLineNumber(textRange.getEndOffset()))) {

                    final PsiElement psiElement = node.getPsi();

                    if (psiElement instanceof LuaFunctionDefinition) {
                        LuaFunctionDefinition stmt = (LuaFunctionDefinition) psiElement;

                        final TextRange rangeEnclosingBlock = stmt.getRangeEnclosingBlock();
                        if (rangeEnclosingBlock.getLength() > 3) {
                            descriptors.add(new FoldingDescriptor(node, rangeEnclosingBlock));
                        }
                    }

                    if (psiElement instanceof LuaTableConstructor) {
                        LuaTableConstructor stmt = (LuaTableConstructor) psiElement;

                        if (stmt.getText().indexOf('\n') > 0 && stmt.getTextLength() > 3) {
                            descriptors.add(new FoldingDescriptor(node,
                                    new TextRange(stmt.getTextRange().getStartOffset() + 1,
                                            node.getTextRange().getEndOffset() - 1)));
                        }
                    }

                    if (psiElement instanceof LuaDocComment) {
                        descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
                    }
                }
            }

            if (node.getElementType() == LONGCOMMENT && node.getTextLength() > 2) {
                descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            }

            ASTNode child = node.getFirstChildNode();
            while (child != null) {
                appendDescriptors(child, document, descriptors);
                child = child.getTreeNext();
            }
        }
        catch (Exception ignored) {
        }
    }

    private boolean isFoldableNode(ASTNode node) {
        final IElementType elementType = node.getElementType();
        return elementType == FUNCTION_DEFINITION ||
                elementType == LOCAL_FUNCTION ||
                elementType == ANONYMOUS_FUNCTION_EXPRESSION ||
                elementType == TABLE_CONSTUCTOR ||
                elementType == LUADOC_COMMENT;
    }

    @Override
    public String getPlaceholderText(@Nonnull ASTNode node) {
        if (node.getElementType() == LONGCOMMENT) {
            return "comment";
        }

        if (node.getElementType() == LUADOC_COMMENT) {
            ASTNode data = node.findChildByType(LDOC_COMMENT_DATA);

            if (data != null) {
                return data.getText();
            }

            return " doc comment";
        }

        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@Nonnull ASTNode node) {
        if (node.getElementType() == FUNCTION_DEFINITION ||
                node.getElementType() == LOCAL_FUNCTION ||
                node.getElementType() == ANONYMOUS_FUNCTION_EXPRESSION) {
            return CodeFoldingSettings.getInstance().COLLAPSE_METHODS;
        }

        if (node.getElementType() == LUADOC_COMMENT) {
            return CodeFoldingSettings.getInstance().COLLAPSE_DOC_COMMENTS;
        }

        return false;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
