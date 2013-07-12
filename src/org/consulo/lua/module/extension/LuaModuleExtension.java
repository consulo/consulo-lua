package org.consulo.lua.module.extension;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.SdkType;
import com.sylvanaar.idea.Lua.sdk.LuaSdkType;
import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 12.07.13.
 */
public class LuaModuleExtension extends ModuleExtensionWithSdkImpl<LuaModuleExtension> {
	public LuaModuleExtension(@NotNull String id, @NotNull Module module) {
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass() {
		return LuaSdkType.class;
	}
}
