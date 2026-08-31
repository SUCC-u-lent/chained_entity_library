package com.ubunifu.multibossbar.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ubunifu.multibossbar.common.barhandler.MultiBossBar;
import com.ubunifu.multibossbar.common.barhandler.MultiBossBarManager;
import com.ubunifu.multibossbar.common.barhandler.ServerMultiBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MultibossBarCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("multibossbar")
				.requires(source -> source.hasPermission(2))
				.then(
					CommandManager.literal("create")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.then(CommandManager.argument("displayName", StringArgumentType.greedyString())
								.executes(context -> createBossBar(
									context.getSource(),
									StringArgumentType.getString(context, "name"),
									StringArgumentType.getString(context, "displayName")
								))
							)
						)
				)
				.then(
					CommandManager.literal("remove")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.executes(context -> removeBossBar(
								context.getSource(),
								StringArgumentType.getString(context, "name")
							))
						)
				)
				.then(
					CommandManager.literal("list")
						.executes(context -> listBossBars(context.getSource()))
				)
				.then(
					CommandManager.literal("show")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.executes(context -> showToAll(
								context.getSource(),
								StringArgumentType.getString(context, "name")
							))
						)
				)
				.then(
					CommandManager.literal("hide")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.executes(context -> hideFromAll(
								context.getSource(),
								StringArgumentType.getString(context, "name")
							))
						)
				)
				.then(
					CommandManager.literal("addbar")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.then(CommandManager.argument("barName", StringArgumentType.greedyString())
								.executes(context -> addBar(
									context.getSource(),
									StringArgumentType.getString(context, "name"),
									StringArgumentType.getString(context, "barName")
								))
							)
						)
				)
				.then(
					CommandManager.literal("removebar")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.then(CommandManager.argument("index", FloatArgumentType.floatArg())
								.executes(context -> removeBar(
									context.getSource(),
									StringArgumentType.getString(context, "name"),
									(int) FloatArgumentType.getFloat(context, "index")
								))
							)
						)
				)
				.then(
					CommandManager.literal("setpercent")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.then(CommandManager.argument("barIndex", FloatArgumentType.floatArg())
								.then(CommandManager.argument("percent", FloatArgumentType.floatArg(0, 100))
									.executes(context -> setPercent(
										context.getSource(),
										StringArgumentType.getString(context, "name"),
										(int) FloatArgumentType.getFloat(context, "barIndex"),
										FloatArgumentType.getFloat(context, "percent") / 100f
									))
								)
							)
						)
				)
				.then(
					CommandManager.literal("setcolor")
						.then(CommandManager.argument("name", StringArgumentType.word())
							.then(CommandManager.argument("barIndex", FloatArgumentType.floatArg())
								.then(CommandManager.argument("color", StringArgumentType.word())
									.executes(context -> setColor(
										context.getSource(),
										StringArgumentType.getString(context, "name"),
										(int) FloatArgumentType.getFloat(context, "barIndex"),
										StringArgumentType.getString(context, "color")
									))
								)
							)
						)
				)
				.then(
					CommandManager.literal("help")
						.executes(context -> help(context.getSource()))
				)
		);
	}

	private static int createBossBar(ServerCommandSource source, String name, String displayName) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		if (manager.createBossBar(name, Text.literal(displayName))) {
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.created", name), false);
			return 1;
		} else {
			source.sendError(Text.translatable("multibossbar.command.error.exists", name));
			return 0;
		}
	}

	private static int removeBossBar(ServerCommandSource source, String name) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		if (manager.removeBossBar(name)) {
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.removed", name), false);
			return 1;
		} else {
			source.sendError(Text.translatable("multibossbar.command.error.notfound", name));
			return 0;
		}
	}

	private static int listBossBars(ServerCommandSource source) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.bar_count", manager.getCount()), false);
		if (manager.getCount() == 0) {
			source.sendFeedback(() -> Text.translatable("multibossbar.command.error.no_bars"), false);
			return 1;
		}
		for (String name : manager.getBossBarNames()) {
			ServerMultiBossBar bar = manager.getBossBar(name);
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.bar_info" , name ,bar.getMinibars().size()), false);
		}
		return 1;
	}

	private static int showToAll(ServerCommandSource source, String name) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		if (manager.showToAll(name)) {
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.show", name), false);
			return 1;
		} else {
			source.sendError(Text.translatable("multibossbar.command.error.notfound", name));
			return 0;
		}
	}

	private static int hideFromAll(ServerCommandSource source, String name) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		if (manager.hideFromAll(name)) {
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.hiding", name), false);
			return 1;
		} else {
			source.sendError(Text.translatable("multibossbar.command.error.notfound",name));
			return 0;
		}
	}

	private static int addBar(ServerCommandSource source, String name, String barName) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		ServerMultiBossBar bar = manager.getBossBar(name);
		if (bar == null) {
			source.sendError(Text.translatable("multibossbar.command.error.notfound",name));
			return 0;
		}
		ServerBossBar serverBossBar = new ServerBossBar(Text.literal(barName), BossBar.Color.WHITE, BossBar.Style.PROGRESS);
		bar.addBossBar(serverBossBar);
		source.sendFeedback(() -> Text.translatable("§aAdded bar to: §f" + name + " §a(Index: " + (bar.getMinibars().size() - 1) + ")"), false);
		return 1;
	}

	private static int removeBar(ServerCommandSource source, String name, int index) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		ServerMultiBossBar bar = manager.getBossBar(name);
		if (bar == null) {
			source.sendError(Text.translatable("multibossbar.command.error.notfound",name));
			return 0;
		}
		try {
			bar.removeBossBar(index);
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.removed", index), false);
			return 1;
		} catch (IndexOutOfBoundsException e) {
			source.sendError(Text.translatable("multibossbar.command.error.invalidindex", index));
			return 0;
		}
	}

	private static int setPercent(ServerCommandSource source, String name, int barIndex, float percent) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		ServerMultiBossBar bar = manager.getBossBar(name);
		if (bar == null) {
			source.sendError(Text.translatable("multibossbar.command.error.notfound",name));
			return 0;
		}
		try {
			bar.setPercent(barIndex, percent);
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.percentageset",percent*100), false);
			return 1;
		} catch (IndexOutOfBoundsException e) {
			source.sendError(Text.translatable("multibossbar.command.error.invalidindex", barIndex));
			return 0;
		}
	}

	private static int setColor(ServerCommandSource source, String name, int barIndex, String colorStr) {
		MultiBossBarManager manager = MultiBossBarManager.getInstance(source.getServer());
		ServerMultiBossBar bar = manager.getBossBar(name);
		if (bar == null) {
			source.sendError(Text.translatable("multibossbar.command.feedback.setcolor",name));
			return 0;
		}
		try {
			BossBar.Color color = BossBar.Color.valueOf(colorStr.toUpperCase());
			bar.setColor(barIndex, color);
			source.sendFeedback(() -> Text.translatable("multibossbar.command.feedback.setcolor", colorStr), false);
			return 1;
		} catch (IllegalArgumentException e) {
			source.sendError(Text.translatable("multibossbar.command.error.invalidcolorlist"));
			return 0;
		} catch (IndexOutOfBoundsException e) {
			source.sendError(Text.translatable("multibossbar.command.error.invalidindex", barIndex));
			return 0;
		}
	}

	private static int help(ServerCommandSource source) {
		source.sendFeedback(() -> Text.translatable("multibossbar.command.help_command"), false);

//		source.sendFeedback(() -> Text.translatable("§6=== MultiBossBar Commands ==="), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar create <name> <displayName>§7 - Create a new boss bar"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar remove <name>§7 - Remove a boss bar"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar list§7 - List all boss bars"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar show <name>§7 - Show boss bar to all players"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar hide <name>§7 - Hide boss bar from all players"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar addbar <name> <barName>§7 - Add a bar to the boss bar"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar removebar <name> <index>§7 - Remove a bar from the boss bar"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar setpercent <name> <barIndex> <percent>§7 - Set bar health (0-100)"), false);
//		source.sendFeedback(() -> Text.translatable("§e/multibossbar setcolor <name> <barIndex> <color>§7 - Set bar color"), false);
		return 1;
	}
}

