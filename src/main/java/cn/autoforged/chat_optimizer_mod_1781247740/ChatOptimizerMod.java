package cn.autoforged.chat_optimizer_mod_1781247740;

import cn.autoforged.chat_optimizer_mod_1781247740.config.ModClientConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(ChatOptimizerMod.MODID)
public class ChatOptimizerMod {
    public static final String MODID = "chat_optimizer_mod_1781247740";

    public ChatOptimizerMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.CONFIG_SPEC);
    }
}
