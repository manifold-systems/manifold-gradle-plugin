package systems.manifold;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public abstract class ManifoldExtension {

    private final Property<String> manifoldVersion;

    @Inject
    public ManifoldExtension(ObjectFactory objects) {
        manifoldVersion = objects.property(String.class).convention("2021.1.15");
    }

    public Property<String> getManifoldVersion() {
        return manifoldVersion;
    }
}
