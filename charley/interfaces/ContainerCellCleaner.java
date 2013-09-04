package charley.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import charley.recipe.Recipes;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCellCleaner extends Container {
	public static void dbg(Object msg)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " - " + (msg == null ? "null" : msg.toString()));
	}
	public enum progressId 
	{
		workTicks,
		workTotal,
		charge,
		liquidAmount, 
		liquidId;
	}
	
	TileEntityCellCleaner tileEntity;
	public static final Integer slotIdFilled = 0;
	public static final Integer slotIdEmpty = 1;
	public static final Integer slotIdBattery = 2;
	
	public static final Integer firstPlayerSlot = 3;
	public static final Integer lastPlayerSlot = 38;
	
	
	public Integer lastWorkTicks = 0;
	public Integer lastWorkTotal = 0;
	public Integer lastLiquidAmount = 0;
	public Integer lastCharge = 0;
	public Integer lastLiquidId = 0;
	
	public ContainerCellCleaner(InventoryPlayer invPlayer, TileEntityCellCleaner tileEntity)
	{
		this.tileEntity = tileEntity;

		
		addSlotToContainer(new Slot(tileEntity, TileEntityCellCleaner.SlotId.filled.getValue(), 21, 17));				// #0 slot
		addSlotToContainer(new SlotCellCleaner(tileEntity, TileEntityCellCleaner.SlotId.empty.getValue(), 81, 35));		// #1 slot
		addSlotToContainer(new Slot(tileEntity, TileEntityCellCleaner.SlotId.battery.getValue(), 21, 53));				// #2 slot
		
		
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
        par1ICrafting.sendProgressBarUpdate(this, progressId.workTicks.ordinal()	, this.tileEntity.getWorkTicks());
        par1ICrafting.sendProgressBarUpdate(this, progressId.workTotal.ordinal()	, this.tileEntity.getWorkTotal());
        par1ICrafting.sendProgressBarUpdate(this, progressId.charge.ordinal()		, this.tileEntity.getCharge());
        par1ICrafting.sendProgressBarUpdate(this, progressId.liquidAmount.ordinal()	, this.tileEntity.getLiquidAmount());
        par1ICrafting.sendProgressBarUpdate(this, progressId.liquidId.ordinal()		, this.tileEntity.getLiquidId());
    }

	
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            

            if (this.lastWorkTicks != this.tileEntity.getWorkTicks())
            {
                icrafting.sendProgressBarUpdate(this, progressId.workTicks.ordinal(), this.tileEntity.getWorkTicks());
            }

            if (this.lastWorkTotal != this.tileEntity.getWorkTotal())
            {
                icrafting.sendProgressBarUpdate(this, progressId.workTotal.ordinal(), this.tileEntity.getWorkTotal());
            }
            if(this.lastCharge != this.tileEntity.getCharge())
            {
            	icrafting.sendProgressBarUpdate(this, progressId.charge.ordinal(), this.tileEntity.getCharge());
            }

            if(this.lastLiquidAmount != this.tileEntity.getLiquidAmount())
            {
            	icrafting.sendProgressBarUpdate(this, progressId.liquidAmount.ordinal(), tileEntity.getLiquidAmount());
            }
            if(this.lastLiquidId != this.tileEntity.getLiquidId())
            {
            	icrafting.sendProgressBarUpdate(this, progressId.liquidId.ordinal(), tileEntity.getLiquidId());
            }
        }

        this.lastWorkTicks = this.tileEntity.getWorkTicks();
        this.lastWorkTotal = this.tileEntity.getWorkTotal();
        this.lastLiquidAmount = this.tileEntity.getLiquidAmount();
        this.lastLiquidId  = this.tileEntity.getLiquidId();
        this.lastCharge = this.tileEntity.getCharge();
    }
    
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        if (id == progressId.workTicks.ordinal())
        {
            this.tileEntity.setWorkTicks(value);
        }
        else if (id == progressId.workTotal.ordinal())
        {
            this.tileEntity.setWorkTotal(value);
        }
        else if (id == progressId.charge.ordinal())
        {
        	this.tileEntity.setCharge(value);
        }
        else if (id == progressId.liquidAmount.ordinal())
        {
        	this.tileEntity.setClientLiquidAmount(value);
        }
        else if (id == progressId.liquidId.ordinal())
        {
        	this.tileEntity.setClientLiquidId(value);
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
//        dbg(slotID);
        
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
//        		dbg(transferedStack.getDisplayName());
            	if(Recipes.cellCleaner.getOutputFor(transferedStack) != null)
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
