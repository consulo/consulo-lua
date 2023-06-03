package consulo.lua.action;

import consulo.project.Project;
import consulo.language.psi.PsiDirectory;
import com.sylvanaar.idea.Lua.LuaBundle;
import com.sylvanaar.idea.Lua.LuaIcons;
import consulo.ide.action.CreateFileFromTemplateAction;
import consulo.ide.action.CreateFileFromTemplateDialog;

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
