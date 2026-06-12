package cn.autoforged.chat_optimizer_mod_1781247740.handler;

import cn.autoforged.chat_optimizer_mod_1781247740.ChatOptimizerMod;
import cn.autoforged.chat_optimizer_mod_1781247740.config.ModClientConfig;
import cn.autoforged.chat_optimizer_mod_1781247740.history.ChatHistoryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.List;

@EventBusSubscriber(modid = ChatOptimizerMod.MODID, value = Dist.CLIENT)
public class AutoCommandHandler {

    private static int tickDelay = -1;
    private static boolean commandsQueued = false;

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        String serverIp = "singleplayer";
        ServerData serverData = Minecraft.getInstance().getCurrentServer();
        if (serverData != null) {
            serverIp = serverData.ip;
        }
        ChatHistoryManager.setCurrentServer(serverIp);

        if (!ModClientConfig.CONFIG.masterEnabled.get()) return;

        if (ModClientConfig.CONFIG.historyEnabled.get() && ModClientConfig.CONFIG.historyLoadOnJoin.get()) {
            ChatHistoryManager.loadFromDisk();
        }

        if (!ModClientConfig.CONFIG.autoCommandsEnabled.get()) return;
        commandsQueued = true;
        tickDelay = ModClientConfig.CONFIG.autoCommandDelayTicks.get();
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (ModClientConfig.CONFIG.historyEnabled.get() && ModClientConfig.CONFIG.historyAutoSave.get()) {
            ChatHistoryManager.saveToDisk();
        }
        commandsQueued = false;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!commandsQueued) return;
        if (Minecraft.getInstance().player == null) return;

        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        commandsQueued = false;
        executeQueuedCommands();
    }

    private static void executeQueuedCommands() {
        if (Minecraft.getInstance().player == null) return;

        List<? extends String> commands = ModClientConfig.CONFIG.autoCommandList.get();
        for (String cmd : commands) {
            if (cmd.startsWith("/")) {
                Minecraft.getInstance().player.connection.sendCommand(cmd.substring(1));
            }
        }
    }
}
