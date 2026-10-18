package com.ubunifu.multibossbar.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.ubunifu.multibossbar.common.boss_bar.FillType;
import com.ubunifu.multibossbar.common.boss_bar.SectionType;
import com.ubunifu.multibossbar.common.boss_bar.container.ServerMultiBossBar;
import com.ubunifu.multibossbar.common.boss_bar.sections.ServerBossBarSection;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.registry.HolderLookup;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.*;

public class MultibossBarCommand {
	private static final Map<String, ServerMultiBossBar> BARS = new HashMap<>();

	public static void register(
		CommandDispatcher<ServerCommandSource> dispatcher,
		HolderLookup.Provider registries
	) {
		if (!QuiltLoader.isDevelopmentEnvironment()) return; // Hide commands out of dev environments as its a library not a usable mod.
		var root = CommandManager.literal("multibossbar")
			.requires(source -> source.hasPermission(2));

		root.then(CommandManager.literal("create")
			.then(CommandManager.argument("name", StringArgumentType.word())
				.then(CommandManager.argument("displayName", StringArgumentType.string())
					.executes(context -> createBossBar(
						context.getSource(),
						StringArgumentType.getString(context, "name"),
						StringArgumentType.getString(context, "displayName")
					))
				)
			)
		);

		root.then(CommandManager.literal("remove")
			.then(existingBarNameArgument()
				.executes(context -> removeBossBar(
					context.getSource(),
					StringArgumentType.getString(context, "name")
				))
			)
		);

		root.then(CommandManager.literal("list")
			.executes(context -> listBossBars(context.getSource()))
		);

		root.then(CommandManager.literal("show")
			.then(existingBarNameArgument()
				.executes(context -> showToAll(
					context.getSource(),
					StringArgumentType.getString(context, "name")
				))
			)
		);

		root.then(CommandManager.literal("hide")
			.then(existingBarNameArgument()
				.executes(context -> hideFromAll(
					context.getSource(),
					StringArgumentType.getString(context, "name")
				))
			)
		);

		root.then(CommandManager.literal("addbar")
			.then(existingBarNameArgument()
				.then(CommandManager.argument("barName", StringArgumentType.string())
					.executes(context->addBar(
						context.getSource(),
						StringArgumentType.getString(context, "name"),
						StringArgumentType.getString(context, "barName"),
						5
					))
					.then(CommandManager.argument("barHeight", IntegerArgumentType.integer(1,10))
						.executes(context -> addBar(
							context.getSource(),
							StringArgumentType.getString(context, "name"),
							StringArgumentType.getString(context, "barName"),
							IntegerArgumentType.getInteger(context, "barHeight")
						))
					)
				)
			)
		);

		root.then(CommandManager.literal("removebar")
			.then(existingBarNameArgument()
				.then(CommandManager.argument("index", IntegerArgumentType.integer(0))
					.executes(context -> removeBar(
						context.getSource(),
						StringArgumentType.getString(context, "name"),
						IntegerArgumentType.getInteger(context, "index")
					))
				)
			)
		);

		root.then(CommandManager.literal("players")
			.then(existingBarNameArgument()
				.then(CommandManager.argument("selector", EntityArgumentType.players())
					.executes((ctx)->setPlayers(
						ctx.getSource(),
						StringArgumentType.getString( ctx, "name"),
						EntityArgumentType.getPlayers( ctx, "selector" )
					))
				)
			)
		);

		root.then(CommandManager.literal("setpercent")
			.then(existingBarNameArgument()
				.then(CommandManager.argument("barIndex", IntegerArgumentType.integer(0))
					.then(CommandManager.argument("percent", FloatArgumentType.floatArg(0, 100))
						.executes(context -> setPercent(
							context.getSource(),
							StringArgumentType.getString(context, "name"),
							IntegerArgumentType.getInteger(context, "barIndex"),
							FloatArgumentType.getFloat(context, "percent") / 100.0F
						))
					)
				)
			)
		);

		root.then(CommandManager.literal("setcolor")
			.then(existingBarNameArgument()
				.then(CommandManager.argument("barIndex", IntegerArgumentType.integer(0))
					.then(CommandManager.argument("color", StringArgumentType.string())
						.executes(context -> setColor(
							context.getSource(),
							StringArgumentType.getString(context, "name"),
							IntegerArgumentType.getInteger(context, "barIndex"),
							StringArgumentType.getString(context, "color")
						))
					)
				)
			)
		);

		root.then(CommandManager.literal("help")
			.executes(context -> help(context.getSource()))
		);

		dispatcher.register(root);
	}
	private static RequiredArgumentBuilder<ServerCommandSource, String> existingBarNameArgument() {
		return CommandManager.argument("name", StringArgumentType.string())
			.suggests((context, builder) -> {
				BARS.keySet().forEach(builder::suggest);
				return builder.buildFuture();
			});
	}
	private static Text parseTextJson(ServerCommandSource source, String json) {
		try {
			Text text = Text.SerializationUtil.fromJson(json, source.getRegistryManager());
			if (text == null) {
				source.sendError(Text.literal("Display name JSON must not be null."));
			}
			return text;
		} catch (Exception e) {
			source.sendError(Text.literal("Invalid display name JSON: " + e.getMessage()));
			return null;
		}
	}
	private static int createBossBar(ServerCommandSource source, String name, String displayName) {
		if (BARS.containsKey(name)) {
			source.sendError(Text.literal("A boss bar named '" + name + "' already exists."));
			return 0;
		}

		ServerMultiBossBar bar = new ServerMultiBossBar();
		bar.setFillType(FillType.VANILLA);
		Text parsedDisplayName = parseTextJson(source, displayName);
		if (parsedDisplayName == null) {
			return 0;
		}
		bar.addPlayer( source.getPlayer() );
		bar.setVisible( true );

		BARS.put(name, bar);
		MultibossBarS2CPacket.createBar(bar).send(source.getPlayer());

		source.sendFeedback(() -> Text.literal("Created boss bar '" + name + "'."), false);
		return 1;
	}

	private static int removeBossBar(ServerCommandSource source, String name) {
		ServerMultiBossBar removed = BARS.remove(name);

		if (removed == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		ServerMultiBossBar.removeBar(removed);

		source.sendFeedback(() -> Text.literal("Removed boss bar '" + name + "'."), false);
		return 1;
	}

	private static int listBossBars(ServerCommandSource source) {
		if (BARS.isEmpty()) {
			source.sendFeedback(() -> Text.literal("No boss bars are currently registered."), false);
			return 1;
		}

		for (Map.Entry<String, ServerMultiBossBar> entry : BARS.entrySet()) {
			entry.getValue().resync();
			source.sendFeedback(
				() -> Text.literal(entry.getKey() + " -> " + entry.getValue().count() + " sections"),
				false
			);
		}

		return 1;
	}

	private static int showToAll(ServerCommandSource source, String name) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		bar.setVisible(true);
		bar.resync();
		source.sendFeedback(() -> Text.literal("Boss bar '" + name + "' is now visible."), false);
		return 1;
	}

	private static int hideFromAll(ServerCommandSource source, String name) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		bar.setVisible(false);
		bar.resync();
		source.sendFeedback(() -> Text.literal("Boss bar '" + name + "' is now hidden."), false);
		return 1;
	}

	private static int addBar(ServerCommandSource source, String name, String barName, int barHeight) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		Text parsedDisplayName = parseTextJson(source, barName);
		if (parsedDisplayName == null) {
			return 0;
		}
		BossBarSection section = new ServerBossBarSection(
			bar.count(),
			parsedDisplayName,
			BossBar.Color.WHITE,
			BossBar.Style.PROGRESS,
			SectionType.MULTI,
			barHeight
		);

		section.setContainer(bar);
		bar.addBossBar(section);

		bar.resync();
		source.sendFeedback(
			() -> Text.literal("Added bar to '" + name + "' at index " + (bar.count() - 1) + "."),
			false
		);

		return 1;
	}

	private static int removeBar(ServerCommandSource source, String name, int index) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		if (index < 0 || index >= bar.count()) {
			source.sendError(Text.literal("Invalid bar index: " + index));
			return 0;
		}

		bar.removeBossBar(index);
		bar.resync();
		source.sendFeedback(() -> Text.literal("Removed section " + index + " from '" + name + "'."), false);
		return 1;
	}

	private static int setPercent(ServerCommandSource source, String name, int barIndex, float percent) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		if (barIndex < 0 || barIndex >= bar.count()) {
			source.sendError(Text.literal("Invalid bar index: " + barIndex));
			return 0;
		}

		bar.setPercent(barIndex, percent);
		bar.resync(barIndex);

		source.sendFeedback(
			() -> Text.literal("Updated section " + barIndex + " of '" + name + "' to " + percent + "."),
			false
		);

		return 1;
	}

	private static int setPlayers(ServerCommandSource source, String name, Collection<ServerPlayerEntity> players) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		try {
			bar.setPlayers(Set.copyOf(players));

			source.sendFeedback(
				() -> Text.literal("Showing bar to "+players.size()+" players"),
				false
			);
			bar.resync();

			return 1;
		} catch (IllegalArgumentException e) {
			source.sendError(Text.literal("Failed to show bar to: " + players.size() + " players!" ));
			return 0;
		}
	}

	private static int setColor(ServerCommandSource source, String name, int barIndex, String color) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		if (barIndex < 0 || barIndex >= bar.count()) {
			source.sendError(Text.literal("Invalid bar index: " + barIndex));
			return 0;
		}

		try {
			BossBar.Color parsed = BossBar.Color.valueOf(color.toUpperCase(Locale.ROOT));
			bar.setColor(barIndex, parsed);

			source.sendFeedback(
				() -> Text.literal("Updated section " + barIndex + " of '" + name
					+ "' to color " + parsed.name() + "."),
				false
			);

			bar.resync(barIndex);

			return 1;
		} catch (IllegalArgumentException e) {
			source.sendError(Text.literal("Invalid color: " + color));
			return 0;
		}
	}

	private static int help(ServerCommandSource source) {
		source.sendFeedback(
			() -> Text.literal(
				"Usage: /multibossbar create <name> <displayName> | remove <name> | list"
					+ " | show <name> | hide <name> | addbar <name> <barName>"
					+ " | removebar <name> <index> | setpercent <name> <barIndex> <percent>"
					+ " | setcolor <name> <barIndex> <color>"
			),
			false
		);

		return 1;
	}
}
