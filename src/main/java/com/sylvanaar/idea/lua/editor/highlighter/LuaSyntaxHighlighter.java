/*
 * Copyright 2009 Max Ishchenko
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

package com.sylvanaar.idea.lua.editor.highlighter;

import com.sylvanaar.idea.lua.lang.lexer.LuaLexer;
import com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes;
import com.sylvanaar.idea.lua.lang.luadoc.parser.LuaDocElementTypes;
import consulo.codeEditor.HighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;
import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

import static com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 06.07.2009
 * Time: 16:40:05
 */
public class LuaSyntaxHighlighter extends SyntaxHighlighterBase {

    private final TextAttributesKey[] BAD_CHARACTER_KEYS = new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};

    private final Map<IElementType, TextAttributesKey> colors = new HashMap<IElementType, TextAttributesKey>();

    public LuaSyntaxHighlighter() {
        colors.put(LuaTokenTypes.LONGCOMMENT, LuaHighlightingData.LONGCOMMENT);
        colors.put(LuaTokenTypes.LONGCOMMENT_BEGIN, LuaHighlightingData.LONGCOMMENT_BRACES);
        colors.put(LuaTokenTypes.LONGCOMMENT_END, LuaHighlightingData.LONGCOMMENT_BRACES);
        colors.put(LuaTokenTypes.SHORTCOMMENT, LuaHighlightingData.COMMENT);
        colors.put(LuaTokenTypes.SHEBANG, LuaHighlightingData.COMMENT);

        colors.put(LuaTokenTypes.STRING, LuaHighlightingData.STRING);
        colors.put(LuaTokenTypes.LONGSTRING, LuaHighlightingData.LONGSTRING);
        colors.put(LuaTokenTypes.LONGSTRING_BEGIN, LuaHighlightingData.LONGSTRING_BRACES);
        colors.put(LuaTokenTypes.LONGSTRING_END, LuaHighlightingData.LONGSTRING_BRACES);

        fillMap(colors, LuaTokenTypes.OPERATORS_SET, LuaHighlightingData.OPERATORS);
        fillMap(colors, KEYWORDS, LuaHighlightingData.KEYWORD);
        fillMap(colors, PARENS, LuaHighlightingData.PARENTHESES);
        fillMap(colors, BRACES, LuaHighlightingData.BRACES);
        fillMap(colors, BRACKS, LuaHighlightingData.BRACKETS);

        colors.put(SEMI, LuaHighlightingData.SEMI);

        fillMap(colors, BAD_INPUT, LuaHighlightingData.BAD_CHARACTER);
        fillMap(colors, DEFINED_CONSTANTS, LuaHighlightingData.DEFINED_CONSTANTS);
        colors.put(LuaTokenTypes.COMMA, LuaHighlightingData.COMMA);
        colors.put(LuaTokenTypes.NUMBER, LuaHighlightingData.NUMBER);

        colors.put(LuaDocElementTypes.LUADOC_COMMENT, LuaHighlightingData.LUADOC);
    }

    @Nonnull
    public Lexer getHighlightingLexer() {
        return new LuaLexer();
    }

    @Nonnull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(colors.get(tokenType));
  }




}
