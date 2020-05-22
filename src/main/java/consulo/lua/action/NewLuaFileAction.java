package consulo.lua.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.sylvanaar.idea.Lua.LuaBundle;
import com.sylvanaar.idea.Lua.LuaIcons;

/**
 * @author VISTALL
 * @since 2020-05-23
 */
public class NewLuaFileAction extends CreateFileFromTemplateAction
{
	public NewLuaFileAction()
	{
		super(LuaBundle.message("newfile.menu.action.text"), LuaBundle.message("newfile.menu.action.description"), LuaIcons.LUA_ICON);
	}

	@Override
	protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder)
	{
		builder.addKind("File", LuaIcons.LUA_ICON, "Lua Script.lua");
	}

	@Override
	protected String getActionName(PsiDirectory directory, String newName, String templateName)
	{
		return LuaBundle.message("newfile.menu.action.text");
	}
}
