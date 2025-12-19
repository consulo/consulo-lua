package consulo.lua.action;

import com.sylvanaar.idea.lua.LuaBundle;
import com.sylvanaar.idea.lua.LuaIcons;
import consulo.ide.action.CreateFileFromTemplateAction;
import consulo.ide.action.CreateFileFromTemplateDialog;
import consulo.language.psi.PsiDirectory;
import consulo.localize.LocalizeValue;
import consulo.project.Project;

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
		builder.addKind(LocalizeValue.localizeTODO("File"), LuaIcons.LUA_ICON, "Lua Script.lua");
	}

	@Override
	protected LocalizeValue getActionName(PsiDirectory directory, String newName, String templateName)
	{
		return LocalizeValue.localizeTODO(LuaBundle.message("newfile.menu.action.text"));
	}
}
