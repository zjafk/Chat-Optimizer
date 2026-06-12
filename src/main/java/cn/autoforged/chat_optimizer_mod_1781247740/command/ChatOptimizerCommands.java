package cn.autoforged.chat_optimizer_mod_1781247740.command;

import cn.autoforged.chat_optimizer_mod_1781247740.ChatOptimizerMod;
import cn.autoforged.chat_optimizer_mod_1781247740.config.ModClientConfig;
import cn.autoforged.chat_optimizer_mod_1781247740.gui.ChatSearchScreen;
import cn.autoforged.chat_optimizer_mod_1781247740.history.ChatHistoryManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.util.List;

@EventBusSubscriber(modid = ChatOptimizerMod.MODID, value = Dist.CLIENT)
public class ChatOptimizerCommands {

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("chatoptimizer")
                        .then(Commands.literal("help")
                                .executes(ChatOptimizerCommands::executeHelp))
                        .then(Commands.literal("toggle")
                                .executes(ChatOptimizerCommands::executeToggle))
                        .then(Commands.literal("timestamp")
                                .then(Commands.literal("on")
                                        .executes(ctx -> setTimestamp(true)))
                                .then(Commands.literal("off")
                                        .executes(ctx -> setTimestamp(false)))
                                .executes(ChatOptimizerCommands::executeTimestampStatus))
                        .then(Commands.literal("history")
                                .then(Commands.literal("clear")
                                        .executes(ChatOptimizerCommands::executeHistoryClear))
                                .then(Commands.literal("count")
                                        .executes(ChatOptimizerCommands::executeHistoryCount))
                                .executes(ChatOptimizerCommands::executeHistoryStatus))
                        .then(Commands.literal("search")
                                .then(Commands.argument("keyword", StringArgumentType.greedyString())
                                        .executes(ChatOptimizerCommands::executeSearch))
                                .executes(ChatOptimizerCommands::executeSearchOpen))
                        .then(Commands.literal("autocommand")
                                .then(Commands.literal("list")
                                        .executes(ChatOptimizerCommands::executeAutoCommandList))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                                .executes(ChatOptimizerCommands::executeAutoCommandAdd)))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                                .executes(ChatOptimizerCommands::executeAutoCommandRemove)))
                                .executes(ChatOptimizerCommands::executeAutoCommandStatus))
                        .then(Commands.literal("status")
                                .executes(ChatOptimizerCommands::executeStatus))
                        .executes(ChatOptimizerCommands::executeHelp)
        );
    }

    private static int executeHelp(CommandContext<CommandSourceStack> ctx) {
        sendMessage(Component.literal("§6=== Chat Optimizer Commands ==="));
        sendMessage(Component.literal("§e/chatoptimizer help§r - Show this help"));
        sendMessage(Component.literal("§e/chatoptimizer toggle§r - Toggle mod on/off"));
        sendMessage(Component.literal("§e/chatoptimizer status§r - Show current status"));
        sendMessage(Component.literal("§e/chatoptimizer timestamp <on|off>§r - Toggle timestamps"));
        sendMessage(Component.literal("§e/chatoptimizer history§r - History info"));
        sendMessage(Component.literal("§e/chatoptimizer history clear§r - Clear history"));
        sendMessage(Component.literal("§e/chatoptimizer search [keyword]§r - Search chat history"));
        sendMessage(Component.literal("§e/chatoptimizer autocommand§r - Manage auto commands"));
        return 1;
    }

    private static int executeToggle(CommandContext<CommandSourceStack> ctx) {
        boolean current = ModClientConfig.CONFIG.masterEnabled.get();
        ModClientConfig.CONFIG.masterEnabled.set(!current);
        sendMessage(Component.literal("Chat Optimizer " + (!current ? "§aenabled" : "§cdisabled")));
        return 1;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> ctx) {
        sendMessage(Component.literal("§6=== Chat Optimizer Status ==="));
        sendMessage(Component.literal("§7Master: " + (ModClientConfig.CONFIG.masterEnabled.get() ? "§aON" : "§cOFF")));
        sendMessage(Component.literal("§7Timestamps: " + (ModClientConfig.CONFIG.timestampsEnabled.get() ? "§aON" : "§cOFF")));
        sendMessage(Component.literal("§7Timestamp mode: §f" + ModClientConfig.CONFIG.timestampMode.get()));
        sendMessage(Component.literal("§7History: " + (ModClientConfig.CONFIG.historyEnabled.get() ? "§aON" : "§cOFF")));
        sendMessage(Component.literal("§7History count: §f" + ChatHistoryManager.getMessageCount()));
        sendMessage(Component.literal("§7Auto commands: " + (ModClientConfig.CONFIG.autoCommandsEnabled.get() ? "§aON" : "§cOFF")));
        sendMessage(Component.literal("§7Search: " + (ModClientConfig.CONFIG.searchEnabled.get() ? "§aON" : "§cOFF")));
        return 1;
    }

    private static int setTimestamp(boolean enabled) {
        ModClientConfig.CONFIG.timestampsEnabled.set(enabled);
        sendMessage(Component.literal("Timestamps " + (enabled ? "§aenabled" : "§cdisabled")));
        return 1;
    }

    private static int executeTimestampStatus(CommandContext<CommandSourceStack> ctx) {
        sendMessage(Component.literal("Timestamps: " + (ModClientConfig.CONFIG.timestampsEnabled.get() ? "§aON" : "§cOFF")
                + " §7(mode: " + ModClientConfig.CONFIG.timestampMode.get() + ")"));
        return 1;
    }

    private static int executeHistoryClear(CommandContext<CommandSourceStack> ctx) {
        ChatHistoryManager.clearHistory();
        sendMessage(Component.literal("§aChat history cleared."));
        return 1;
    }

    private static int executeHistoryCount(CommandContext<CommandSourceStack> ctx) {
        sendMessage(Component.literal("§7Messages in memory: §f" + ChatHistoryManager.getMessageCount()));
        return 1;
    }

    private static int executeHistoryStatus(CommandContext<CommandSourceStack> ctx) {
        sendMessage(Component.literal("§7History enabled: " + (ModClientConfig.CONFIG.historyEnabled.get() ? "§aYes" : "§cNo")));
        sendMessage(Component.literal("§7Auto-save: " + (ModClientConfig.CONFIG.historyAutoSave.get() ? "§aYes" : "§cNo")));
        sendMessage(Component.literal("§7Clear strategy: §f" + ModClientConfig.CONFIG.historyClearStrategy.get()));
        sendMessage(Component.literal("§7Server: §f" + ChatHistoryManager.getCurrentServerKey()));
        sendMessage(Component.literal("§7Messages in memory: §f" + ChatHistoryManager.getMessageCount()));
        return 1;
    }

    private static int executeSearch(CommandContext<CommandSourceStack> ctx) {
        String keyword = StringArgumentType.getString(ctx, "keyword");
        Minecraft.getInstance().setScreen(new ChatSearchScreen(keyword));
        return 1;
    }

    private static int executeSearchOpen(CommandContext<CommandSourceStack> ctx) {
        Minecraft.getInstance().setScreen(new ChatSearchScreen(""));
        return 1;
    }

    private static int executeAutoCommandList(CommandContext<CommandSourceStack> ctx) {
        List<? extends String> commands = ModClientConfig.CONFIG.autoCommandList.get();
        sendMessage(Component.literal("§6Auto Commands (" + commands.size() + "):"));
        if (commands.isEmpty()) {
            sendMessage(Component.literal("§7  (none)"));
        } else {
            for (String cmd : commands) {
                sendMessage(Component.literal("§7  - " + cmd));
            }
        }
        return 1;
    }

    private static int executeAutoCommandAdd(CommandContext<CommandSourceStack> ctx) {
        String command = StringArgumentType.getString(ctx, "command");
        if (!command.startsWith("/")) {
            sendMessage(Component.literal("§cCommand must start with /"));
            return 0;
        }
        List<String> commands = new java.util.ArrayList<>(ModClientConfig.CONFIG.autoCommandList.get());
        if (commands.contains(command)) {
            sendMessage(Component.literal("§cCommand already exists: " + command));
            return 0;
        }
        commands.add(command);
        ModClientConfig.CONFIG.autoCommandList.set(commands);
        sendMessage(Component.literal("§aAdded: " + command));
        return 1;
    }

    private static int executeAutoCommandRemove(CommandContext<CommandSourceStack> ctx) {
        String command = StringArgumentType.getString(ctx, "command");
        List<String> commands = new java.util.ArrayList<>(ModClientConfig.CONFIG.autoCommandList.get());
        if (!commands.remove(command)) {
            sendMessage(Component.literal("§cNot found: " + command));
            return 0;
        }
        ModClientConfig.CONFIG.autoCommandList.set(commands);
        sendMessage(Component.literal("§aRemoved: " + command));
        return 1;
    }

    private static int executeAutoCommandStatus(CommandContext<CommandSourceStack> ctx) {
        sendMessage(Component.literal("§7Auto commands: " + (ModClientConfig.CONFIG.autoCommandsEnabled.get() ? "§aON" : "§cOFF")));
        sendMessage(Component.literal("§7Global config: " + (ModClientConfig.CONFIG.autoCommandsGlobal.get() ? "§aYes" : "§cNo")));
        sendMessage(Component.literal("§7Delay: §f" + ModClientConfig.CONFIG.autoCommandDelayTicks.get() + " ticks"));
        sendMessage(Component.literal("§7Encryption: " + (ModClientConfig.CONFIG.autoCommandsEncryption.get() ? "§aON" : "§cOFF")));
        return 1;
    }

    private static void sendMessage(Component message) {
        Minecraft.getInstance().player.sendSystemMessage(message);
    }
}
