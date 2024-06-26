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

package com.sylvanaar.idea.lua.lang.formatter.blocks;

import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.codeStyle.*;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiErrorElement;
import consulo.language.psi.PsiWhiteSpace;
import consulo.logging.Logger;
import consulo.language.psi.PsiComment;
import consulo.language.ast.ILazyParseableElementType;
import com.sylvanaar.idea.lua.lang.formatter.processors.LuaSpacingProcessorBasic;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import com.sylvanaar.idea.lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaBinaryExpression;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaIdentifierList;
import com.sylvanaar.idea.lua.lang.psi.lists.LuaParameterList;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaTableConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;


public class LuaFormattingBlock implements Block
{
    public static final Logger LOG = Logger.getInstance("Lua.LuaBlock");
  final protected ASTNode myNode;
  final protected Alignment myAlignment;
  final protected Indent myIndent;
  final protected Wrap myWrap;
  final protected CodeStyleSettings mySettings;

  protected List<Block> mySubBlocks = null;

  public LuaFormattingBlock(@Nonnull final ASTNode node, @Nullable final Alignment alignment, @Nonnull final Indent indent, @Nullable final Wrap wrap, final CodeStyleSettings settings) {
    myNode = node;
    myAlignment = alignment;
    myIndent = indent;
    myWrap = wrap;
    mySettings = settings;
  }

  @Nonnull
  public ASTNode getNode() {
   // LOG.info("Block <"+myNode.getText()+"> " + myNode.getElementType());
    return myNode;
  }

//  @NotNull
//  public CodeStyleSettings getSettings() {
//    return mySettings;
//  }

  @Nonnull
  public TextRange getTextRange() {
    return myNode.getTextRange();
  }

  @Nonnull
  public List<Block> getSubBlocks() {
    if (mySubBlocks == null) {
      mySubBlocks = LuaBlockGenerator.generateSubBlocks(myNode, myAlignment, myWrap, mySettings, this);
    }
    return mySubBlocks;
  }

  @Nullable
  public Wrap getWrap() {
    return myWrap;
  }

  @Nullable
  public Indent getIndent() {
    return myIndent;
  }

  @Nullable
  public Alignment getAlignment() {
    return myAlignment;
  }

  /**
   * Returns spacing between neighrbour elements
   *
   * @param child1 left element
   * @param child2 right element
   * @return
   */
  @Nullable
  public Spacing getSpacing(Block child1, Block child2) {
    if ((child1 instanceof LuaFormattingBlock) && (child2 instanceof LuaFormattingBlock)) {
         return LuaSpacingProcessorBasic.getSpacing(((LuaFormattingBlock) child1), ((LuaFormattingBlock) child2), mySettings);
    }
    return null;
  }


  public boolean isIncomplete() {
    return isIncomplete(myNode);
  }

  /**
   * @param node Tree node
   * @return true if node is incomplete
   */
  public boolean isIncomplete(@Nonnull final ASTNode node) {
    if (node.getElementType() instanceof ILazyParseableElementType) return false;
    ASTNode lastChild = node.getLastChildNode();
    while (lastChild != null &&
        !(lastChild.getElementType() instanceof ILazyParseableElementType) &&
        (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
      lastChild = lastChild.getTreePrev();
    }
    return lastChild != null && (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
  }

    public boolean isLeaf() {
        return getNode().getFirstChildNode() == null;
    }

  @Nonnull
  public ChildAttributes getChildAttributes(final int newChildIndex) {
    return getAttributesByParent();
  }

  private ChildAttributes getAttributesByParent() {
    ASTNode astNode = getNode();
    final PsiElement psiParent = astNode.getPsi();
    if (psiParent instanceof LuaPsiFile) {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
      if (psiParent instanceof LuaTableConstructor) {
        return new ChildAttributes(Indent.getNormalIndent(), null);
      }
    if (LuaElementTypes.BLOCK_SET.contains(astNode.getElementType())) {
      final Alignment align = Alignment.createAlignment();
      return new ChildAttributes(Indent.getNormalIndent(), align);
    }

    if (psiParent instanceof LuaBinaryExpression ) {
      return new ChildAttributes(Indent.getContinuationWithoutFirstIndent(), null);
    }
    if (psiParent instanceof LuaParameterList) {
      return new ChildAttributes(this.getIndent(), this.getAlignment());
    }
    if (psiParent instanceof LuaIdentifierList) {
      return new ChildAttributes(Indent.getContinuationIndent(), null);
    }
    return new ChildAttributes(Indent.getNoneIndent(), this.getAlignment());
  }
}
