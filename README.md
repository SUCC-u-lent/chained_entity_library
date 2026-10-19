# Multiboss Bar Mod

A quilt mod that allows compacting multiple boss bars into one!

Want a boss with multiple parts you have to destroy first? This mod has you covered... for showing the health at least.

## Usage

MultiBossBar is intended to be used as a dependency by other mods. It provides a Java API for creating and managing boss bars composed of multiple individual bars.

### Adding the dependency

Add the Modrinth Maven repository and MultiBossBar dependency to your `build.gradle`:

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

Replace `<version>` with the version you want to use.

### Creating a MultiBossBar

Create a `ServerMultiBossBar`, configure its display name, and add individual bars using `Minibar.of(...)`.

```java
import com.ubunifu.multibossbar.common.barhandler.Minibar;
import com.ubunifu.multibossbar.common.barhandler.ServerMultiBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

// Create a multi-bar boss bar.
ServerMultiBossBar multiBossBar = new ServerMultiBossBar();

// Set the display name for the multi-bar.
multiBossBar.setText(Text.literal("Dragon Encounter"));

// Add an individual bar.
multiBossBar.addBossBar(Minibar.of(
    Text.literal("Dragon Health"),
    BossBar.Color.RED,
    BossBar.Style.PROGRESS,
    false, // Darken sky
    false, // Play dragon music
    false, // Thicken fog
    1.0f,  // Progress: 0.0 = empty, 1.0 = full
    5      // Bar height
));

// Enable updates and display the bar to a player.
multiBossBar.setVisible(true);
multiBossBar.addPlayer(player);
```

Here, `player` is a `ServerPlayerEntity` representing the player who should see the bar.

### Updating a bar

Individual bars can be updated by their zero-based index. These methods send the appropriate updates to players currently viewing the `ServerMultiBossBar`.

```java
// Update the first bar's progress to 50%.
multiBossBar.setPercent(0, 0.5f);

// Change its color.
multiBossBar.setColor(0, BossBar.Color.GREEN);

// Change its display name.
multiBossBar.setDisplayName(0, Text.literal("Remaining Health"));

// Change its style.
multiBossBar.setStyle(0, BossBar.Style.PROGRESS);
```

The progress value is clamped to the range `0.0f`–`1.0f`. The index refers to the order in which bars were added, starting at `0`.

You can also add an existing vanilla `BossBar`:

```java
multiBossBar.addBossBar(bossBar);
```

This converts the supplied boss bar into a `BossBarSection` using its current properties. Subsequent changes to the original `BossBar` are not automatically reflected; use the `ServerMultiBossBar` setters to update the added bar.

### Managing visibility

A `ServerMultiBossBar` can be shown to individual players or hidden from them at any time.

```java
// Show the bar to a player.
multiBossBar.addPlayer(player);

// Hide the bar from that player.
multiBossBar.removePlayer(player);
```

To manage multiple viewers, call `addPlayer` or `removePlayer` for each `ServerPlayerEntity` as appropriate.

The `setVisible(boolean)` method controls whether the multi-bar sends update packets. It does not itself add or remove viewers. Use `addPlayer` and `removePlayer` to control who sees the bar.

### Removing bars

Remove an individual bar by its zero-based index:

```java
multiBossBar.removeBossBar(0);
```

To remove all individual bars:

```java
multiBossBar.clear();
```

### Commands

MultiBossBar includes commands intended for development and testing. These commands are only included in development environments and are **not available as in-game commands in normal releases**.

## License

This mod is licensed under the [GNU Lesser General Public License](./LICENSE-TEMPLATE.md).

The License terms dictate:
### You may:
- Distribute
- Use
- Copy
- Use another license on projects/libraries/mods that use this mod as a dependency

### You must not:
- Modify
- Distribute without crediting the original author.
- Use any other license than the GNU Lesser General Public Licence on distributions.
