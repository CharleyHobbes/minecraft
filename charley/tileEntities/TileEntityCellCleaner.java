package charley.tileEntities;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import charley.configuration.ModInfo;
import charley.network.PacketHandler;
import charley.recipe.CellCleanerResult;
import charley.recipe.Recipes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public class TileEntityCellCleaner extends TileEntityBasicElectricLiquidMachine
{
	public static void dbg(Object msg)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " - " + (msg == null ? "null" : msg.toString()));
	}
	
	public enum SlotId
	{
		filled(0),
		empty(1),
		battery(2);

		private int value;
		
		SlotId(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static int size()
		{
			return values().length;
		}
	}
	
	
	public TileEntityCellCleaner()
	{
		super(SlotId.size(), 10000);
	}

	
	
	public boolean canProcess(ItemStack item)
	{
		if(item == null)
			return false;
		
		CellCleanerResult res = Recipes.cellCleaner.getOutputFor(item);

		
		if(res == null)
			return false;

		if(res.emptyContainer == null)
			return false;
		
		if(res.liquid == null)
			return false;
		if(res.liquid.canonical() == null)
			return false;
		if(res.liquid.canonical().getRenderingIcon() == null)
			return false;
		
		
		if(tank.getLiquid() != null)
		{
			if(!res.liquid.isLiquidEqual(tank.getLiquid()))
				return false;
			
			if(res.liquid.amount > (tank.getCapacity() - tank.getLiquid().amount))
				return false;
		}
		
		if(getStackInSlot(SlotId.empty) != null)
		{
			if(!res.emptyContainer.isItemEqual(getStackInSlot(SlotId.empty)))
				return false;
			
			if(getStackInSlot(SlotId.empty).stackSize >= getStackInSlot(SlotId.empty).getMaxStackSize())
				return false;
		}
		
		return true;
	}
	
	public void processItem()
	{
		if(getStackInSlot(SlotId.filled) != null && canProcess(getStackInSlot(SlotId.filled)))
		{
			LiquidStack liquid = Recipes.cellCleaner.getOutputFor(getStackInSlot(SlotId.filled)).liquid;
//			dbg(liquid.canonical());
//			if(liquid.canonical() != null)
//				dbg(liquid.canonical().getRenderingIcon());
//			else
//				dbg("NULL");
			
			ItemStack empty = Recipes.cellCleaner.getOutputFor(getStackInSlot(SlotId.filled)).emptyContainer;
			
			tank.fill(liquid, true);
//			clientLiquidId = tank.getLiquid()
//			visibleLiquid = tank.getLiquid().canonical();
//			visibleLiquid.amount = tank.getLiquid().amount;
//			if(tank.getLiquid().amount >= tank.getCapacity() * 0.8)
//				tank.setLiquid(null);
			
			if(getStackInSlot(SlotId.empty) == null)
			{
				setInventorySlotContents(SlotId.empty, empty.copy());
			}
			else
			{
				if(getStackInSlot(SlotId.empty).itemID == empty.itemID && getStackInSlot(SlotId.empty).getItemDamage() == empty.getItemDamage())
				{
					getStackInSlot(SlotId.empty).stackSize ++;
				}
			}
			
			getStackInSlot(SlotId.filled).stackSize --;
			if(getStackInSlot(SlotId.filled).stackSize <= 0)
			{
				setInventorySlotContents(SlotId.filled, null);
			}
		}
		

		

		
		System.out.println("liquidAmount=" + getLiquidAmount() + "    charge=" + charge);
	}
	
	@Override
	public void updateEntity()
	{
		boolean inventoryUpdateRequired = false;

        if (!worldObj.isRemote)
        {
    		if(!connectedToEnergyNet)
    		{
    			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
    			connectedToEnergyNet = true;
    		}
        	
        	
        	if(!isWorking() && canProcess(getStackInSlot(SlotId.filled)))
        	{
        		workTotal = 400;
        		workTicks = 0;
                inventoryUpdateRequired = true;
        	}
        	
            if (isWorking() && canProcess(getStackInSlot(SlotId.filled)))
            {
            	if(charge > 2)
            	{
	                ++workTicks;
	
	        		charge -= 2;
	                
	                if (workTicks == workTotal)
	                {
	                	workTicks = 0;
	                	workTotal = 0;
	                    processItem();
	                    inventoryUpdateRequired = true;
	                }
            	}
            }
            else
            {
            	workTicks = 0;
            }

        }
        
        if(inventoryUpdateRequired)
        {
        	onInventoryChanged();
        }
	}
	
//	public void onLoaded()
//	{
//		super.onLoaded();
//		if (IC2.platform.isSimulating()) {
//			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
//			
//			this.addedToEnergyNet = true;
//		}
//	}
//	
//	public void onUnloaded()
//	{
//		if ((IC2.platform.isSimulating()) && (this.addedToEnergyNet)) {
//			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
//			
//			this.addedToEnergyNet = false;
//		}
//		
//		super.onUnloaded();
//	}

	
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 2, tag);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.customParam1);
	}


	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		if (tankIndex != defaultTankIndex)
			return null;
		LiquidStack liquid = tank.drain(maxDrain, doDrain);
		return liquid;
	}




//	@Override
	public void invalidate()
	{
		dbg("invalidate 1");
		if(!this.getWorldObj().isRemote && connectedToEnergyNet)
		{
			dbg("invalidate 2");
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			connectedToEnergyNet = false;
		}
		super.invalidate();
	}
	
	public void onChunkUnload()
	{
		
	}


	@Override
	public int getMaxSafeInput() {
		return 32;
	}


	@Override
	public int getChargeCapacity() {
		return 800;
	}



	@Override
	public int getSizeInventory() 
	{
		return SlotId.size();
	}
	
	public ItemStack getStackInSlot(SlotId slot)
	{
		return getStackInSlot(slot.getValue());
	}
	
	public void setInventorySlotContents(SlotId inventorySlot, ItemStack itemstack)
	{
		setInventorySlotContents(inventorySlot.getValue(), itemstack);
	}
}
