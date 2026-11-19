package dev.strangequark.mobcensus.gui;

import dev.strangequark.mobcensus.config.ModConfig;
import dev.strangequark.mobcensus.scanner.MobCensusScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public class MobCensusHud {

    private static final int PADDING = 6;
    private static final int LINE_SPACING = 2;
    private static final int MIN_COL_WIDTH = 80;
    private static final int MAX_VISIBLE_ROWS = 10;
    private static final int MAX_NAME_LENGTH = 18;

    private static final int BORDER_RGB = 0x00D4D4D4;
    private static final int COLOR_NORMAL = 0xFFFFFFFF;
    private static final int COLOR_HIGH = 0xFFFFAA00;
    private static final int COLOR_CAPPED = 0xFFFF4D4D;
    private static final int COLOR_SEPARATOR = 0x25FFFFFF;
    private static final int COLOR_MUTED = 0xFF888888;
    private static final int COLOR_NAMED = 0xFF74C0FC;

    public static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null || client.gui.hud.isHidden()) {
            return;
        }

        boolean showHostile = ModConfig.INSTANCE.hostileEnabled;
        boolean showPassive = ModConfig.INSTANCE.passiveEnabled;
        if (!showHostile && !showPassive) {
            return;
        }

        MobCensusScanner.CensusResult result = MobCensusScanner.LATEST;
        Font font = client.font;

        float scale = Math.clamp(ModConfig.INSTANCE.hudScale / 100.0f, 0.4f, 2.0f);
        int screenWidth = (int) (guiGraphics.guiWidth() / scale);
        int screenHeight = (int) (guiGraphics.guiHeight() / scale);
        int lineHeight = font.lineHeight + LINE_SPACING;

        int hostileHeight = showHostile ? calculateColumnHeight(result.hostileList(), lineHeight) : 0;
        int passiveHeight = showPassive ? calculateColumnHeight(result.passiveList(), lineHeight) : 0;
        int maxHeight = Math.max(hostileHeight, passiveHeight);

        int topY = Math.max(8, ((screenHeight - maxHeight) / 2) + ModConfig.INSTANCE.yOffset);
        int xOffset = ModConfig.INSTANCE.xOffset;

        int alpha = Math.clamp((int) (ModConfig.INSTANCE.bgOpacity * 2.55f), 0, 255);
        int bgColor = alpha << 24;

        int borderAlpha = Math.clamp((int) (alpha * 1.1f), 0, 255);
        int borderColor = (borderAlpha << 24) | BORDER_RGB;

        int currentRightX = screenWidth - xOffset;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale);

        try {
            if (showPassive) {
                String title = Component.translatable("hud.mobcensus.passive").getString();
                String totalText = formatCount(result.totalPassive());
                int width = calculateColumnWidth(font, title, totalText, result.passiveList());
                int colX = currentRightX - width;
                renderColumn(guiGraphics, font, colX, topY, width, title, totalText, result.totalPassive(), result.passiveList(), bgColor, borderColor, alpha, lineHeight);
                currentRightX = colX - 4;
            }

            if (showHostile) {
                String title = Component.translatable("hud.mobcensus.hostile").getString();
                String totalText = formatCount(result.totalHostile());
                int width = calculateColumnWidth(font, title, totalText, result.hostileList());
                int colX = currentRightX - width;
                renderColumn(guiGraphics, font, colX, topY, width, title, totalText, result.totalHostile(), result.hostileList(), bgColor, borderColor, alpha, lineHeight);
            }
        } finally {
            guiGraphics.pose().popMatrix();
        }
    }

    private static String formatCount(int count) {
        if (count >= 1_000) {
            return String.format(Locale.ROOT, "%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }

    private static String ellipsizeName(String name) {
        if (name != null && name.length() > MAX_NAME_LENGTH) {
            return name.substring(0, MAX_NAME_LENGTH - 1) + "…";
        }
        return name;
    }

    private static int calculateColumnHeight(List<MobCensusScanner.MobEntry> list, int lineHeight) {
        int visibleCount = Math.min(list.size(), MAX_VISIBLE_ROWS);
        if (list.size() > MAX_VISIBLE_ROWS) {
            visibleCount++;
        }
        int baseHeight = PADDING + lineHeight + 3 + 1 + 3 + PADDING;
        return baseHeight + (visibleCount * lineHeight);
    }

    private static int calculateColumnWidth(Font font, String title, String totalText, List<MobCensusScanner.MobEntry> list) {
        int max = font.width(title) + 12 + font.width(totalText);
        int rowsToCheck = Math.min(list.size(), MAX_VISIBLE_ROWS);

        for (int i = 0; i < rowsToCheck; i++) {
            MobCensusScanner.MobEntry entry = list.get(i);
            String displayName = ellipsizeName(entry.name());
            String formattedCount = formatCount(entry.count());
            int w = font.width(displayName + ": ") + font.width(formattedCount);
            if (w > max) {
                max = w;
            }
        }

        if (list.size() > MAX_VISIBLE_ROWS) {
            int extra = list.size() - MAX_VISIBLE_ROWS;
            int moreWidth = font.width("… +" + extra + " more");
            if (moreWidth > max) {
                max = moreWidth;
            }
        }

        return Math.max(MIN_COL_WIDTH, max + (PADDING * 2));
    }

    private static int getCountColor(int count) {
        if (count >= 70) {
            return COLOR_CAPPED;
        } else if (count >= 36) {
            return COLOR_HIGH;
        }
        return COLOR_NORMAL;
    }

    private static void drawFramedBox2px(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, int bgColor, int borderColor) {
        guiGraphics.fill(x + 2, y + 1, x + width - 2, y + 2, bgColor);
        guiGraphics.fill(x + 1, y + 2, x + width - 1, y + height - 2, bgColor);
        guiGraphics.fill(x + 2, y + height - 2, x + width - 2, y + height - 1, bgColor);

        guiGraphics.fill(x + 2, y, x + width - 2, y + 1, borderColor);
        guiGraphics.fill(x + 2, y + height - 1, x + width - 2, y + height, borderColor);

        guiGraphics.fill(x + 1, y + 1, x + 2, y + 2, borderColor);
        guiGraphics.fill(x + width - 2, y + 1, x + width - 1, y + 2, borderColor);
        guiGraphics.fill(x + 1, y + height - 2, x + 2, y + height - 1, borderColor);
        guiGraphics.fill(x + width - 2, y + height - 2, x + width - 1, y + height - 1, borderColor);

        guiGraphics.fill(x, y + 2, x + 1, y + height - 2, borderColor);
        guiGraphics.fill(x + width - 1, y + 2, x + width, y + height - 2, borderColor);
    }

    private static void renderColumn(
            GuiGraphicsExtractor guiGraphics,
            Font font,
            int x,
            int y,
            int width,
            String title,
            String totalText,
            int totalCount,
            List<MobCensusScanner.MobEntry> list,
            int bgColor,
            int borderColor,
            int alpha,
            int lineHeight
    ) {
        int height = calculateColumnHeight(list, lineHeight);

        if (alpha > 0) {
            drawFramedBox2px(guiGraphics, x, y, width, height, bgColor, borderColor);
        }

        int textY = y + PADDING;
        int totalColor = getCountColor(totalCount);

        guiGraphics.text(font, title, x + PADDING, textY, 0xFFFFFFFF);

        int totalWidth = font.width(totalText);
        int totalX = (x + width) - PADDING - totalWidth;
        guiGraphics.text(font, totalText, totalX, textY, totalColor);

        textY += lineHeight + 2;

        if (alpha > 0) {
            guiGraphics.fill(x + PADDING, textY, (x + width) - PADDING, textY + 1, COLOR_SEPARATOR);
        }
        textY += 4;

        int rowsToRender = Math.min(list.size(), MAX_VISIBLE_ROWS);
        for (int i = 0; i < rowsToRender; i++) {
            MobCensusScanner.MobEntry entry = list.get(i);
            String rawName = ellipsizeName(entry.name());
            String nameText;
            int nameColor;

            if (entry.isNamed()) {
                nameText = "§o" + rawName + ":";
                nameColor = COLOR_NAMED;
            } else {
                nameText = rawName + ":";
                nameColor = 0xFFE0E0E0;
            }

            String countText = formatCount(entry.count());
            int countColor = getCountColor(entry.count());

            guiGraphics.text(font, nameText, x + PADDING, textY, nameColor);

            int countWidth = font.width(countText);
            int countX = (x + width) - PADDING - countWidth;
            guiGraphics.text(font, countText, countX, textY, countColor);

            textY += lineHeight;
        }

        if (list.size() > MAX_VISIBLE_ROWS) {
            int remaining = list.size() - MAX_VISIBLE_ROWS;
            guiGraphics.text(font, "… +" + remaining + " more", x + PADDING, textY, COLOR_MUTED);
        }
    }
}
