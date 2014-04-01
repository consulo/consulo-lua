package org.consulo.lua.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.sylvanaar.idea.Lua.sdk.LuaSdkType;

/**
 * @author VISTALL
 * @since 12.07.13.
 */
public class LuaModuleExtension extends ModuleExtensionWithSdkImpl<LuaModuleExtension>
{
	public LuaModuleExtension(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return LuaSdkType.class;
	}
}
