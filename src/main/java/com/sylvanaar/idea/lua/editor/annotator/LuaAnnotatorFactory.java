package com.sylvanaar.idea.lua.editor.annotator;

import com.sylvanaar.idea.lua.lang.LuaLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 24/05/2023
 */
@ExtensionImpl
public class LuaAnnotatorFactory implements AnnotatorFactory {
    @Nullable
    @Override
    public Annotator createAnnotator() {
        return new LuaAnnotator();
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return LuaLanguage.INSTANCE;
    }
}
