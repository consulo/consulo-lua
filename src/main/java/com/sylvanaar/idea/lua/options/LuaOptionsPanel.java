/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.sylvanaar.idea.lua.options;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import consulo.configurable.BaseConfigurable;
import org.jetbrains.annotations.Nls;
import consulo.configurable.Configurable;
import consulo.project.Project;
import consulo.project.ProjectManager;
import consulo.language.psi.PsiManager;
import com.sylvanaar.idea.lua.LuaFileType;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 20, 2010
 * Time: 7:08:52 PM
 */
public class LuaOptionsPanel extends BaseConfigurable implements Configurable
{
	public LuaOptionsPanel()
	{
		addAdditionalCompletionsCheckBox.addActionListener(e -> setModified(isModified(LuaApplicationSettings.getInstance())));
		enableTypeInference.addActionListener(e -> setModified(isModified(LuaApplicationSettings.getInstance())));
	}

	JPanel getMainPanel()
	{
		return mainPanel;
	}

	private JPanel mainPanel;
	private JCheckBox addAdditionalCompletionsCheckBox;
	private JCheckBox enableTypeInference;

	@Override
	public JComponent createComponent()
	{
		setData(LuaApplicationSettings.getInstance());
		return getMainPanel();
	}

	public void apply()
	{
		getData(LuaApplicationSettings.getInstance());
		setModified(false);
	}

	public void reset()
	{
		setData(LuaApplicationSettings.getInstance());
	}

	@Override
	public void disposeUIResources()
	{

	}

	@Nls
	@Override
	public String getDisplayName()
	{
		return LuaFileType.LUA;
	}

	@Override
	public String getHelpTopic()
	{
		return null;
	}

	public void setData(LuaApplicationSettings data)
	{
		addAdditionalCompletionsCheckBox.setSelected(data.INCLUDE_ALL_FIELDS_IN_COMPLETIONS);
		enableTypeInference.setSelected(data.ENABLE_TYPE_INFERENCE);
	}

	public void getData(LuaApplicationSettings data)
	{

		data.INCLUDE_ALL_FIELDS_IN_COMPLETIONS = addAdditionalCompletionsCheckBox.isSelected();
		data.ENABLE_TYPE_INFERENCE = enableTypeInference.isSelected();
		if(data.ENABLE_TYPE_INFERENCE)
		{
			for(Project project : ProjectManager.getInstance().getOpenProjects())
			{
				PsiManager.getInstance(project).dropResolveCaches();
			}
		}
	}

	public boolean isModified(LuaApplicationSettings data)
	{
		if(addAdditionalCompletionsCheckBox.isSelected() != data.INCLUDE_ALL_FIELDS_IN_COMPLETIONS)
		{
			return true;
		}
		if(enableTypeInference.isSelected() != data.ENABLE_TYPE_INFERENCE)
		{
			return true;
		}

		return false;
	}
}
