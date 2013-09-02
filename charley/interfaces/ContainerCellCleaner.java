package charley.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCellCleaner extends Container {

	public enum progressId 
	{
		workProgress,
		workTotal,
		chargeLevel,
		liquidLevel;
	}
	
	TileEntityCellCleaner tileEntity;
	public static final int slotIdFilled = 0;
	public static final int slotIdEmpty = 1;
	public static final int slotIdBattery = 2;
	
	public static final int firstPlayerSlot = 3;
	public static final int lastPlayerSlot = 38;
	
	
	public int lastProgress = 0;
	public int lastTotal = 0;
	public int lastLiquidLevel = 0;
	public int lastChargeLevel = 0;
	
	public ContainerCellCleaner(InventoryPlayer invPlayer, TileEntityCellCleaner tileEntity)
	{
		this.tileEntity = tileEntity;

		
		addSlotToContainer(new Slot(tileEntity, TileEntityCellCleaner.slotFilled, 21, 17));					// #0 slot
		addSlotToContainer(new SlotCellCleaner(tileEntity, TileEntityCellCleaner.slotEmpty, 81, 35));		// #1 slot
		addSlotToContainer(new Slot(tileEntity, TileEntityCellCleaner.slotBattery, 21, 53));				// #2 slot
		
		
		for(int x = 0; x < 9; x ++)
		{
			addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142));
		}
		
		
		for(int y = 0; y < 3; y ++)
		{
			for(int x = 0; x < 9; x ++)
			{
				addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		
		
	}
	
	
    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, progressId.workProgress.ordinal(), this.tileEntity.workProgress);
        par1ICrafting.sendProgressBarUpdate(this, progressId.workTotal.ordinal(), this.tileEntity.workTotal);
        LiquidStack liquid = this.tileEntity.tank.getLiquid();
        par1ICrafting.sendProgressBarUpdate(this, progressId.liquidLevel.ordinal(), liquid == null ? 0 : liquid.amount);
        par1ICrafting.sendProgressBarUpdate(this, progressId.chargeLevel.ordinal(), this.tileEntity.chargeLevel);
    }
	
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            

            if (this.lastProgress != this.tileEntity.workProgress)
            {
                icrafting.sendProgressBarUpdate(this, progressId.workProgress.ordinal(), this.tileEntity.workProgress);
            }

            if (this.lastTotal != this.tileEntity.workTotal)
            {
                icrafting.sendProgressBarUpdate(this, progressId.workTotal.ordinal(), this.tileEntity.workTotal);
            }

            if(this.lastLiquidLevel != this.tileEntity.visibleLiquidLevel)
            {
            	icrafting.sendProgressBarUpdate(this, progressId.liquidLevel.ordinal(), tileEntity.visibleLiquidLevel);
            }
            
            
            if(this.lastChargeLevel != this.tileEntity.chargeLevel)
            {
            	icrafting.sendProgressBarUpdate(this, progressId.chargeLevel.ordinal(), this.tileEntity.chargeLevel);
            }
        }

        this.lastProgress = this.tileEntity.workProgress;
        this.lastTotal = this.tileEntity.workTotal;
        this.lastLiquidLevel = this.tileEntity.visibleLiquidLevel;
        this.lastChargeLevel = this.tileEntity.chargeLevel;
    }
    
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        if (id == progressId.workProgress.ordinal())
        {
            this.tileEntity.workProgress = value;
        }
        else if (id == progressId.workTotal.ordinal())
        {
            this.tileEntity.workTotal = value;
        }
        else if (id == progressId.chargeLevel.ordinal())
        {
        	this.tileEntity.chargeLevel = value;
        }
        else if (id == progressId.liquidLevel.ordinal())
        {
        	this.tileEntity.visibleLiquidLevel = value;
        }
        else
        {
        	System.out.println("Wrong progress bar id!");
        }
    }
    
    
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tileEntity.isUseableByPlayer(entityplayer);
	}

	
	
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID)
    {
        ItemStack returnedStack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotID);
        
        if (slot != null && slot.getHasStack())			// There's something in this slot
        {
            ItemStack transferedStack = slot.getStack();
            returnedStack = transferedStack.copy();

//            if (slotID == 2)
//            {
//                if (!this.mergeItemStack(transferedStack, firstPlayerSlot, lastPlayerSlot + 1, true))
//                {
//                    return null;
//                }
//
//                slot.onSlotChange(transferedStack, returnedStack);
//            }
            if (slotID > 2)
            {
            	if(tileEntity.canProcess(transferedStack))
            	{
	            	if (!this.mergeItemStack(transferedStack, 0, 1, false))
	            	{
	            		return null;
	            	}
            	}
            	
//                if (FurnaceRecipes.smelting().getSmeltingResult(transferedStack) != null)
//                {
//                    if (!this.mergeItemStack(transferedStack, 0, 1, false))
//                    {
//                        return null;
//                    }
//                }
//                else if (TileEntityFurnace.isItemFuel(transferedStack))
//                {
//                    if (!this.mergeItemStack(transferedStack, 1, 2, false))
//                    {
//                        return null;
//                    }
//                }
//                else if (slotID >= 3 && slotID < 30)
//                {
//                    if (!this.mergeItemStack(transferedStack, 30, 39, false))
//                    {
//                        return null;
//                    }
//                }
//                else if (slotID >= 30 && slotID < 39 && !this.mergeItemStack(transferedStack, 3, 30, false))
//                {
//                    return null;
//                }
//            	return null;
            }
            else if (!this.mergeItemStack(transferedStack, firstPlayerSlot, lastPlayerSlot + 1, false))
            {
                return null;
            }

            if (transferedStack.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (transferedStack.stackSize == returnedStack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(entityPlayer, transferedStack);
        }

        return returnedStack;
    }
	
}
