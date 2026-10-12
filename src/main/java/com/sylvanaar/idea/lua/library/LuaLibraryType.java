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

package com.sylvanaar.idea.lua.library;

import com.sylvanaar.idea.lua.LuaBundle;
import com.sylvanaar.idea.lua.LuaFileType;
import com.sylvanaar.idea.lua.LuaIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.library.DummyLibraryProperties;
import consulo.content.library.LibraryType;
import consulo.content.library.NewLibraryConfiguration;
import consulo.content.library.PersistentLibraryKind;
import consulo.content.library.ui.LibraryEditor;
import consulo.content.library.ui.LibraryEditorComponent;
import consulo.content.library.ui.LibraryPropertiesEditor;
import consulo.fileChooser.FileChooser;
import consulo.fileChooser.FileChooserDescriptor;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.lua.localize.LuaLocalize;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/21/11
 * Time: 8:54 PM
 */
@ExtensionImpl
public class LuaLibraryType extends LibraryType<DummyLibraryProperties> implements LuaLibrary {
    private static final PersistentLibraryKind<DummyLibraryProperties> LIBRARY_KIND =
            new PersistentLibraryKind<DummyLibraryProperties>(LUA_LIBRARY_KIND_ID) {
                @Nonnull
                @Override
                public DummyLibraryProperties createDefaultProperties() {
                    return new DummyLibraryProperties();
                }
            };

    public LuaLibraryType() {
        super(LIBRARY_KIND);
    }


    @Nonnull
    @Override
    public String getCreateActionName() {
        return "Lua";
    }

    @RequiredUIAccess
    @Override
    public CompletableFuture<NewLibraryConfiguration> createNewLibrary(@Nullable Component parentComponent,
                                                                       @Nullable VirtualFile contextDirectory,
                                                                       @Nonnull Project project) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createAllButJarContentsDescriptor();
        descriptor.withTitle(LuaLocalize.newLibraryFileChooserTitle());
        descriptor.withDescription(LuaLocalize.newLibraryFileChooserDescription());
        return FileChooser.chooseFiles(descriptor, TargetAWT.to(parentComponent), project, contextDirectory).thenApply(files -> {
            if (files.length == 0) {
                throw new CancellationException();
            }
            return new NewLibraryConfiguration("Lua Library", this, new DummyLibraryProperties()) {
                @Override
                public void addRoots(@Nonnull LibraryEditor editor) {
                    for (VirtualFile file : files) {
                        editor.addRoot(file, BinariesOrderRootType.ID);
                    }
                }
            };
        });
    }
//
//    @NotNull
//    @Override
//    public LuaLibraryProperties createDefaultProperties() {
//        return new LuaLibraryProperties();
//    }

    @Override
    public Image getIcon() {
        return LuaIcons.LUA_ICON;
    }

//    @Override
//    public LibraryRootsComponentDescriptor createLibraryRootsComponentDescriptor() {
//        return new LuaLibraryRootsComponentDescriptor();
//    }

    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@Nonnull LibraryEditorComponent<DummyLibraryProperties>
                                                                  libraryPropertiesLibraryEditorComponent) {

        return null;
    }

    @Override
    public DummyLibraryProperties detect(@Nonnull List<VirtualFile> classesRoots) {
        for (VirtualFile vf : classesRoots) {
            if (!vf.isDirectory()) {
                return null;
            }

            for (VirtualFile file : vf.getChildren()) {
                String fileExtension = file.getExtension();
                if (fileExtension != null) {
                    if (fileExtension.equals(LuaFileType.DEFAULT_EXTENSION)) {
                        return new DummyLibraryProperties();
                    }
                }
            }
        }

        return null;
    }

}
