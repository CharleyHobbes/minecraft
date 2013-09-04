package charley.tileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityBasicElectricLiquidMachine extends TileEntityBasicElectricMachine implements ITankContainer 
{
	public static void dbg(Object msg)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " - " + (msg == null ? "null" : msg.toString()));
	}
	
	public static final int defaultTankIndex = 0;
	protected LiquidTank tank;
	
	@SideOnly(Side.CLIENT)
	protected Integer clientLiquidAmount;
	@SideOnly(Side.CLIENT)
	protected Integer clientLiquidId;
	
	public TileEntityBasicElectricLiquidMachine(int inventorySize, int tankCapacity)
	{
		super(inventorySize);
		tank = new LiquidTank(tankCapacity);
	}

	
	
//  NBT operations
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("tank"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setCompoundTag("tank", tank.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public NBTTagCompound getNBTTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		super.writeToNBT(tag);
		writeToNBT(tag);
		return tag;
	}
	
	
	
//  Setters and getters
	
	public LiquidStack getLiquid()
	{
		return tank.getLiquid();
	}
	
	public Integer getLiquidAmount()
	{
		return hasLiquid() ? getLiquid().amount : 0; 
	}

	public Integer getLiquidId()
	{
		return hasLiquid() ? getLiquid().itemID : -1;
	}
	
	public Integer getClientLiquidAmount()
	{
		return clientLiquidAmount;
	}
	
	public Integer getClientLiquidId()
	{
		return clientLiquidId;
	}
	
	public void setClientLiquidAmount(int amount)
	{
		clientLiquidAmount = amount;
	}
	
	public void setClientLiquidId(int id)
	{
		clientLiquidId = id == -1 ? null : id;
	}
	
	public float getClientLiquidLevel()
	{
		return (float)clientLiquidAmount / (float)tank.getCapacity();
	}
	
	public float getClientLiquidLevelScaled(float scale)
	{
		return (float)clientLiquidAmount / (float)tank.getCapacity() * scale;
	}
	
	public boolean hasLiquid()
	{
		return getLiquid() != null;
	}
	
	
	
//  ITankContainer implementation

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
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[]{tank};
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
	}
}
