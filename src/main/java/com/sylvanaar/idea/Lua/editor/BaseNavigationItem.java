package com.sylvanaar.idea.Lua.editor;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.FakePsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BaseNavigationItem extends FakePsiElement {

	private final PsiElement myPsiElement;
	private final String myText;
	private final Icon myIcon;

	/**
	 * Creates a new display item.
	 *
	 * @param psiElement The PsiElement to navigate to.
	 * @param text       Text to show for this element.
	 * @param icon       Icon to show for this element.
	 */
	public BaseNavigationItem(@NotNull PsiElement psiElement, @NotNull @NonNls String text, @Nullable Icon icon) {
		myPsiElement = psiElement;
		myText = text;
		myIcon = icon;
	}

	@NotNull
	public PsiElement getNavigationElement() {
		return myPsiElement;
	}

	public Icon getIcon(boolean flags) {
		return myIcon;
	}

	public ItemPresentation getPresentation() {
		return new ItemPresentation() {

			public String getPresentableText() {
				return myText;
			}

			@Nullable
			public String getLocationString() {
				return '(' + myPsiElement.getContainingFile().getName() + ')';
			}

			@Nullable
			public Icon getIcon(boolean open) {
				return myIcon;
			}
		};
	}

	public PsiElement getParent() {
		return myPsiElement.getParent();
	}

	@NotNull
	@Override
	public Project getProject() {
		return myPsiElement.getProject();
	}

	@Override
	public PsiFile getContainingFile() {
		return myPsiElement.getContainingFile();
	}

	@Override
	public boolean isValid() {
		return myPsiElement.isValid();
	}

	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final BaseNavigationItem that = (BaseNavigationItem) o;

		if (!myPsiElement.equals(that.myPsiElement)) return false;
		if (!myText.equals(that.myText)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = myPsiElement.hashCode();
		result = 31 * result + myText.hashCode();
		return result;
	}
}
