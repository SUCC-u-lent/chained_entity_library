# Multi Boss Bar Mod

This is a library mod.

## Usage

In order to use this mod as a template:

1. Add this mod as a dependency in your `build.gradle`:

```gradle
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}
dependencies {
    modImplementation "maven.modrinth:multi-boss-bar:<version>"
}
```

2. Initalize `ServerMultiBossBar` in your entity's class and add bars via `.addBossBar()`
