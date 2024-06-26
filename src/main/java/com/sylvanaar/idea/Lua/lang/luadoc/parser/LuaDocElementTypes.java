/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sylvanaar.idea.lua.lang.luadoc.parser;

import javax.annotation.Nonnull;

import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.ILazyParseableElementType;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiBuilderFactory;
import consulo.language.parser.PsiParser;
import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocElementType;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocElementTypeImpl;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocLexer;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocTokenTypes;
import com.sylvanaar.idea.lua.lang.luadoc.psi.impl.LuaDocCommentImpl;
import consulo.language.version.LanguageVersionUtil;
import consulo.language.psi.PsiElement;
import consulo.project.Project;

/**
 * @author ilyas
 */
public interface LuaDocElementTypes extends LuaDocTokenTypes {

  /**
   * LuaDoc comment
   */
  ILazyParseableElementType LUADOC_COMMENT = new ILazyParseableElementType("LuaDocComment") {
    @Nonnull
    public Language getLanguage() {
      return LuaFileType.LUA_FILE_TYPE.getLanguage();
    }

    public ASTNode parseContents(ASTNode chameleon) {

      final PsiElement parentElement = chameleon.getTreeParent().getPsi();

      assert parentElement != null;
        
      final Project project = parentElement.getProject();

      final PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new LuaDocLexer(), getLanguage(), LanguageVersionUtil.findDefaultVersion(getLanguage()), chameleon.getText());
      final PsiParser parser = new LuaDocParser();

      return parser.parse(this, builder, LanguageVersionUtil.findDefaultVersion(getLanguage())).getFirstChildNode();
    }

    @Override
    public ASTNode createNode(CharSequence text) {
      return new LuaDocCommentImpl(text);
    }
  };

  LuaDocElementType LDOC_TAG = new LuaDocElementTypeImpl("LuaDocTag");

  LuaDocElementType LDOC_REFERENCE_ELEMENT = new LuaDocElementTypeImpl("LuaDocReferenceElement");
  LuaDocElementType LDOC_PARAM_REF = new LuaDocElementTypeImpl("LuaDocParameterReference");
  LuaDocElementType LDOC_FIELD_REF = new LuaDocElementTypeImpl("LuaDocFieldReference");
  }
