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

package com.sylvanaar.idea.lua.lang.parser;


import com.sylvanaar.idea.lua.lang.LuaLanguage;
import com.sylvanaar.idea.lua.lang.lexer.LuaLexer;
import com.sylvanaar.idea.lua.lang.lexer.LuaParsingLexerMergingAdapter;
import com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.lua.lang.parser.kahlua.KahluaParser;
import com.sylvanaar.idea.lua.lang.psi.impl.LuaPsiFileImpl;
import com.sylvanaar.idea.lua.lang.psi.stubs.elements.LuaStubFileElementType;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.util.LanguageUtil;
import consulo.language.version.LanguageVersion;
import jakarta.annotation.Nonnull;

import static com.sylvanaar.idea.lua.lang.parser.LuaElementTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 04.07.2009
 * Time: 14:39:39
 */
@ExtensionImpl
public class LuaParserDefinition implements ParserDefinition
{
    public static final IStubFileElementType LUA_FILE = new LuaStubFileElementType();
    //public static final IFileElementType LUA_FILE = new IFileElementType("Lua Script", LuaFileType.LUA_LANGUAGE);

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }

    @Nonnull
    public Lexer createLexer( LanguageVersion languageVersion) {
        return new LuaParsingLexerMergingAdapter(new LuaLexer());
    }

    public PsiParser createParser(LanguageVersion languageVersion) {
        return new KahluaParser();
    }

    public IFileElementType getFileNodeType() {
        return LUA_FILE;
    }

    @Nonnull
    public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
        return WHITE_SPACES_SET;
    }

    @Nonnull
    public TokenSet getCommentTokens(LanguageVersion languageVersion) {
        return COMMENT_SET;
    }

    @Nonnull
    public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
        return STRING_LITERAL_SET;
    }

    @Nonnull
    public PsiElement createElement(ASTNode node) {
        final PsiElement element = LuaPsiCreator.createElement(node);

        return element;
    }


    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new LuaPsiFileImpl(fileViewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        if (left.getElementType() == LuaTokenTypes.SHORTCOMMENT) return SpaceRequirements.MUST_LINE_BREAK;

        if (left.getElementType() == LuaTokenTypes.NAME && KEYWORDS.contains(right.getElementType()))
            return SpaceRequirements.MUST;

        Lexer lexer = new LuaLexer();

        return LanguageUtil.canStickTokensTogetherByLexer(left, right, lexer);
    }
}
