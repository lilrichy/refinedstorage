package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.container.ContainerDetector;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.RedstoneMode;

public class TileDetector extends TileNode implements ICompareConfig {
    public static final int SPEED = 5;

    public static final int MODE_UNDER = 0;
    public static final int MODE_EQUAL = 1;
    public static final int MODE_ABOVE = 2;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_POWERED = "Powered";

    private BasicItemHandler filter = new BasicItemHandler(1, this);

    private int compare = 0;
    private int mode = MODE_EQUAL;
    private int amount = 0;

    private boolean powered = false;
    private boolean wasPowered;

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.detectorUsage;
    }

    @Override
    public void updateNode() {
        if (ticks % SPEED == 0) {
            ItemStack slot = filter.getStackInSlot(0);

            if (slot != null) {
                ItemStack stack = network.getStorage().get(slot, compare);

                if (stack != null) {
                    switch (mode) {
                        case MODE_UNDER:
                            powered = stack.stackSize < amount;
                            break;
                        case MODE_EQUAL:
                            powered = stack.stackSize == amount;
                            break;
                        case MODE_ABOVE:
                            powered = stack.stackSize > amount;
                            break;
                    }
                } else {
                    if (mode == MODE_UNDER && amount != 0) {
                        powered = true;
                    } else if (mode == MODE_EQUAL && amount == 0) {
                        powered = true;
                    } else {
                        powered = false;
                    }
                }
            } else {
                powered = false;
            }
        }
    }

    @Override
    public void update() {
        if (powered != wasPowered) {
            wasPowered = powered;

            worldObj.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.DETECTOR);

            RefinedStorageUtils.updateBlock(worldObj, pos);
        }

        super.update();
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        if (!state) {
            powered = false;
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }

        if (nbt.hasKey(NBT_AMOUNT)) {
            amount = nbt.getInteger(NBT_AMOUNT);
        }

        RefinedStorageUtils.readItems(filter, 0, nbt);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_AMOUNT, amount);

        RefinedStorageUtils.writeItems(filter, 0, tag);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        powered = tag.getBoolean(NBT_POWERED);

        super.readUpdate(tag);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_POWERED, powered);

        return tag;
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
        buf.writeInt(amount);
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
        amount = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerDetector.class;
    }

    public IItemHandler getInventory() {
        return filter;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
