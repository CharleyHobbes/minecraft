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

public class TileEntityCellCleaner extends TileEntity implements IInventory, ITankContainer, IEnergySink
{
	public static void dbg(Object msg)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " - " + (msg == null ? "null" : msg.toString()));
	}
	
	public static final int slotFilled = 0;
	public static final int slotEmpty = 1;
	public static final int slotBattery = 2;
	
	public static final int defaultTankIndex = 0;
	

	boolean isEnergyTileLoaded = false;
	
	private int facing;
	private ItemStack[] content;
	public LiquidTank tank;
	public int visibleLiquidLevel;
	
	
//	private boolean working;
	public int workProgress;
	public int workTotal;
	
	public int chargeLevel;
	public static final int maxChargeLevel = 1000;
	
	public TileEntityCellCleaner()
	{
//		working = false;
		workProgress = 0;
		workTotal = 0;
		visibleLiquidLevel = 0;
		content = new ItemStack[3];
		tank = new LiquidTank(10000);
	}
	
	public void setFacing(int facing)
	{
		this.facing = facing;
	}
	
	public int getFacing()
	{
		return this.facing;
	}
	
	
	public int getWorkProgress()
	{
		return workProgress;
	}
	
	public int getWorkTotal()
	{
		return workTotal;
	}
	
	public boolean isWorking()
	{
		return workTotal > 0 && workProgress < workTotal;
	}
	
	
	public int getLiquidLevel()
	{
		return tank.getLiquid() == null ? 0 : tank.getLiquid().amount;
	}
	
	public int getChargeLevel()
	{
		return chargeLevel;
	}
	
	
	public boolean canProcess(ItemStack item)
	{
//		dbg("canProcess");
		if(item == null){
			return false;
		}
		
		CellCleanerResult res = Recipes.cellCleaner.getOutputFor(item);

		if(res.emptyContainer == null)
			return false;
		
		if(res.liquid == null)
			return false;
		if(res.liquid.canonical() == null)
			return false;
		if(res.liquid.canonical().getRenderingIcon() == null)
			return false;
		
//		dbg(res);
		if(res == null)
			return false;

		if(tank.getLiquid() != null)
		{
			if(!res.liquid.isLiquidEqual(tank.getLiquid()))
				return false;
			
			if(res.liquid.amount > (tank.getCapacity() - tank.getLiquid().amount))
				return false;
		}
		
		if(content[slotEmpty] != null)
		{
			if(!res.emptyContainer.isItemEqual(content[slotEmpty]))
				return false;
			
			if(content[slotEmpty].stackSize >= content[slotEmpty].getMaxStackSize())
				return false;
		}
		
		return true;
	}
	
	public void processItem()
	{
		if(content[slotFilled] != null && canProcess(content[slotFilled]))
		{
			LiquidStack liquid = Recipes.cellCleaner.getOutputFor(content[slotFilled]).liquid;
			dbg(liquid.canonical());
			if(liquid.canonical() != null)
				dbg(liquid.canonical().getRenderingIcon());
			else
				dbg("NULL");
			
			ItemStack empty = Recipes.cellCleaner.getOutputFor(content[slotFilled]).emptyContainer;
			
			tank.fill(liquid, true);
			visibleLiquidLevel = tank.getLiquid().amount;
//			if(tank.getLiquid().amount >= tank.getCapacity() * 0.8)
//				tank.setLiquid(null);
			
			if(content[slotEmpty] == null)
			{
				content[slotEmpty] = empty.copy();
			}
			else
			{
				if(content[slotEmpty].itemID == empty.itemID && content[slotEmpty].getItemDamage() == empty.getItemDamage())
				{
					content[slotEmpty].stackSize ++;
				}
			}
			
			content[slotFilled].stackSize --;
			if(content[slotFilled].stackSize <= 0)
			{
				content[slotFilled] = null;
			}
		}
		

		

		
		System.out.println("liquidLevel=" + visibleLiquidLevel + "    chargeLevel=" + chargeLevel);
	}
	
	@Override
	public void updateEntity()
	{

		
		boolean inventoryUpdateRequired = false;

//		dbg("updateEntity 1");
        if (!worldObj.isRemote)
        {
    		if(!isEnergyTileLoaded)
    		{
//    			dbg("updateEntity 2");
    			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
    			isEnergyTileLoaded = true;
    		}
        	
        	
        	if(!isWorking() && canProcess(content[slotFilled]))
        	{
        		workTotal = 400;
        		workProgress = 0;
                inventoryUpdateRequired = true;
        	}
        	
            if (isWorking() && canProcess(content[slotFilled]))
            {
            	if(chargeLevel > 2)
            	{
	                ++workProgress;
	
	        		chargeLevel -= 2;
	                
	                if (workProgress == workTotal)
	                {
	                	workProgress = 0;
	                	workTotal = 0;
	                    processItem();
	                    inventoryUpdateRequired = true;
	                }
            	}
            }
            else
            {
            	workProgress = 0;
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
	
	////////////////////////////////////////////////////////////////////////////
	//  S T O R I N G   D A T A   I N   N B T  /////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void readFromNBT(NBTTagCompound root)
	{
		super.readFromNBT(root);
		
		this.facing = root.getInteger("facing");
		this.workProgress = root.getInteger("workProgress");
		this.workTotal = root.getInteger("workTotal");
		this.tank.readFromNBT(root.getCompoundTag("tank"));
		this.visibleLiquidLevel = root.getInteger("visibleLiquidLevel");
		this.chargeLevel = root.getInteger("chargeLevel");
		
		NBTTagList items = root.getTagList("items");
		for(int i = 0; i < items.tagCount(); i ++)
		{
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			int slot = item.getInteger("slot");
			
			if(slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}
	
	
	@Override
	public void writeToNBT(NBTTagCompound root)
	{
		super.writeToNBT(root);
		root.setInteger("facing", this.facing);
		root.setInteger("workProgress", workProgress);
		root.setInteger("workTotal", workTotal);
		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		root.setCompoundTag("tank", tankTag);
		root.setInteger("visibleLiquidLevel", visibleLiquidLevel);
		root.setInteger("chargeLevel", chargeLevel);
		
		NBTTagList items = new NBTTagList();
		for(int slot = 0; slot < getSizeInventory(); slot ++)
		{
			ItemStack stack = getStackInSlot(slot);
			if(stack != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				stack.writeToNBT(item);
				item.setInteger("slot", slot);
				items.appendTag(item);
			}
		}
		
		root.setTag("items", items);
	}
	
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
	
//
//	public void updateClientEntity() {
//		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
//		{
//			
//			Packet250CustomPayload p = new Packet250CustomPayload();
//			ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
//	        DataOutputStream outputStream = new DataOutputStream(bos);
//	        try {
//	                outputStream.writeByte(PacketHandler.requestUpdateTileEntity);
//	                outputStream.writeInt(xCoord);
//	                outputStream.writeInt(yCoord);
//	                outputStream.writeInt(zCoord);
//	        } catch (Exception ex) {
//	                ex.printStackTrace();
//	        }
//	       
//	        Packet250CustomPayload packet = new Packet250CustomPayload();
//	        packet.channel = ModInfo.channel;
//	        packet.data = bos.toByteArray();
//	        packet.length = bos.size();
//	        
//	        PacketDispatcher.sendPacketToServer(packet);
//		}
//	}
	
	////////////////////////////////////////////////////////////////////////////
	//  I I N V E N T O R Y   I M P L E M E N T A T I O N  /////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public int getSizeInventory() 
	{
		return 3;
	}

	
	
	@Override
	public ItemStack getStackInSlot(int inventorySlot) 
	{
		if(inventorySlot >= 0 && inventorySlot < getSizeInventory())
			return content[inventorySlot];
		return null;
	}

	
	
	@Override
	public ItemStack decrStackSize(int inventorySlot, int count)
	{
		ItemStack itemstack = getStackInSlot(inventorySlot);
		
		if(itemstack != null)
		{
			if(itemstack.stackSize <= count)
			{
				setInventorySlotContents(inventorySlot, null);
			}
			else
			{
				itemstack = itemstack.splitStack(count);
				onInventoryChanged();
			}
		}
		
		return itemstack;
	}

	
	
	@Override
	public ItemStack getStackInSlotOnClosing(int inventorySlot) 
	{
		ItemStack itemStack = getStackInSlot(inventorySlot);
		setInventorySlotContents(inventorySlot, null);
		return itemStack;
	}

	
	
	@Override
	public void setInventorySlotContents(int inventorySlot, ItemStack itemstack)
	{
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();
		
		if(inventorySlot >= 0 && inventorySlot < getSizeInventory())
			content[inventorySlot] = itemstack;
		
		onInventoryChanged();
	}

	
	
	@Override
	public String getInvName() 
	{
		return "Cell cleaner inventory";
	}

	
	
	@Override
	public boolean isInvNameLocalized() 
	{
		return false;
	}

	
	
	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 25;
	}

	
	
	@Override
	public void openChest() {}

	
	
	@Override
	public void closeChest() {}

	
	
	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) 
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(defaultTankIndex, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		if (tankIndex != defaultTankIndex)
			return null;
		LiquidStack liquid = tank.drain(maxDrain, doDrain);
		visibleLiquidLevel = getLiquidLevel();
		return liquid;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[]{tank};
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
		return true;
	}

	@Override
	public boolean isAddedToEnergyNet() {
		dbg("isAddedToEnergyNet");
		return isEnergyTileLoaded;
	}

	@Override
	public int demandsEnergy() {
		return maxChargeLevel - chargeLevel;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) 
	{
//		if (amount > 32) {
//			IC2.explodeMachineAt(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
//			getWorldObj().setBlock(par1, par2, par3, par4, a, c);
//			return 0;
//		}

		if (chargeLevel >= maxChargeLevel) {
			return amount;
		}
		
		chargeLevel += amount;
		return 0;
	}

	@Override
	public int getMaxSafeInput() {
		return 32;
	}

//	@Override
	public void invalidate()
	{
		dbg("invalidate 1");
		if(!this.getWorldObj().isRemote && isEnergyTileLoaded)
		{
			dbg("invalidate 2");
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			isEnergyTileLoaded = false;
		}
		super.invalidate();
	}
	
	public void onChunkUnload()
	{
		
	}
}
