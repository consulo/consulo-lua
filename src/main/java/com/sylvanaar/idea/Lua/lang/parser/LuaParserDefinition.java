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

package com.sylvanaar.idea.Lua.lang.parser;


import static com.sylvanaar.idea.Lua.lang.parser.LuaElementTypes.COMMENT_SET;
import static com.sylvanaar.idea.Lua.lang.parser.LuaElementTypes.KEYWORDS;
import static com.sylvanaar.idea.Lua.lang.parser.LuaElementTypes.STRING_LITERAL_SET;
import static com.sylvanaar.idea.Lua.lang.parser.LuaElementTypes.WHITE_SPACES_SET;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageUtil;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.sylvanaar.idea.Lua.lang.lexer.LuaLexer;
import com.sylvanaar.idea.Lua.lang.lexer.LuaParsingLexerMergingAdapter;
import com.sylvanaar.idea.Lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.Lua.lang.parser.kahlua.KahluaParser;
import com.sylvanaar.idea.Lua.lang.psi.impl.LuaPsiFileImpl;
import com.sylvanaar.idea.Lua.lang.psi.stubs.elements.LuaStubFileElementType;
import consulo.lang.LanguageVersion;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 04.07.2009
 * Time: 14:39:39
 */
public class LuaParserDefinition implements ParserDefinition {
    public static final IStubFileElementType LUA_FILE = new LuaStubFileElementType();
    //public static final IFileElementType LUA_FILE = new IFileElementType("Lua Script", LuaFileType.LUA_LANGUAGE);

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
