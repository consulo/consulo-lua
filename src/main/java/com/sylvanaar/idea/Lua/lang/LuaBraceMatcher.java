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

package com.sylvanaar.idea.lua.lang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.BracePair;
import consulo.language.Language;
import consulo.language.PairedBraceMatcher;
import consulo.language.ast.IElementType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.sylvanaar.idea.lua.lang.lexer.LuaTokenTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 13.07.2009
 * Time: 20:37:13
 */
@ExtensionImpl
public class LuaBraceMatcher implements PairedBraceMatcher {
    public static final BracePair[] BRACES =
            {
                    new BracePair(LONGSTRING_BEGIN, LONGSTRING_END, true),
                    new BracePair(LONGCOMMENT_BEGIN, LONGCOMMENT_END, true),
                    new BracePair(LPAREN, RPAREN, false),
                    new BracePair(LBRACK, RBRACK, false),
                    new BracePair(LCURLY, RCURLY, false),
                    new BracePair(REPEAT, UNTIL, true),
                    new BracePair(DO, END, true),
                    new BracePair(IF, END, true),
                    new BracePair(FUNCTION, END, true),
            };

    public BracePair[] getPairs() {
        return BRACES;
    }

    public boolean isPairedBracesAllowedBeforeType(@Nonnull IElementType lbraceType, @Nullable IElementType contextType) {
        if (lbraceType == LBRACK || lbraceType == LONGCOMMENT_BEGIN || lbraceType == LONGSTRING_BEGIN) {
            return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
