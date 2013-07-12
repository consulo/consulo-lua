package org.consulo.lua;

import com.intellij.openapi.wm.ToolWindowModuleExtensionCondition;
import org.consulo.lua.module.extension.LuaModuleExtension;

/**
 * @author VISTALL
 * @since 12.07.13.
 */
public class LuaToolWindowCondition extends ToolWindowModuleExtensionCondition {
	public LuaToolWindowCondition() {
		super(LuaModuleExtension.class);
	}
}
