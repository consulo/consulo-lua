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

package com.sylvanaar.idea.lua.lang.luadoc.parser.elements;

import com.sylvanaar.idea.lua.lang.luadoc.lexer.ILuaDocElementType;
import com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocLexer;
import com.sylvanaar.idea.lua.lang.luadoc.psi.api.LuaDocTag;
import com.sylvanaar.idea.lua.lang.parser.util.ParserUtils;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.LeafPsiElement;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiBuilderFactory;
import consulo.language.psi.PsiElement;
import consulo.language.version.LanguageVersionUtil;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocTokenTypes.LDOC_TAG_PLAIN_VALUE_TOKEN;
import static com.sylvanaar.idea.lua.lang.luadoc.lexer.LuaDocTokenTypes.LDOC_TAG_VALUE;

/**
 * @author ilyas
 */
public class LuaDocTagValueTokenType extends LuaDocChameleonElementType implements ILuaDocElementType {

    private static final Set<String> TAGS_WITH_REFERENCES = new HashSet<String>();
    private static final Set<String> BUILT_IN_TYPES       = new HashSet<String>();

    static {
        BUILT_IN_TYPES
                .addAll(Arrays.asList("table", "number", "boolean", "string", "nil", "userdata", "function",
                        "thread"));
    }

    static {
        TAGS_WITH_REFERENCES.addAll(Arrays.asList("@see", "@field", "@name"));
    }

    public LuaDocTagValueTokenType() {
        super("LDOC_TAG_VALUE_TOKEN");
    }

    public TagValueTokenType getValueType(@Nonnull ASTNode node) {
        return isReferenceElement(node.getTreeParent(),
                node) ? TagValueTokenType.REFERENCE_ELEMENT : TagValueTokenType.VALUE_TOKEN;
    }

    public ASTNode parseContents(ASTNode chameleon) {
        ASTNode parent = chameleon.getTreeParent();
        if (isReferenceElement(parent, chameleon)) {
            return parseImpl(chameleon);
        }

        return getPlainValueToken(chameleon);
    }

    private static boolean isReferenceElement(ASTNode parent, ASTNode child) {
        if (parent != null && child != null) {
            PsiElement parentPsi = parent.getPsi();
            if (parentPsi instanceof LuaDocTag) {
                String name = ((LuaDocTag) parentPsi).getName();
                if (TAGS_WITH_REFERENCES.contains(name)) {
                    return parent.findChildByType(LDOC_TAG_VALUE) == child;
                }
            }

        }
        return false;
    }

    private static ASTNode getPlainValueToken(ASTNode chameleon) {
        return new LeafPsiElement(LDOC_TAG_PLAIN_VALUE_TOKEN, chameleon.getChars());
    }

    private ASTNode parseImpl(ASTNode chameleon) {
        final PsiElement parentElement = chameleon.getTreeParent().getPsi();

        assert parentElement != null;
        final Project project = parentElement.getProject();
        final PsiBuilder builder = PsiBuilderFactory.getInstance()
                                                    .createBuilder(project, chameleon, new LuaDocLexer(),
                                                            getLanguage(),
															LanguageVersionUtil.findDefaultVersion(getLanguage()),
                                                            chameleon.getText());

        PsiBuilder.Marker rootMarker = builder.mark();
        if (BUILT_IN_TYPES.contains(chameleon.getText())) {
            ParserUtils.advance(builder, 1);
        } else {
            parseBody(builder);
        }
        rootMarker.done(this);
        return builder.getTreeBuilt().getFirstChildNode();
    }

    private static void parseBody(PsiBuilder builder) {
        //ReferenceElement.parse(builder, false, false, false, false);
        while (!builder.eof()) {
            builder.advanceLexer();
        }
    }

    public static enum TagValueTokenType {
        REFERENCE_ELEMENT, VALUE_TOKEN
    }
}
