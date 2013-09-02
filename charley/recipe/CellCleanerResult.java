package charley.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

public class CellCleanerResult {
	public ItemStack emptyContainer;
	public LiquidStack liquid;
	
	public CellCleanerResult() {}
	public CellCleanerResult(ItemStack emptyContainer, LiquidStack liquid)
	{
		this.emptyContainer = emptyContainer;
		this.liquid = liquid;
	}
}
