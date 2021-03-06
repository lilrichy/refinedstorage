package refinedstorage.gui;

import refinedstorage.api.storage.CompareFlags;
import refinedstorage.container.ContainerExporter;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileExporter;

public class GuiExporter extends GuiBase {
    private TileExporter exporter;

    public GuiExporter(ContainerExporter container, TileExporter exporter) {
        super(container, 211, 137);

        this.exporter = exporter;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(exporter));

        addSideButton(new SideButtonCompare(exporter, CompareFlags.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(exporter, CompareFlags.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/exporter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:exporter"));
        drawString(7, 43, t("container.inventory"));
    }
}
