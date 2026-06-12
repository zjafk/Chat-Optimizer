package cn.autoforged.chat_optimizer_mod_1781247740.history;

import cn.autoforged.chat_optimizer_mod_1781247740.ChatOptimizerMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatHistoryManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path HISTORY_DIR = Path.of("config", ChatOptimizerMod.MODID, "history");
    private static final int MAX_MEMORY_MESSAGES = 1000;
    private static final Type HISTORY_TYPE = new TypeToken<List<ChatMessageEntry>>() {}.getType();

    private static final ConcurrentLinkedDeque<ChatMessageEntry> messages = new ConcurrentLinkedDeque<>();
    private static String currentServerKey = "unknown";

    public record ChatMessageEntry(String content, String sender, long timestamp, boolean isSystem, boolean isPlayer) {}

    public static void setCurrentServer(String serverIp) {
        currentServerKey = sanitizeServerKey(serverIp);
    }

    private static String sanitizeServerKey(String ip) {
        if (ip == null || ip.isBlank()) return "unknown";
        return ip.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static String getCurrentServerKey() {
        return currentServerKey;
    }

    public static void addMessage(ChatMessageEntry entry) {
        messages.addFirst(entry);
        while (messages.size() > MAX_MEMORY_MESSAGES) {
            messages.pollLast();
        }
    }

    public static void addMessage(Component component, boolean isSystem, String senderName) {
        String content = component.getString();
        addMessage(new ChatMessageEntry(content, senderName, Instant.now().toEpochMilli(), isSystem, !isSystem));
    }

    public static List<ChatMessageEntry> getRecentMessages(int count) {
        List<ChatMessageEntry> result = new ArrayList<>();
        Iterator<ChatMessageEntry> it = messages.iterator();
        int i = 0;
        while (it.hasNext() && i < count) {
            result.add(it.next());
            i++;
        }
        return result;
    }

    public static List<ChatMessageEntry> searchMessages(String keyword, int maxResults) {
        List<ChatMessageEntry> result = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            return getRecentMessages(maxResults);
        }
        String lower = keyword.toLowerCase();
        for (ChatMessageEntry entry : messages) {
            if (result.size() >= maxResults) break;
            if (entry.content().toLowerCase().contains(lower)
                    || entry.sender().toLowerCase().contains(lower)) {
                result.add(entry);
            }
        }
        return result;
    }

    public static void saveToDisk() {
        if (!HISTORY_DIR.toFile().exists()) {
            HISTORY_DIR.toFile().mkdirs();
        }
        Path file = HISTORY_DIR.resolve(currentServerKey + ".json");
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            List<ChatMessageEntry> list = new ArrayList<>(messages);
            Collections.reverse(list);
            GSON.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromDisk() {
        Path file = HISTORY_DIR.resolve(currentServerKey + ".json");
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<ChatMessageEntry> loaded = GSON.fromJson(reader, HISTORY_TYPE);
            if (loaded != null) {
                messages.clear();
                messages.addAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearHistory() {
        messages.clear();
        Path file = HISTORY_DIR.resolve(currentServerKey + ".json");
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {}
    }

    public static int getMessageCount() {
        return messages.size();
    }
}
