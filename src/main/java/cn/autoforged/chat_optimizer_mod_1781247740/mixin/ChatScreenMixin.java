package cn.autoforged.chat_optimizer_mod_1781247740.mixin;

import cn.autoforged.chat_optimizer_mod_1781247740.config.ModClientConfig;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Shadow
    protected EditBox input;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void chatoptimizer$modifyInput(CallbackInfo ci) {
        if (!ModClientConfig.CONFIG.masterEnabled.get()) return;
        this.input.setMaxLength(ModClientConfig.CONFIG.maxCharacters.get());
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatoptimizer$onRightClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && ModClientConfig.CONFIG.masterEnabled.get()) {
            ChatComponent chatComponent = this.minecraft.gui.getChat();
            String text = getMessageTextAt(chatComponent, mouseX, mouseY);
            if (text != null && !text.isEmpty()) {
                Minecraft.getInstance().keyboardHandler.setClipboard(text);
                if (this.minecraft.player != null) {
                    this.minecraft.player.displayClientMessage(
                            Component.literal("§7[Chat Optimizer] §aCopied to clipboard"), true);
                }
                cir.setReturnValue(true);
            }
        }
    }

    private String getMessageTextAt(ChatComponent chatComponent, double mouseX, double mouseY) {
        if (chatComponent == null) return null;
        ChatComponentAccessor accessor = (ChatComponentAccessor) chatComponent;
        List<GuiMessage.Line> trimmedMessages = accessor.getTrimmedMessages();
        if (trimmedMessages.isEmpty()) return null;

        int chatScrollbarPos = accessor.getChatScrollbarPos();
        double scale = chatComponent.getScale();
        double chatX = mouseX / scale - 4.0;
        int lineHeight = (int)(9.0 * (this.minecraft.options.chatLineSpacing().get() + 1.0));
        double chatY = (this.minecraft.getWindow().getGuiScaledHeight() - mouseY - 40.0) / (scale * (double) lineHeight);

        if (chatX < -4.0 || chatX > (double) chatComponent.getWidth() / scale) return null;
        if (chatY < 0 || chatY >= (double) chatComponent.getLinesPerPage()) return null;

        int lineIndex = (int) Math.floor(chatY + chatScrollbarPos);
        if (lineIndex < 0 || lineIndex >= trimmedMessages.size()) return null;

        GuiMessage.Line line = trimmedMessages.get(lineIndex);
        StringBuilder sb = new StringBuilder();
        line.content().accept((index, style, codePoint) -> {
            sb.append((char) codePoint);
            return true;
        });

        if (sb.isEmpty()) return null;

        List<GuiMessage> allMessages = accessor.getAllMessages();
        String targetText = sb.toString();
        for (int i = allMessages.size() - 1; i >= 0; i--) {
            String fullText = allMessages.get(i).content().getString();
            if (fullText.contains(targetText)) {
                return fullText;
            }
        }
        return sb.toString();
    }
}
