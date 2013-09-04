package charley.tileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityBasicMachine extends TileEntityDirectionalBlock implements IBasicMachine, IInventory
{
	
	protected int workTicks;
	protected int workTotal;
	
	protected ItemStack[] machineInventory;
	
	public TileEntityBasicMachine(int inventorySize) 
	{
		machineInventory = new ItemStack[inventorySize];
	}
	
	
	
//  NBT operations  ////////////////////////////////////////////////////////////
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		workTicks = tag.getInteger("workTicks");
		workTotal = tag.getInteger("workTotal");
		
		NBTTagList items = tag.getTagList("machineInventory");

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
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("workTicks", workTicks);
		tag.setInteger("workTotal", workTotal);
		
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
		
		tag.setTag("machineInventory", items);
	}
	
	@Override
	public NBTTagCompound getNBTTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		super.writeToNBT(tag);
		writeToNBT(tag);
		return tag;
	}
	
	
	
//  Setters and getters  ///////////////////////////////////////////////////////
	
	public void setWorkTicks(int workProgress)
	{
		this.workTicks = workProgress;
	}
	
	public void setWorkTotal(int workTotal)
	{
		this.workTotal = workTotal;
	}
	
	
	
//  IBasicMachine partial implementation  //////////////////////////////////////

	@Override
	public int getWorkTicks() {
		return workTicks;
	}

	@Override
	public int getWorkTotal() {
		return workTotal;
	}

	@Override
	public boolean isWorking() {
		return workTotal > 0;
	}
	
	@Override
	public float getWorkProgress()
	{
		return (float)workTicks / (float)workTotal;
	}
	
	@Override
	public float getWorkProgressScaled(float scale)
	{
		return (float)workTicks / (float)workTotal * scale;
	}
	
//  IInventory implementation  /////////////////////////////////////////////////

	@Override
	public ItemStack getStackInSlot(int inventorySlot) 
	{
		if(inventorySlot >= 0 && inventorySlot < getSizeInventory())
			return machineInventory[inventorySlot];
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
			machineInventory[inventorySlot] = itemstack;
		
		onInventoryChanged();
	}
	
	@Override
	public String getInvName() 
	{
		return "BasicMachineInventory";
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
}
