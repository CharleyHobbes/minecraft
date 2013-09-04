package charley.recipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.FMLCommonHandler;

public class RecipeManagerCellCleaner {

	public static void dbg(Object msg)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " - " + (msg == null ? "null" : msg.toString()));
	}
	private final Map<ItemStack, CellCleanerResult> recipes = new HashMap();
	private final Map<Integer, String> liquidTextureSheetFromId = new HashMap();
	private final Map<Integer, Icon> liquidRenderingIconFromId = new HashMap();
	
	private boolean initialized = false;

	private void addRecipe(ItemStack input, CellCleanerResult output) 
	{
		this.recipes.put(input, output);
	}

	public CellCleanerResult getOutputFor(ItemStack input) 
	{
		if(!initialized)
			initialize();
		
		for (Map.Entry<ItemStack, CellCleanerResult> entry : this.recipes.entrySet()) 
		{
			ItemStack recipeInput = (ItemStack)entry.getKey();

			if ((input.itemID == recipeInput.itemID) && ((input.getItemDamage() == recipeInput.getItemDamage()) || (recipeInput.getItemDamage() == 32767)) && (input.stackSize >= recipeInput.stackSize))
			{
				return entry.getValue();
			}
		}
		return null;
	}

	public Map<ItemStack, CellCleanerResult> getRecipes() 
	{
		if(!initialized)
			initialize();
		
		return recipes;
	}
	
	public void initialize()
	{
//		dbg("initialize");
		LiquidContainerData[] containers = LiquidContainerRegistry.getRegisteredLiquidContainerData();
		for(LiquidContainerData container : containers)
		{
			ItemStack filled = container.filled.copy();
			ItemStack empty = container.container.copy();
			LiquidStack liquid = container.stillLiquid.copy();
			
			if(empty.getItem() instanceof ItemBucket)
				liquid.amount = 1000;
			
			if(!liquidTextureSheetFromId.containsKey(liquid.itemID))
				liquidTextureSheetFromId.put(liquid.itemID, liquid.canonical().getTextureSheet());
			if(!liquidRenderingIconFromId.containsKey(liquid.itemID))
				liquidRenderingIconFromId.put(liquid.itemID, liquid.canonical().getRenderingIcon());
			
//			emptyContainerFromFilledContainer.put(filled, empty);
//			liquidFromFilledContainer.put(filled, liquid);
//			filledContainers.add(filled);

			this.addRecipe(filled, new CellCleanerResult(empty, liquid));
			dbg("Adding cellCleaner recipe: " + filled.getItem().getItemDisplayName(filled) + " -> " + empty.getItem().getItemDisplayName(empty) + " + " + liquid.asItemStack().getItem().getItemDisplayName(liquid.asItemStack()) + " " + liquid.amount);
		}
		
		initialized = true;
	}

	public String getTextureSheet(int id)
	{
		if(!initialized)
			initialize();
		
		return liquidTextureSheetFromId.get(id);
	}
	
	public Icon getRenderingIcon(int id)
	{
		if(!initialized)
			initialize();
		return liquidRenderingIconFromId.get(id);
	}
}
