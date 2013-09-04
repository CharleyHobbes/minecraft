package charley.tileEntities;

import ic2.api.Direction;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityBasicElectricMachine extends TileEntityBasicMachine implements IEnergySink
{

	protected boolean connectedToEnergyNet = false;
	protected int charge;
	
	public TileEntityBasicElectricMachine(int inventorySize) 
	{
		super(inventorySize);
	}

	
	
//  NBT operations  ////////////////////////////////////////////////////////////
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		charge = tag.getInteger("charge");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("charge", charge);
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

	public abstract int getChargeCapacity();
	
	public void setCharge(int charge)
	{
		this.charge = charge;
	}
	
	public int getCharge()
	{
		return charge;
	}
	
	public float getChargeLevel()
	{
		return (float)charge / (float)getChargeCapacity(); 
	}
	
	public float getChargeLevelScaled(float scale)
	{
		return (float)charge / (float)getChargeCapacity() * scale;
	}

	
	
	
//  IEnergySink implementation  //////////////////////////////////////////////// 
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) 
	{
		return direction != this.getIc2Direction();
	}

	@Override
	public boolean isAddedToEnergyNet() 
	{
		return connectedToEnergyNet;
	}

	@Override
	public int demandsEnergy() 
	{
		return getChargeCapacity() - charge;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) 
	{
	/*
		if (amount > getMaxSafeInput()) 
		{
			IC2.explodeMachineAt(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			getWorldObj().setBlock(par1, par2, par3, par4, a, c);
			return 0;
		}
	*/
	
		if (charge >= getChargeCapacity()) {
			return amount;
		}
		
		charge += amount;
		return 0;
	}
	
}
