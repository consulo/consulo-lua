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

import com.sylvanaar.idea.lua.lang.luadoc.parser.parsing.LuaDocParsing;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersion;
import jakarta.annotation.Nonnull;

/**
* @author ilyas
*/
public class LuaDocParser implements PsiParser
{
  @Nonnull
  public ASTNode parse(IElementType root, PsiBuilder builder, LanguageVersion languageVersion) {
    PsiBuilder.Marker rootMarker = builder.mark();
    new LuaDocParsing().parse(builder);
    rootMarker.done(root);
    return builder.getTreeBuilt();
  }
}
