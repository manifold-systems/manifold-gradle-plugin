# manifold-gradle-plugin
A Gradle plugin for Manifold

Requires Gradle 6.4 or newer.

The `java` plugin and relevant compiler arguments are automatically applied.

A project extension `manifold` is added with a convenience property `manifoldVersion`. The default is `2021.1.15`.

Example application:
```groovy
plugins {
    id 'systems.manifold.manifold-gradle-plugin'
}

repositories {
    mavenCentral()
}

manifold {
    manifoldVersion = '2021.1.15'
}

dependencies {
    // apply desired manifold libs
    implementation "systems.manifold:manifold-props-rt:${manifold.manifoldVersion.get()}"
    ...
}

```