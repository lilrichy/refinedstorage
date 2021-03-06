package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.container.slot.SlotSpecimen;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(EntityPlayer player) {
        super(player);
    }

    public ContainerStorage(EntityPlayer player, IItemHandler filters) {
        this(player);

        addSpecimenAndPlayerInventorySlots(filters);
    }

    protected void addSpecimenAndPlayerInventorySlots(IItemHandler filters) {
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(filters, i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 129);
    }
}
