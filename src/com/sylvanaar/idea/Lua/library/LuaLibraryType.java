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

package com.sylvanaar.idea.Lua.library;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryType;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.libraries.PersistentLibraryKind;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.vfs.VirtualFile;
import com.sylvanaar.idea.Lua.LuaBundle;
import com.sylvanaar.idea.Lua.LuaFileType;
import com.sylvanaar.idea.Lua.LuaIcons;
import consulo.awt.TargetAWT;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/21/11
 * Time: 8:54 PM
 */
public class LuaLibraryType extends LibraryType<DummyLibraryProperties> implements LuaLibrary {
    private static final PersistentLibraryKind<DummyLibraryProperties> LIBRARY_KIND =
            new PersistentLibraryKind<DummyLibraryProperties>(LUA_LIBRARY_KIND_ID) {
                @NotNull
                @Override
                public DummyLibraryProperties createDefaultProperties() {
                    return new DummyLibraryProperties();
                }
            };

    protected LuaLibraryType() {
        super(LIBRARY_KIND);
    }


    @NotNull
    @Override
    public String getCreateActionName() {
        return "Lua";
    }

    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent jComponent, @Nullable VirtualFile virtualFile,
                                                    @NotNull Project project) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createAllButJarContentsDescriptor();
        descriptor.setTitle(LuaBundle.message("new.library.file.chooser.title"));
        descriptor.setDescription(LuaBundle.message("new.library.file.chooser.description"));
        final VirtualFile[] files = FileChooser.chooseFiles(descriptor, jComponent, project, virtualFile);
        if (files.length == 0) {
            return null;
        }
        return new NewLibraryConfiguration("Lua Library", this, new DummyLibraryProperties()) {
            @Override
            public void addRoots(@NotNull LibraryEditor editor) {
                for (VirtualFile file : files) {
                    editor.addRoot(file, OrderRootType.CLASSES);
                }
            }
        };
    }
//
//    @NotNull
//    @Override
//    public LuaLibraryProperties createDefaultProperties() {
//        return new LuaLibraryProperties();
//    }

    @Override
    public Icon getIcon() {
        return TargetAWT.to(LuaIcons.LUA_ICON);
    }

//    @Override
//    public LibraryRootsComponentDescriptor createLibraryRootsComponentDescriptor() {
//        return new LuaLibraryRootsComponentDescriptor();
//    }

    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<DummyLibraryProperties>
                                                                  libraryPropertiesLibraryEditorComponent) {

        return null;
    }

    @Override
    public DummyLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        for (VirtualFile vf : classesRoots) {
            if (!vf.isDirectory())
                return null;

            for(VirtualFile file : vf.getChildren()) {
                String fileExtension = file.getExtension();
                if (fileExtension != null)
                    if (fileExtension.equals(LuaFileType.DEFAULT_EXTENSION))
                        return new DummyLibraryProperties();
            }
        }

        return null;
    }

}
