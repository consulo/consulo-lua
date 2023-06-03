/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.Lua.actions;

import consulo.document.FileDocumentManager;
import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.fileEditor.FileEditorManager;
import consulo.ide.IdeView;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiFileFactory;
import consulo.lua.bundle.BaseLuaSdkType;
import consulo.module.ModuleManager;
import consulo.navigation.OpenFileDescriptor;
import consulo.navigation.OpenFileDescriptorFactory;
import consulo.process.ExecutionException;
import consulo.process.local.ProcessOutput;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.language.editor.LangDataKeys;
import consulo.application.ApplicationManager;
import consulo.module.Module;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import com.sylvanaar.idea.Lua.LuaFileType;
import com.sylvanaar.idea.Lua.lang.psi.LuaPsiFile;
import com.sylvanaar.idea.Lua.sdk.StdLibrary;
import com.sylvanaar.idea.Lua.util.LuaModuleUtil;
import com.sylvanaar.idea.Lua.util.LuaSystemUtil;
import consulo.util.lang.ref.Ref;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 8/5/11
 * Time: 8:55 AM
 */
public class GenerateLuaListingAction extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(false);
        e.getPresentation().setEnabled(false);
        Project project = e.getData(LangDataKeys.PROJECT);
        if (project != null) {
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                e.getPresentation().setVisible(true);
                Sdk luaSdk = LuaModuleUtil.findLuaSdk(module);
                if (luaSdk == null) continue;

                final String homePath = luaSdk.getHomePath();
                if (homePath == null) continue;

                if (BaseLuaSdkType.getByteCodeCompilerExecutable(homePath).exists()) {
                    e.getPresentation().setEnabled(true);
                    break;
                }
            }
        }
    }

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(LangDataKeys.PROJECT);
        assert project != null;

        Module module = e.getData(LangDataKeys.MODULE);
        assert module != null;

        Sdk    sdk = LuaModuleUtil.findLuaSdk(module);
        assert sdk != null;

        final String homePath = sdk.getHomePath();
        if (homePath == null) return;

        String path = BaseLuaSdkType.getByteCodeCompilerExecutable(homePath).getParent();
        String exePath = BaseLuaSdkType.getTopLevelExecutable(homePath).getAbsolutePath();

        PsiFile currfile = e.getData(LangDataKeys.PSI_FILE);
        if (currfile == null || !(currfile instanceof LuaPsiFile)) return;

        FileDocumentManager.getInstance().saveAllDocuments();
        LuaSystemUtil.clearConsoleToolWindow(project);
        
        final VirtualFile virtualFile = currfile.getVirtualFile();
        if (virtualFile == null) return;

        final ProcessOutput processOutput;
        try {
            final VirtualFile child = StdLibrary.getListingModuleLocation().findChild("listing.lua");
            if (child == null) return;

            final String listingScript = child.getPath();
            processOutput = LuaSystemUtil.getProcessOutput(path, exePath, listingScript, virtualFile.getPath());
        } catch (final ExecutionException ex) {
            return;
        }
        if (processOutput.getExitCode() != 0) return;

        String errors = processOutput.getStderr();
        if (StringUtil.notNullize(errors).length() > 0) {
            LuaSystemUtil.printMessageToConsole(project, errors, ConsoleViewContentType.ERROR_OUTPUT);
            return;
        }
        String listing = processOutput.getStdout();

        final IdeView view = e.getData(IdeView.KEY);
        if (view == null) return;

        final PsiDirectory dir = view.getOrChooseDirectory();
        if (dir == null) return;

        final PsiFileFactory factory = PsiFileFactory.getInstance(project);
        final String listingFileName = virtualFile.getNameWithoutExtension() + "-listing.lua";

        final PsiFile existingFile = dir.findFile(listingFileName);
        if (existingFile != null) ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                existingFile.delete();
            }
        });


        final PsiFile file = factory.createFileFromText(listingFileName, LuaFileType.LUA_FILE_TYPE, listing);

		Ref<PsiElement> elementRef = consulo.util.lang.ref.Ref.create();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
				elementRef.set(dir.add(file));
            }
        });

		PsiElement created = elementRef.get();
		if (created == null) return;
        final PsiFile containingFile = created.getContainingFile();
        if (containingFile == null) return;
        final VirtualFile virtualFile1 = containingFile.getVirtualFile();
        if (virtualFile1 == null) return;
        
        OpenFileDescriptor fileDesc = OpenFileDescriptorFactory.getInstance(project).builder(virtualFile1).build();

        FileEditorManager.getInstance(project).openTextEditor(fileDesc, false);

        view.selectElement(created);
    }
}
