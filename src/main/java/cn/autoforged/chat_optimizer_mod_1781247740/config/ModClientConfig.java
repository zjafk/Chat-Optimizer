package cn.autoforged.chat_optimizer_mod_1781247740.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ModClientConfig {
    public static final ModClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<ModClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(ModClientConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    // Master switch
    public final ModConfigSpec.BooleanValue masterEnabled;

    // Infinite input
    public final ModConfigSpec.IntValue maxCharacters;

    // Timestamps
    public final ModConfigSpec.BooleanValue timestampsEnabled;
    public final ModConfigSpec.ConfigValue<String> timestampMode;
    public final ModConfigSpec.BooleanValue timestampSystemIndependent;
    public final ModConfigSpec.BooleanValue timestampPlayerIndependent;

    // History
    public final ModConfigSpec.BooleanValue historyEnabled;
    public final ModConfigSpec.BooleanValue historyAutoSave;
    public final ModConfigSpec.ConfigValue<String> historyClearStrategy;
    public final ModConfigSpec.BooleanValue historyLoadOnJoin;
    public final ModConfigSpec.IntValue historyLoadCount;

    // Right-click actions
    public final ModConfigSpec.ConfigValue<List<? extends String>> rightClickActions;

    // Auto commands
    public final ModConfigSpec.BooleanValue autoCommandsEnabled;
    public final ModConfigSpec.BooleanValue autoCommandsGlobal;
    public final ModConfigSpec.ConfigValue<List<? extends String>> autoCommandList;
    public final ModConfigSpec.BooleanValue autoCommandsEncryption;
    public final ModConfigSpec.IntValue autoCommandDelayTicks;

    // Search
    public final ModConfigSpec.BooleanValue searchEnabled;

    private ModClientConfig(ModConfigSpec.Builder builder) {
        builder.push("master");
        masterEnabled = builder.comment("Global master switch for the mod")
                .define("masterEnabled", true);
        builder.pop();

        builder.push("input");
        maxCharacters = builder.comment("Maximum characters in chat input field")
                .defineInRange("maxCharacters", 65536, 256, 65536);
        builder.pop();

        builder.push("timestamps");
        timestampsEnabled = builder.comment("Enable timestamps on chat messages")
                .define("timestampsEnabled", true);
        timestampMode = builder.comment("Timestamp mode: chat_only, disk_only, both")
                .define("timestampMode", "chat_only");
        timestampSystemIndependent = builder.comment("Independent timestamp setting for system messages")
                .define("timestampSystemIndependent", true);
        timestampPlayerIndependent = builder.comment("Independent timestamp setting for player messages")
                .define("timestampPlayerIndependent", true);
        builder.pop();

        builder.push("history");
        historyEnabled = builder.comment("Enable chat history recording")
                .define("historyEnabled", true);
        historyAutoSave = builder.comment("Auto-save chat history to disk")
                .define("historyAutoSave", true);
        historyClearStrategy = builder.comment("History clear strategy: timed, quantitative")
                .define("historyClearStrategy", "quantitative");
        historyLoadOnJoin = builder.comment("Load history when joining a server")
                .define("historyLoadOnJoin", true);
        historyLoadCount = builder.comment("Number of history messages to load on join")
                .defineInRange("historyLoadCount", 100, 0, 1000);
        builder.pop();

        builder.push("right_click");
        rightClickActions = builder.comment("Right-click actions: copy_plain_text, copy_raw_json")
                .defineListAllowEmpty("rightClickActions",
                        () -> List.of("copy_plain_text", "copy_raw_json"),
                        obj -> obj instanceof String);
        builder.pop();

        builder.push("auto_commands");
        autoCommandsEnabled = builder.comment("Enable auto commands on server join")
                .define("autoCommandsEnabled", true);
        autoCommandsGlobal = builder.comment("Use global auto-command config")
                .define("autoCommandsGlobal", true);
        autoCommandList = builder.comment("Default whitelist of auto commands")
                .defineListAllowEmpty("autoCommandList",
                        () -> List.of("/login", "/l"),
                        obj -> obj instanceof String);
        autoCommandsEncryption = builder.comment("Enable hardware-based encryption for stored commands")
                .define("autoCommandsEncryption", true);
        autoCommandDelayTicks = builder.comment("Delay in ticks before executing auto commands after login")
                .defineInRange("autoCommandDelayTicks", 5, 0, 200);
        builder.pop();

        builder.push("search");
        searchEnabled = builder.comment("Enable local chat search")
                .define("searchEnabled", true);
        builder.pop();
    }
}
