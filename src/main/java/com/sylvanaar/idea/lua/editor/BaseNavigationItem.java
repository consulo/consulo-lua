package com.sylvanaar.idea.lua.editor;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.impl.psi.FakePsiElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.project.Project;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;

public class BaseNavigationItem extends FakePsiElement {
    private final PsiElement myPsiElement;
    private final LocalizeValue myText;
    private final Image myIcon;

    /**
     * Creates a new display item.
     *
     * @param psiElement The PsiElement to navigate to.
     * @param text       Text to show for this element.
     * @param icon       Icon to show for this element.
     */
    public BaseNavigationItem(@Nonnull PsiElement psiElement, @Nonnull LocalizeValue text, @Nullable Image icon) {
        myPsiElement = psiElement;
        myText = text;
        myIcon = icon;
    }

    @Nonnull
    @Override
    public PsiElement getNavigationElement() {
        return myPsiElement;
    }

    @Override
    public Image getIcon() {
        return myIcon;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public LocalizeValue getPresentableText() {
                return myText;
            }

            @Override
            @RequiredReadAction
            public @Nullable String getLocationString() {
                return '(' + myPsiElement.getContainingFile().getName() + ')';
            }

            @Override
            public @Nullable Image getIcon() {
                return myIcon;
            }
        };
    }

    @Override
    public PsiElement getParent() {
        return myPsiElement.getParent();
    }

    @Nonnull
    @Override
    public Project getProject() {
        return myPsiElement.getProject();
    }

    @Override
    public PsiFile getContainingFile() {
        return myPsiElement.getContainingFile();
    }

    @Override
    @RequiredReadAction
    public boolean isValid() {
        return myPsiElement.isValid();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseNavigationItem that = (BaseNavigationItem) o;

        return myPsiElement.equals(that.myPsiElement)
            && myText.equals(that.myText);
    }

    @Override
    public int hashCode() {
        return 31 * myPsiElement.hashCode() + myText.hashCode();
    }
}
