/**
 * @author VISTALL
 * @since 2023-06-03
 */
module consulo.lua {
    requires transitive consulo.annotation;
    requires transitive consulo.application.api;
    requires transitive consulo.execution.debug.api;
    requires transitive consulo.http.api;
    requires transitive consulo.ide.api;
    requires transitive consulo.language.api;
    requires transitive consulo.language.editor.refactoring.api;
    requires transitive consulo.process.api;
    requires transitive consulo.virtual.file.system.api;

    requires core;
    requires interpreter;
    requires jakarta.annotation;
    requires jsyntaxpane;
}