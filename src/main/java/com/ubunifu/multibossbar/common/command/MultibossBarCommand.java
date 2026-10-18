package com.ubunifu.multibossbar.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.ubunifu.multibossbar.common.boss_bar.FillType;
import com.ubunifu.multibossbar.common.boss_bar.SectionType;
import com.ubunifu.multibossbar.common.boss_bar.container.ServerMultiBossBar;
import com.ubunifu.multibossbar.common.boss_bar.sections.ServerBossBarSection;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class MultibossBarCommand {
	private static final Map<String, ServerMultiBossBar> BARS = new HashMap<>();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		var root = CommandManager.literal("multibossbar")
			.requires(source -> source.hasPermission(2));

		root.then(CommandManager.literal("create")
			.then(CommandManager.argument("name", StringArgumentType.word())
				.then(CommandManager.argument("displayName", StringArgumentType.greedyString())
					.executes(context -> createBossBar(
						context.getSource(),
						StringArgumentType.getString(context, "name"),
						StringArgumentType.getString(context, "displayName")
					))
				)
			)
		);

		root.then(CommandManager.literal("remove")
			.then(CommandManager.argument("name", StringArgumentType.word())
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
			.then(CommandManager.argument("name", StringArgumentType.word())
				.executes(context -> showToAll(
					context.getSource(),
					StringArgumentType.getString(context, "name")
				))
			)
		);

		root.then(CommandManager.literal("hide")
			.then(CommandManager.argument("name", StringArgumentType.word())
				.executes(context -> hideFromAll(
					context.getSource(),
					StringArgumentType.getString(context, "name")
				))
			)
		);

		root.then(CommandManager.literal("addbar")
			.then(CommandManager.argument("name", StringArgumentType.word())
				.then(CommandManager.argument("barName", StringArgumentType.greedyString())
					.executes(context -> addBar(
						context.getSource(),
						StringArgumentType.getString(context, "name"),
						StringArgumentType.getString(context, "barName")
					))
				)
			)
		);

		root.then(CommandManager.literal("removebar")
			.then(CommandManager.argument("name", StringArgumentType.word())
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
			.then(CommandManager.argument("name", StringArgumentType.word())
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
			.then(CommandManager.argument("name", StringArgumentType.word())
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
			.then(CommandManager.argument("name", StringArgumentType.word())
				.then(CommandManager.argument("barIndex", IntegerArgumentType.integer(0))
					.then(CommandManager.argument("color", StringArgumentType.word())
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

	private static int createBossBar(ServerCommandSource source, String name, String displayName) {
		if (BARS.containsKey(name)) {
			source.sendError(Text.literal("A boss bar named '" + name + "' already exists."));
			return 0;
		}

		ServerMultiBossBar bar = new ServerMultiBossBar();
		bar.setFillType(FillType.VANILLA);

		BossBarSection section = new ServerBossBarSection(
			0,
			Text.literal(displayName),
			BossBar.Color.WHITE,
			BossBar.Style.PROGRESS,
			SectionType.MULTI,
			5
		);

		section.setContainer(bar);
		bar.addBossBar(section);
		BARS.put(name, bar);

		source.sendFeedback(() -> Text.literal("Created boss bar '" + name + "'."), false);
		return 1;
	}

	private static int removeBossBar(ServerCommandSource source, String name) {
		ServerMultiBossBar removed = BARS.remove(name);

		if (removed == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		source.sendFeedback(() -> Text.literal("Removed boss bar '" + name + "'."), false);
		return 1;
	}

	private static int listBossBars(ServerCommandSource source) {
		if (BARS.isEmpty()) {
			source.sendFeedback(() -> Text.literal("No boss bars are currently registered."), false);
			return 1;
		}

		for (Map.Entry<String, ServerMultiBossBar> entry : BARS.entrySet()) {
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
		source.sendFeedback(() -> Text.literal("Boss bar '" + name + "' is now hidden."), false);
		return 1;
	}

	private static int addBar(ServerCommandSource source, String name, String barName) {
		ServerMultiBossBar bar = BARS.get(name);

		if (bar == null) {
			source.sendError(Text.literal("Boss bar '" + name + "' was not found."));
			return 0;
		}

		BossBarSection section = new ServerBossBarSection(
			bar.count(),
			Text.literal(barName),
			BossBar.Color.WHITE,
			BossBar.Style.PROGRESS,
			SectionType.MULTI,
			5
		);

		section.setContainer(bar);
		bar.addBossBar(section);

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

