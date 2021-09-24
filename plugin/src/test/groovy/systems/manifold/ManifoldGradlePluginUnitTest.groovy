package systems.manifold

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ManifoldGradlePluginUnitTest extends Specification {

    def 'plugin registers an extension'() {
        given:
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("systems.manifold.manifold-gradle-plugin");

        expect:
        // Verify the result
        def manifold = project.extensions.getByType(ManifoldExtension)
        manifold.manifoldVersion.get() == '2021.1.15'
    }
}
