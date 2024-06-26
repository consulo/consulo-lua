/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

import consulo.language.ast.ASTNode;
import consulo.document.util.TextRange;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaLiteralExpression;
import com.sylvanaar.idea.lua.lang.psi.types.LuaPrimitiveType;
import com.sylvanaar.idea.lua.lang.psi.types.LuaType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/7/11
 * Time: 12:18 PM
 */
public class LuaStringLiteralExpressionImpl extends LuaLiteralExpressionImpl implements LuaLiteralExpression {
    public LuaStringLiteralExpressionImpl(ASTNode node) {
        super(node);
    }


    @Override
    public Object getValue() {
        return getStringContent();
    }

    public String getStringContent() {
        return stripQuotes(getText());
    }

    @Nullable
    public TextRange getStringContentTextRange() {
        String openQuote = getOpenQuote(getText());

        if (openQuote == null)
            return null;
        
        return  new TextRange(getTextRange().getStartOffset() + openQuote.length() - getTextOffset(),
                getTextRange().getEndOffset() - openQuote.length()  - getTextOffset());
    }

    @Nonnull
    @Override
    public LuaType getLuaType() {
        return LuaPrimitiveType.STRING;
    }


    @Nullable
    public static String getOpenQuote(String text) {
        switch (text.charAt(0)) {
            case '\'':
            case '\"':
                return text.substring(0,1);

            case '[':
                int quoteLen = text.indexOf('[', 1);
                assert quoteLen > 1;
                return text.substring(0, quoteLen+1);
        }

        return null;
    }

    public static boolean isClosed(String text, String open) {
        String close = open.replace('[', ']');

        return text.length() > open.length() && text.endsWith(close);
    }

    public static String stripQuotes(String text) {
        String openQuote = getOpenQuote(text);
        if (openQuote == null)
            return "ERROR";

        final int quoteLen = openQuote.length();

        final int length = text.length();
        final int endIndex = isClosed(text, openQuote) ? length - quoteLen : length;
        return text.substring(quoteLen, endIndex > quoteLen ? endIndex : quoteLen);
    }
}
