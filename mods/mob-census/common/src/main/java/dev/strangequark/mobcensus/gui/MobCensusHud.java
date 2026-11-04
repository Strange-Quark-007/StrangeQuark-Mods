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

    // Visual constants
    private static final int PADDING = 6;
    private static final int LINE_SPACING = 2;
    private static final int MIN_COL_WIDTH = 80;
    private static final int MAX_VISIBLE_ROWS = 10; // Cap rows to prevent overflowing off-screen
    private static final int MAX_NAME_LENGTH = 18;  // Ellipsize excessively long modded mob names

    private static final int BORDER_RGB = 0x00D4D4D4; // Crisp light

    // Threshold colors based on vanilla 70 Mob Cap
    private static final int COLOR_NORMAL = 0xFFFFFFFF;    // White (0 - 35)
    private static final int COLOR_HIGH = 0xFFFFAA00;      // Orange/Amber (36 - 69: High / Approaching Cap)
    private static final int COLOR_CAPPED = 0xFFFF4D4D;    // Red (70+: Mob Cap Reached)
    private static final int COLOR_SEPARATOR = 0x25FFFFFF; // Subtle translucent divider line
    private static final int COLOR_MUTED = 0xFF888888;     // Dimmed color for ellipsis / extra count
    private static final int COLOR_NAMED = 0xFF74C0FC;     // Soft ice-blue for name-tagged mobs

    public static void render(GuiGraphicsExtractor guiGraphics) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null || client.options.hideGui) {
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

        // Calculate heights to vertically center along the right edge (middle-right)
        int hostileHeight = showHostile ? calculateColumnHeight(result.hostileList(), lineHeight) : 0;
        int passiveHeight = showPassive ? calculateColumnHeight(result.passiveList(), lineHeight) : 0;
        int maxHeight = Math.max(hostileHeight, passiveHeight);

        // Middle-right vertical centering (keeps top-right clear for minimap mods)
        int topY = Math.max(8, ((screenHeight - maxHeight) / 2) + ModConfig.INSTANCE.yOffset);
        int xOffset = ModConfig.INSTANCE.xOffset;

        int alpha = Math.clamp((int) (ModConfig.INSTANCE.bgOpacity * 2.55f), 0, 255);
        int bgColor = (alpha << 24); // Translucent black box

        // Border opacity scales proportionally, using soft silver/slate tone
        int borderAlpha = Math.clamp((int) (alpha * 1.1f), 0, 255);
        int borderColor = (borderAlpha << 24) | BORDER_RGB;

        int currentRightX = screenWidth - xOffset;

        // Apply scale transformation
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale);

        try {
            // Render Passive column (rightmost if active)
            if (showPassive) {
                String title = Component.translatable("hud.mobcensus.passive").getString();
                String totalText = formatCount(result.totalPassive());
                int width = calculateColumnWidth(font, title, totalText, result.passiveList());
                int colX = currentRightX - width;
                renderColumn(guiGraphics, font, colX, topY, width, title, totalText, result.totalPassive(), result.passiveList(), bgColor, borderColor, alpha, lineHeight);
                currentRightX = colX - 4; // 4px spacing between columns
            }

            // Render Hostile column (left of passive, or rightmost if passive is disabled)
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
        if (count >= 10_000) {
            return String.format(Locale.ROOT, "%.1fk", count / 1000.0);
        } else if (count >= 1_000) {
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
            visibleCount++; // Extra row for "... +X more"
        }
        int baseHeight = PADDING + lineHeight + 3 + 1 + 3 + PADDING;
        return baseHeight + (visibleCount * lineHeight);
    }

    private static int calculateColumnWidth(Font font, String title, String totalText, List<MobCensusScanner.MobEntry> list) {
        int max = font.width(title) + 12 + font.width(totalText); // 12px gap between title and total count
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
            return COLOR_CAPPED; // Red (Mob Cap saturated)
        } else if (count >= 36) {
            return COLOR_HIGH;   // Orange (Approaching cap)
        }
        return COLOR_NORMAL;     // White (0 - 35)
    }

    private static void drawFramedBox2px(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, int bgColor, int borderColor) {
        // 1. Inside background fill (curved 2px inner body)
        guiGraphics.fill(x + 2, y + 1, x + width - 2, y + 2, bgColor);                     // Top inner row
        guiGraphics.fill(x + 1, y + 2, x + width - 1, y + height - 2, bgColor);             // Main center body
        guiGraphics.fill(x + 2, y + height - 2, x + width - 2, y + height - 1, bgColor);   // Bottom inner row

        // 2. 2px stepped outer border frame
        // Top and bottom horizontal lines
        guiGraphics.fill(x + 2, y, x + width - 2, y + 1, borderColor);
        guiGraphics.fill(x + 2, y + height - 1, x + width - 2, y + height, borderColor);

        // Corner step pixels (row 1 and row height-2)
        guiGraphics.fill(x + 1, y + 1, x + 2, y + 2, borderColor);
        guiGraphics.fill(x + width - 2, y + 1, x + width - 1, y + 2, borderColor);
        guiGraphics.fill(x + 1, y + height - 2, x + 2, y + height - 1, borderColor);
        guiGraphics.fill(x + width - 2, y + height - 2, x + width - 1, y + height - 1, borderColor);

        // Left and right vertical lines
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

        // 1. Draw 2px framed translucent background box (only if opacity > 0)
        if (alpha > 0) {
            drawFramedBox2px(guiGraphics, x, y, width, height, bgColor, borderColor);
        }

        // 2. Draw Header (Title left-aligned, Total count right-aligned)
        int textY = y + PADDING;
        int totalColor = getCountColor(totalCount);

        // Category title on left
        guiGraphics.text(font, title, x + PADDING, textY, 0xFFFFFFFF);

        // Total count aligned to the exact right margin
        int totalWidth = font.width(totalText);
        int totalX = (x + width) - PADDING - totalWidth;
        guiGraphics.text(font, totalText, totalX, textY, totalColor);

        textY += lineHeight + 2;

        // 3. Draw horizontal separator line (fades out if opacity is 0)
        if (alpha > 0) {
            guiGraphics.fill(x + PADDING, textY, (x + width) - PADDING, textY + 1, COLOR_SEPARATOR);
        }
        textY += 4; // gap below line

        // 4. Draw mob entries (up to MAX_VISIBLE_ROWS)
        int rowsToRender = Math.min(list.size(), MAX_VISIBLE_ROWS);
        for (int i = 0; i < rowsToRender; i++) {
            MobCensusScanner.MobEntry entry = list.get(i);
            String rawName = ellipsizeName(entry.name());
            String nameText;
            int nameColor;

            if (entry.isNamed()) {
                nameText = "§o" + rawName + ":";
                nameColor = COLOR_NAMED; // Soft ice-blue for name-tagged / persistent mobs
            } else {
                nameText = rawName + ":";
                nameColor = 0xFFE0E0E0; // Soft readable gray
            }

            String countText = formatCount(entry.count());
            int countColor = getCountColor(entry.count());

            // Mob name left-aligned
            guiGraphics.text(font, nameText, x + PADDING, textY, nameColor);

            // Count aligned to the exact right margin
            int countWidth = font.width(countText);
            int countX = (x + width) - PADDING - countWidth;
            guiGraphics.text(font, countText, countX, textY, countColor);

            textY += lineHeight;
        }

        // 5. Overflow ellipsis row if there are more mobs
        if (list.size() > MAX_VISIBLE_ROWS) {
            int remaining = list.size() - MAX_VISIBLE_ROWS;
            String overflowText = "… +" + remaining + " more";
            guiGraphics.text(font, overflowText, x + PADDING, textY, COLOR_MUTED);
        }
    }
}
