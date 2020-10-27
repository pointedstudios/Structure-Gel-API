# Structure Gel API

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

To install this library in your workspace:
1) Get a deobfuscated jar of this mod from the "jars" folder in the repository.
	- Make sure you're in the correct version number branch.
	- MCP mapping and minimum forge versions are listed in the "jars/README" file.
	- The latest version in this folder may not yet be released to [the curse page](https://www.curseforge.com/minecraft/mc-mods/structure-gel-api), so make sure to check what players can actually download before using the latest.
2) In the build.gradle for your mod, add the jar as a dependency.
```
dependencies {
    minecraft 'net.minecraftforge:forge:1.16.3-34.1.23'
    compile files('libs/structure-gel-api-1.16.3-1.5.0-deobf.jar')
}
```
3) In the build.gradle for your mod, add the Structure Gel mixin file to your run configurations. This only shows the client, but all are required.
```
minecraft {
    runs {
        client {
            workingDirectory project.file('run')
            arg '-mixin.config=structure_gel.mixins.json' // <-- This part right here
            property 'forge.logging.markers', 'SCAN,REGISTRIES,REGISTRYDUMP'
            property 'forge.logging.console.level', 'debug'
            mods {
                examplemod {
                    source sourceSets.main
                }
            }
        }
    }
}
```
4) Add Structure Gel as a dependency for your mod in it's mods.toml.
```
[[dependencies.examplemod]]
    modId="structure_gel"
    mandatory=true
    versionRange="[1.5.0,]"
    ordering="NONE"
    side="BOTH"
```
5) Setup your workspace with the appropriate gradlew commands for your IDE.
	- For Eclipse
		- ``gradlew eclispe``
		- ``gradlew genEclipseRuns``
	- For IntelliJ
		- ``gradlew genIntellijRuns``
6) From here, assuming you see Structure Gel in your project's external dependencies, you should be able to use the API. If you do not see it, try refreshing your project as some IDEs won't do that automatically.