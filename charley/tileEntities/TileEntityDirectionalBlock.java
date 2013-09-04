package charley.tileEntities;

import ic2.api.Direction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityDirectionalBlock extends TileEntity 
{

	protected ForgeDirection blockDirection;
	
	public TileEntityDirectionalBlock() { }
	
	
	
//  Setters and getters
	
	public ForgeDirection getDirection()
	{
		return blockDirection;
	}

	public void setDirection(ForgeDirection direction)
	{
		this.blockDirection = direction;
	}
	
	public Direction getIc2Direction()
	{
		return Direction.directions[(blockDirection.ordinal() + 2) % 6];
	}
	
	
	
//  NBT operations  ////////////////////////////////////////////////////////////
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		blockDirection = ForgeDirection.getOrientation(tag.getInteger("blockDirection"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("blockDirection", blockDirection.ordinal());
	}
	
	public NBTTagCompound getNBTTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		super.writeToNBT(tag);
		writeToNBT(tag);
		return tag;
	}
}
