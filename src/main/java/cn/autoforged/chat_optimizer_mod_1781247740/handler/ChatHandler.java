package cn.autoforged.chat_optimizer_mod_1781247740.handler;

import cn.autoforged.chat_optimizer_mod_1781247740.ChatOptimizerMod;
import cn.autoforged.chat_optimizer_mod_1781247740.config.ModClientConfig;
import cn.autoforged.chat_optimizer_mod_1781247740.history.ChatHistoryManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.UUID;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = ChatOptimizerMod.MODID, value = Dist.CLIENT)
public class ChatHandler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        if (!ModClientConfig.CONFIG.masterEnabled.get()) return;
        if (event.isCanceled()) return;

        Component original = event.getMessage();
        String senderName = "";
        boolean isSystem = event.isSystem();

        if (!isSystem && event instanceof ClientChatReceivedEvent.Player playerEvent) {
            UUID senderId = playerEvent.getPlayerChatMessage().sender();
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                PlayerInfo info = connection.getPlayerInfo(senderId);
                if (info != null) {
                    senderName = info.getProfile().getName();
                }
            }
        }

        // Record to history
        if (ModClientConfig.CONFIG.historyEnabled.get()) {
            ChatHistoryManager.addMessage(original, isSystem, senderName);
        }

        // Add timestamp
        if (ModClientConfig.CONFIG.timestampsEnabled.get()) {
            String mode = ModClientConfig.CONFIG.timestampMode.get();
            if ("chat_only".equals(mode) || "both".equals(mode)) {
                String time = LocalTime.now().format(TIME_FORMATTER);
                MutableComponent timestamp = Component.literal("§7[" + time + "]§r ");
                event.setMessage(timestamp.copy().append(original));
            }
        }
    }

    @SubscribeEvent
    public static void onChatSend(ClientChatEvent event) {
        if (!ModClientConfig.CONFIG.masterEnabled.get()) return;
        if (event.isCanceled()) return;

        String message = event.getMessage();
        if (message.startsWith("/chatoptimizer") || message.startsWith("/co ")) {
            return;
        }

        // Record sent messages to history
        if (ModClientConfig.CONFIG.historyEnabled.get()) {
            ChatHistoryManager.addMessage(
                    Component.literal(message),
                    false,
                    Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getName().getString() : "You"
            );
        }
    }
}
