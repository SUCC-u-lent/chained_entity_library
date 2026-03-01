package com.ubunifu.multibossbar.common.barhandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Manages all ServerMultiBossBars created via commands.
 * This singleton keeps track of all active boss bars on the server.
 */
public class MultiBossBarManager {
	private static MultiBossBarManager INSTANCE;
	private final Map<String, ServerMultiBossBar> bossBars = new HashMap<>();
	private final MinecraftServer server;

	private MultiBossBarManager(MinecraftServer server) {
		this.server = server;
	}

	/**
	 * Gets or creates the singleton instance.
	 */
	public static MultiBossBarManager getInstance(MinecraftServer server) {
		if (INSTANCE == null) {
			INSTANCE = new MultiBossBarManager(server);
		}
		return INSTANCE;
	}

	/**
	 * Creates a new MultiBossBar with the given name.
	 * @param name Unique identifier for the boss bar
	 * @param displayName The display name shown to players
	 * @return true if created successfully, false if name already exists
	 */
	public boolean createBossBar(String name, Text displayName) {
		if (bossBars.containsKey(name)) {
			return false;
		}
		ServerMultiBossBar bar = new ServerMultiBossBar();
		bar.setText(displayName);
		bar.setVisible(true);
		bossBars.put(name, bar);
		return true;
	}

	/**
	 * Removes a boss bar by name.
	 * @param name The name of the boss bar to remove
	 * @return true if removed successfully, false if not found
	 */
	public boolean removeBossBar(String name) {
		ServerMultiBossBar bar = bossBars.remove(name);
		if (bar != null) {
			bar.clear();
			return true;
		}
		return false;
	}

	/**
	 * Gets a boss bar by name.
	 * @param name The name of the boss bar
	 * @return The ServerMultiBossBar, or null if not found
	 */
	public ServerMultiBossBar getBossBar(String name) {
		return bossBars.get(name);
	}

	/**
	 * Gets all boss bar names.
	 */
	public Set<String> getBossBarNames() {
		return new HashSet<>(bossBars.keySet());
	}

	/**
	 * Shows the boss bar to a player.
	 * @param name The name of the boss bar
	 * @param player The player to show it to
	 * @return true if successful, false if boss bar not found
	 */
	public boolean showToPlayer(String name, ServerPlayerEntity player) {
		ServerMultiBossBar bar = getBossBar(name);
		if (bar == null) return false;
		bar.addPlayer(player);
		return true;
	}

	/**
	 * Hides the boss bar from a player.
	 * @param name The name of the boss bar
	 * @param player The player to hide it from
	 * @return true if successful, false if boss bar not found
	 */
	public boolean hideFromPlayer(String name, ServerPlayerEntity player) {
		ServerMultiBossBar bar = getBossBar(name);
		if (bar == null) return false;
		bar.removePlayer(player);
		return true;
	}

	/**
	 * Shows the boss bar to all online players.
	 * @param name The name of the boss bar
	 * @return true if successful, false if boss bar not found
	 */
	public boolean showToAll(String name) {
		ServerMultiBossBar bar = getBossBar(name);
		if (bar == null) return false;
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			bar.addPlayer(player);
		}
		return true;
	}

	/**
	 * Hides the boss bar from all players.
	 * @param name The name of the boss bar
	 * @return true if successful, false if boss bar not found
	 */
	public boolean hideFromAll(String name) {
		ServerMultiBossBar bar = getBossBar(name);
		if (bar == null) return false;
		for (ServerPlayerEntity player : bar.getViewers()) {
			bar.removePlayer(player);
		}
		return true;
	}

	/**
	 * Gets the number of currently managed boss bars.
	 */
	public int getCount() {
		return bossBars.size();
	}
}

