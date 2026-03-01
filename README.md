# Multi Boss Bar Mod

This is a library mod.

## Usage

In order to use this mod:

- Add this mod as a dependency in your `build.gradle`:
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

- Initalize `ServerMultiBossBar` in your entity's class and add bars via `.addBossBar()`

## In-game Usage
This mod is quite limited to what it can do in-game however there is a command that exists that allows a basic usage of the boss bar
