package systems.manifold;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.util.GradleVersion;

/**
 * Configures {@link JavaCompile} tasks to support the manifold compiler plugin.
 */
public class ManifoldGradlePlugin implements Plugin<Project> {

    public static final String MANIFOLD_EXTENSION = "manifold";

    public void apply(Project project) {
        project.getPluginManager().apply("java");
        if (!isAtLeastGradle64()) {
            throw new GradleException("The manifold plugin requires Gradle 6.4+ to correctly infer the module-path");
        }

        project.getExtensions().create(MANIFOLD_EXTENSION, ManifoldExtension.class);

        project.getTasks().withType(JavaCompile.class, javaCompile -> {
            javaCompile.getOptions().getCompilerArgs().add("-Xplugin:Manifold");
        });
    }

    private boolean isAtLeastGradle64() {
        return GradleVersion.current().compareTo(GradleVersion.version("6.4")) >= 0;
    }
}
