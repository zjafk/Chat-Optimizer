package cn.autoforged.chat_optimizer_mod_1781247740.gui;

import cn.autoforged.chat_optimizer_mod_1781247740.config.ModClientConfig;
import cn.autoforged.chat_optimizer_mod_1781247740.history.ChatHistoryManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatSearchScreen extends Screen {

    private static final int MAX_VISIBLE_ROWS = 20;
    private static final int ROW_HEIGHT = 12;

    private EditBox searchField;
    private List<ChatHistoryManager.ChatMessageEntry> results;
    private int scrollOffset = 0;
    private String currentKeyword = "";

    public ChatSearchScreen(String initialKeyword) {
        super(Component.literal("Chat Search"));
        this.currentKeyword = initialKeyword;
        this.results = ChatHistoryManager.searchMessages(initialKeyword, 500);
    }

    @Override
    protected void init() {
        super.init();
        this.searchField = new EditBox(this.font, this.width / 2 - 100, 10, 200, 16,
                Component.translatable("chat_optimizer_mod_1781247740.search.hint"));
        this.searchField.setMaxLength(100);
        this.searchField.setValue(currentKeyword);
        this.searchField.setResponder(this::onSearchChanged);
        this.searchField.setFocused(true);
        addRenderableWidget(this.searchField);
        setInitialFocus(this.searchField);
    }

    private void onSearchChanged(String keyword) {
        this.currentKeyword = keyword;
        this.results = ChatHistoryManager.searchMessages(keyword, 500);
        this.scrollOffset = 0;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int startY = 35;
        int visibleCount = Math.min(MAX_VISIBLE_ROWS, results.size() - scrollOffset);
        Font font = this.minecraft.font;

        for (int i = 0; i < visibleCount; i++) {
            int index = scrollOffset + i;
            if (index >= results.size()) break;
            ChatHistoryManager.ChatMessageEntry entry = results.get(index);

            int y = startY + i * ROW_HEIGHT;
            String prefix = entry.isSystem() ? "§7[SYS] " : "§e" + entry.sender() + "§r: ";
            String display = prefix + entry.content();

            if (font.width(display) > this.width - 20) {
                display = font.plainSubstrByWidth(display, this.width - 30);
            }

            guiGraphics.drawString(font, Component.literal(display), 10, y, 0xFFFFFF, false);

            if (entry.timestamp() > 0) {
                String timeStr = Instant.ofEpochMilli(entry.timestamp())
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
                guiGraphics.drawString(font, timeStr, this.width - 40, y, 0x555555, false);
            }
        }

        guiGraphics.drawString(font, "Results: " + results.size(), 10, this.height - 15, 0x555555, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (results.size() > MAX_VISIBLE_ROWS) {
            scrollOffset = (int) Math.max(0, Math.min(results.size() - MAX_VISIBLE_ROWS, scrollOffset - scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (ModClientConfig.CONFIG.historyAutoSave.get()) {
            ChatHistoryManager.saveToDisk();
        }
        super.onClose();
    }
}
