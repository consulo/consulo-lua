/*
 * Copyright 2000-2008 JetBrains s.r.o.
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

package com.sylvanaar.idea.Lua.lang.luadoc.lexer;

import consulo.language.ast.IElementType;
import consulo.language.lexer.LexerBase;

%%

%extends LexerBase
%class _LuaDocLexer
%implements LuaDocTokenTypes


%unicode
%public

%function advanceImpl
%type IElementType


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// LuaDoc lexems ////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%state COMMENT_DATA_START
%state COMMENT_DATA
%state TAG_DOC_SPACE
%state PRE_TAG_DATA_SPACE
%state DOC_TAG_VALUE

WHITE_DOC_SPACE_CHAR=[\ \t\f\n\r]
WHITE_DOC_SPACE_NO_NL=[\ \t\f]
NON_WHITE_DOC_SPACE_CHAR=[^\ \t\f\n\r]
DIGIT=[0-9]
ALPHA=[:jletter:]
TAGNAME={ALPHA}({ALPHA}|{DIGIT})*

%%

<YYINITIAL> --- { yybegin(COMMENT_DATA_START); return LDOC_COMMENT_START; }
<COMMENT_DATA_START> {WHITE_DOC_SPACE_NO_NL}+ { return LDOC_WHITESPACE; }
<COMMENT_DATA_START> (--+) { return LDOC_DASHES; }
<COMMENT_DATA>  {WHITE_DOC_SPACE_NO_NL}+ { return LDOC_COMMENT_DATA; }
<COMMENT_DATA, COMMENT_DATA_START, TAG_DOC_SPACE>  \r?\n { yybegin(COMMENT_DATA_START); return LDOC_WHITESPACE; }

<TAG_DOC_SPACE> {WHITE_DOC_SPACE_NO_NL}+ { yybegin(COMMENT_DATA); return LDOC_WHITESPACE; }
<DOC_TAG_VALUE> {NON_WHITE_DOC_SPACE_CHAR}+ { yybegin(TAG_DOC_SPACE); return LDOC_TAG_VALUE; }

<COMMENT_DATA_START> "@param" { yybegin(PRE_TAG_DATA_SPACE); return LDOC_TAG_NAME; }
<COMMENT_DATA_START> "@class" { yybegin(PRE_TAG_DATA_SPACE); return LDOC_TAG_NAME; }
<COMMENT_DATA_START> "@name"  { yybegin(PRE_TAG_DATA_SPACE); return LDOC_TAG_NAME; }
<COMMENT_DATA_START> "@field" { yybegin(PRE_TAG_DATA_SPACE); return LDOC_TAG_NAME; }
<COMMENT_DATA_START> "@see"   { yybegin(PRE_TAG_DATA_SPACE); return LDOC_TAG_NAME; }

<PRE_TAG_DATA_SPACE>  {WHITE_DOC_SPACE_CHAR}+ {yybegin(DOC_TAG_VALUE); return LDOC_WHITESPACE;}

<COMMENT_DATA_START> "@"{TAGNAME} { yybegin(TAG_DOC_SPACE); return LDOC_TAG_NAME;  }

<COMMENT_DATA_START, COMMENT_DATA, DOC_TAG_VALUE> . { yybegin(COMMENT_DATA); return LDOC_COMMENT_DATA; }

[^] { return LDOC_COMMENT_BAD_CHARACTER; }