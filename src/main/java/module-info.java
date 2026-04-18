/**
 * @author VISTALL
 * @since 2023-06-03
 */
module consulo.lua {
    requires transitive consulo.ide.api;

    requires consulo.application.api;
    requires consulo.application.content.api;
    requires consulo.code.editor.api;
    requires consulo.color.scheme.api;
    requires consulo.component.api;
    requires consulo.configurable.api;
    requires consulo.container.api;
    requires consulo.datacontext.api;
    requires consulo.disposer.api;
    requires consulo.document.api;
    requires consulo.execution.api;
    requires consulo.execution.debug.api;
    requires consulo.file.chooser.api;
    requires consulo.file.editor.api;
    requires consulo.http.api;
    requires consulo.index.io;
    requires consulo.language.api;
    requires consulo.language.impl;
    requires consulo.language.code.style.api;
    requires consulo.language.code.style.ui.api;
    requires consulo.language.copyright.api;
    requires consulo.language.editor.api;
    requires consulo.language.editor.refactoring.api;
    requires consulo.language.editor.ui.api;
    requires consulo.localize.api;
    requires consulo.logging.api;
    requires consulo.module.api;
    requires consulo.module.content.api;
    requires consulo.module.ui.api;
    requires consulo.navigation.api;
    requires consulo.base.icon.library;
    requires consulo.process.api;
    requires consulo.project.api;
    requires consulo.project.content.api;
    requires consulo.project.ui.api;
    requires consulo.project.ui.view.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.ui.ex.awt.api;
    requires consulo.undo.redo.api;
    requires consulo.usage.api;
    requires consulo.virtual.file.system.api;
    requires consulo.util.collection;
    requires consulo.util.dataholder;
    requires consulo.util.interner;
    requires consulo.util.io;
    requires consulo.util.lang;
    requires consulo.util.xml.serializer;

    requires core;
    requires interpreter;
    requires jakarta.annotation;
    requires jsyntaxpane;
}
