package com.rutasur;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
class ArquitecturaTest {
    @Test void controladoresNoUsanRepositorios() {
        var clases=new ClassFileImporter().importPackages("com.rutasur");
        ArchRuleDefinition.noClasses().that().resideInAPackage("..controller..")    .should().dependOnClassesThat().resideInAPackage("..repository..").check(clases);
    }
    @Test void serviciosNoDependenDeControladores() {
        var clases=new ClassFileImporter().importPackages("com.rutasur");
        ArchRuleDefinition.noClasses().that().resideInAPackage("..service..")    .should().dependOnClassesThat().resideInAPackage("..controller..").check(clases);
    }
}
