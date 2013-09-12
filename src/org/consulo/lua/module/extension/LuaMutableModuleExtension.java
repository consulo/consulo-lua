package org.consulo.lua.module.extension;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.consulo.module.extension.MutableModuleExtensionWithSdk;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.consulo.module.extension.ui.ModuleExtensionWithSdkPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author VISTALL
 * @since 12.07.13.
 */
public class LuaMutableModuleExtension extends LuaModuleExtension implements MutableModuleExtensionWithSdk<LuaModuleExtension> {
	private LuaModuleExtension myModuleExtension;

	public LuaMutableModuleExtension(@NotNull String id, @NotNull Module module, @NotNull LuaModuleExtension moduleExtension) {
		super(id, module);
		myModuleExtension = moduleExtension;
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk() {
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new ModuleExtensionWithSdkPanel(this, runnable), BorderLayout.NORTH);
		return panel;
	}

	@Override
	public void setEnabled(boolean b) {
		myIsEnabled = b;
	}

	@Override
	public boolean isModified() {
		return isModifiedImpl(myModuleExtension);
	}

	@Override
	public void commit() {
		myModuleExtension.commit(this);
	}
}
