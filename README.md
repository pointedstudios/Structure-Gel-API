# Structure Gel API

## About

This library mod seeks to make handling world gen easier on developers. It's main features include:

- Gel blocks that can be used to automatically fill a structure and replace with air using structure processors
- Jigsaw registry shortcuts/builders
- Data structure block handling within jigsaw structures
- Configurable structure classes
  - Placement separation values, mob spawns, data marker handling, etc
- Dimension registry events
- Extensible vanilla-like portal behavior
- An extended biome dictionary with support for many popular biome mods
- Registrars for holding registry data for dimensions and everything structure related
- Registry utilities for structures, structure processors, points of interest, and configured features

## Compatibility

Mods are able to add compatibility to some aspects Structure Gel without needing to incorporate it into their workspace. Currently, support exists for the following:

- Biome dictionary registry

Check `com.legacy.structure_gel.StructureGelCompat` class in the source code to learn more on how to use this as it's documented there.

## Installing in Your Workspace

The Structure Gel API can be downloaded from the Modding Legacy maven repository via the buildscript. To do so, follow these steps:

1. In the `build.gradle` file for your mod, add the Modding Legacy maven repository to the `repositories` block (it is not in the `buildscript` block)

```groovy
repositories {
    // ...

    maven {
        name = "Modding Legacy Maven"
        url = "https://maven.moddinglegacy.com/artifactory/modding-legacy/"
    }
}
```

2. In the `build.gradle` file for your mod, add Structure Gel API as a dependency.

```groovy
dependencies {
    // ...

    compile fg.deobf("com.legacy:structure-gel:1.16.4-1.7.3")
}
```

To view a list of all the available versions, go to the [structure_gel folder in the maven repository browser](https://maven.moddinglegacy.com/artifactory/modding-legacy/com/legacy/structure-gel/).

3. In the `build.gradle` file for your mod, you must add two things to your run configurations. This only shows the client, but all are required. 
   1. Add the Structure Gel mixin file to your run configurations.
   2. Add a property to your run configurations that disables mixin's reference maps. This is to prevent mixin from injecting code into unmapped methods instead of the methods that have been remapped with your mappings of choice.

```groovy
minecraft {
    runs {
        client {
            workingDirectory project.file('run')
           
            arg '-mixin.config=structure_gel.mixins.json' // <-- Part 3a

            property 'mixin.env.disableRefMap', 'true' // <-- Part 3b
            // ...
        }
    }
}
```

4. Add Structure Gel as a dependency for your mod in its `mods.toml` file.

```toml
[[dependencies.examplemod]]
    modId="structure_gel"
    mandatory=true
    versionRange="[1.7.3,]"
    ordering="NONE"
    side="BOTH"
```

5. Setup your workspace with the appropriate gradlew commands for your IDE.
    - For Eclipse
        - ``gradlew eclispe``
        - ``gradlew genEclipseRuns``
    - For IntelliJ
        - ``gradlew genIntellijRuns``

6. From here, assuming you see Structure Gel in your project's external dependencies, you should be able to use the API. If you do not see it, try refreshing your project as some IDEs won't do that automatically.
