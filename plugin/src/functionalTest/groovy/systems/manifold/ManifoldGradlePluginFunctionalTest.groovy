package systems.manifold

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.TempDir

class ManifoldGradlePluginFunctionalTest extends Specification {

    @TempDir
    private File projectDir

    private getBuildFile() {
        new File(projectDir, 'build.gradle')
    }

    private getSettingsFile() {
        new File(projectDir, 'settings.gradle')
    }

    def 'plugin can compile with properties and extensions successfully'() {
        given:
        settingsFile << ''
        buildFile << '''
            |plugins {
            |  id 'systems.manifold.manifold-gradle-plugin'
            |}
            |
            |repositories {
            |  mavenCentral()
            |}
            |
            |dependencies {
            |  implementation "systems.manifold:manifold-json-rt:${manifold.manifoldVersion.get()}"
            |  implementation "systems.manifold:manifold-props-rt:${manifold.manifoldVersion.get()}"
            |  annotationProcessor "systems.manifold:manifold-json:${manifold.manifoldVersion.get()}"
            |  annotationProcessor "systems.manifold:manifold-props:${manifold.manifoldVersion.get()}"
            |}
            |
            |// logging for debugging purposes
            |tasks.withType(JavaCompile) {
            |  doFirst {
            |    logger.lifecycle 'Invoking javac with arguments: {}', options.compilerArgs
            |    logger.lifecycle 'Invoking javac with annotationProcessorPath: {}', options.annotationProcessorPath.files*.name            
            |    logger.lifecycle 'Invoking javac with compileClasspath: {}', classpath.files*.name
            |    logger.lifecycle 'Invoking javac with source: {}', source*.name
            |  }
            |}
            |'''.stripMargin()

        // main resources
        def dataResources = new File(projectDir, 'src/main/resources/data')
        dataResources.mkdirs()
        def person = new File(dataResources, 'Person.json')
        person << '''{
        |  "Name": "Scott",
        |  "Age": 39,
        |  "Address": {
        |    "Number": 9604,
        |    "Street": "Donald Court",
        |    "City": "Golden Shores",
        |    "State": "FL"
        |  },
        |  "Hobby": [
        |    {
        |      "Category": "Sport",
        |      "Name": "Baseball"
        |    },
        |    {
        |      "Category": "Recreation",
        |      "Name": "Hiking"
        |    }
        |  ]
        |}
        '''.stripMargin()

        def user = new File(dataResources, 'User.json')
        user << '''{
        |  "$schema": "http://json-schema.org/draft-07/schema#",
        |  "$id": "http://example.com/schemas/User.json",
        |  "type": "object",
        |  "definitions": {
        |    "Gender": {
        |      "type": "string",
        |      "enum": [
        |        "male",
        |        "female"
        |      ]
        |    }
        |  },
        |  "properties": {
        |    "name": {
        |      "type": "string",
        |      "description": "User's full name.",
        |      "maxLength": 80
        |    },
        |    "email": {
        |      "description": "User's email.",
        |      "type": "string",
        |      "format": "email"
        |    },
        |    "date_of_birth": {
        |      "type": "string",
        |      "description": "Date of uses birth in the one and only date standard: ISO 8601.",
        |      "format": "date"
        |    },
        |    "gender": {
        |      "$ref": "#/definitions/Gender"
        |    }
        |  },
        |  "required": [
        |    "name",
        |    "email"
        |  ]
        |}
        '''.stripMargin()

        // main sources
        def mainSources = new File(projectDir, 'src/main/java/com/example')
        mainSources.mkdirs()
        def main = new File(mainSources, 'Main.java')
        main << '''
        |package com.example;
        |
        |import data.Person;
        |import data.User;
        |
        |import java.time.LocalDate;
        |import java.util.ArrayList;
        |
        |import static data.User.Gender.male;
        |
        |public class Main {
        |  public static void main(String[] args) {
        |    // simple JSON sample file + property inference
        |    var person = Person.fromSource();
        |    System.out.println(person.name);
        |
        |    // simple JSON schema file + property inference
        |    var user = User.builder("Bob", "bob@email.com")
        |      .withGender(male)
        |      .withDate_of_birth(LocalDate.of(1978, 8, 18))
        |      .build();
        |    System.out.println(user.name);
        |    System.out.println(user.date_of_birth.year);
        |
        |    // use custom method extension
        |    ArrayList<String> list = new ArrayList<>();
        |    list.myArrayListMethod();
        |  }
        |}
        '''.stripMargin()

        // extension sources
        def extensionSources = new File(projectDir, 'src/main/java/extensions/java/util/ArrayList')
        extensionSources.mkdirs()
        def myArrayListExt = new File(extensionSources, 'MyArrayListExt.java')
        myArrayListExt << '''
        |package extensions.java.util.ArrayList;
        |
        |import manifold.ext.rt.api.Extension;
        |import manifold.ext.rt.api.This;
        |import java.util.ArrayList;
        |
        |@Extension
        |public class MyArrayListExt {
        |  public static <E> void myArrayListMethod(@This ArrayList<E> thiz) {
        |    System.out.println("hello world!");
        |  }
        |}
        '''.stripMargin()

        when:
        def runner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments('assemble')
                .forwardOutput()

        def result = runner.build()

        then:
        result.task(':compileJava').outcome == TaskOutcome.SUCCESS
    }
}
