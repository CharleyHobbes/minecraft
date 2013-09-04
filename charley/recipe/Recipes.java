package charley.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Recipes {

	private static boolean recipesRegistered = false;
	
	private static Map<ItemStack, ItemStack> emptyContainerFromFilledContainer;
	private static Map<ItemStack, LiquidStack> liquidFromFilledContainer;
	private static ArrayList<ItemStack> filledContainers;
	
	static
	{
		emptyContainerFromFilledContainer = new HashMap<ItemStack, ItemStack>();
		liquidFromFilledContainer = new HashMap<ItemStack, LiquidStack>();
		filledContainers = new ArrayList<ItemStack>();
	}
	
	public static final int bucketSize = 1000;
	
	@SideOnly(Side.CLIENT)
	public static void registerRecipes()
	{
		if(recipesRegistered)
			return;
		
//		Map<String, LiquidStack> liquids = LiquidDictionary.getLiquids();
//		
//		for(Map.Entry<String, LiquidStack> entry : liquids.entrySet())
//		{
//			System.out.println("================================================================================");
//			System.out.println("Liquid found: " + entry.getKey());
//			printLiquidStack(entry.getValue());
//			
//			
//			
////			
////			LiquidStack l = LiquidDictionary.getCanonicalLiquid("itemCellWater");
////			if(l != null)
////				System.out.println("              " + l + "    " + l.itemID + "    " + l.asItemStack().getItemName());
////			System.out.println("              " + cell + "    " + cell.itemID );
//		}
//		System.out.println("================================================================================");
		
		
//		ItemStack cell = ic2.api.item.Items.getItem("waterCell");
//		System.out.println("name : " + cell.getItemName());
//		LiquidStack ls = LiquidDictionary.getCanonicalLiquid(cell.getItemName());
//		if(ls == null)
//			System.out.println("liquid : null");
//		else
//			System.out.println("liquid : " + ls.asItemStack().getItemName());
		
		
//		int oreId = OreDictionary.getOreID(cell);
//		String oreName = OreDictionary.getOreName(oreId);
//		ArrayList<ItemStack> ores = OreDictionary.getOres(oreId);
		
//		for(ItemStack ore : ores)
//		{
//			System.out.println("================================================================================");
//			System.out.println("Ore found: " + ore);
//			System.out.println("      ore  " + ore.getItemName());
//			System.out.println("       id  " + ore.itemID);
//		}
//		if(!ores.isEmpty())
//			System.out.println("================================================================================");
		
		
		
//		LiquidStack l = LiquidContainerRegistry.getLiquidForFilledItem(cell);
//		if(l != null)
//		{
//			System.out.println("================================================================================");
//			System.out.println("Liquid found: " + l);
//			printLiquidStack(l);
////			System.out.println("          id  " + l.itemID);
////			System.out.println("        item  " + l.asItemStack());
////			System.out.println("          id  " + l.asItemStack().itemID);
////			System.out.println("        name  " + l.asItemStack().getItemName());
//		}
		
//		ItemStack lc = LiquidContainerRegistry.
		

//		if(containers.length > 0)
//			System.out.println("================================================================================");
		
//		for(ItemStack filled : filledContainers)
//		{
//			System.out.println("================================================================================");
//			System.out.println(s.substring(filled.getItem().getItemDisplayName(filled).length()) + filled.getItem().getItemDisplayName(filled) + " : " + liquidFromFilledContainer.get(filled).asItemStack().getItem().getItemDisplayName(liquidFromFilledContainer.get(filled).asItemStack()) + " " + liquidFromFilledContainer.get(filled).amount);
//			System.out.println(s + "   " + emptyContainerFromFilledContainer.get(filled).getItem().getItemDisplayName(emptyContainerFromFilledContainer.get(filled)));
//		}
//		if(!filledContainers.isEmpty())
//			System.out.println("================================================================================");
	}
	
	public static final RecipeManagerCellCleaner cellCleaner = new RecipeManagerCellCleaner();
	
	
	
	
	public static final String s = "                             "; 
	
	
	public static void printLiquidStack(LiquidStack liquid)
	{
		printLiquidStack(liquid, "liquid", false);
	}
	
	public static void printLiquidStack(LiquidStack liquid, String liquidDescription)
	{
		printLiquidStack(liquid, liquidDescription, false);
	}
	
	public static void printLiquidStack(LiquidStack liquid, boolean isCanonical)
	{
		printLiquidStack(liquid, "liquid", isCanonical);
	}
	
	public static void printLiquidStack(LiquidStack liquidStack, String liquidDescription, boolean isCanonical)
	{
		
		String canonical = isCanonical ? "can." : "    ";

		String liquidDesc = canonical + " " + liquidDescription + " : ";
		String idDesc = canonical + " id   ";
		String amountDesc = canonical + " amount   ";

		System.out.println(s.substring(liquidDesc.length()) + liquidDesc + liquidStack);
		System.out.println(s.substring(idDesc.length()) + idDesc + liquidStack.itemID);
		System.out.println(s.substring(amountDesc.length()) + amountDesc + liquidStack.amount);

		System.out.println();
		
		printItemStack(liquidStack.asItemStack(), canonical + " " + liquidDescription + " item");
		
		if(!isCanonical)
		{
			System.out.println();
			printLiquidStack(LiquidDictionary.getCanonicalLiquid(liquidStack), liquidDescription, true);
		}
	}
	
	public static void printLiquidContainerData(LiquidContainerData data)
	{
		System.out.println("Container data : " + data);
		System.out.println();
		printLiquidStack(data.stillLiquid, "still liquid", false);
//		System.out.println("  still liquid   " + data.stillLiquid);
//		System.out.println("       item id   " + data.stillLiquid.asItemStack().itemID);
//		System.out.println("     item name   " + data.stillLiquid.asItemStack().getItemName());
		System.out.println();
		printItemStack(data.filled, "filled");
//		System.out.println("        filled   " + data.filled);
//		System.out.println("            id   " + data.filled.itemID);
//		System.out.println("          name   " + data.filled.getItemName());
		System.out.println();
		printItemStack(data.container, "container");
	}

	public static void printItemStack(ItemStack stack)
	{
		printItemStack(stack, "item");
	}
	
	public static void printItemStack(ItemStack stack, String itemDescription)
	{
		String itemDesc = itemDescription + " : ";
		String itemId = "id   ";
		String itemDamage = "damage   ";
		String itemName = "name   ";
//		System.out.println(itemDesc.length());
//		System.out.println(itemDesc);
//		System.out.println(s.length());
		
		System.out.println(s.substring(itemDesc.length()) + itemDesc + stack);
		System.out.println(s.substring(itemId.length()) + itemId + stack.itemID);
		System.out.println(s.substring(itemDamage.length()) + itemDamage + stack.getItemDamage());
		System.out.println(s.substring(itemName.length()) + itemName + stack.getItemName());
	}
	
	
	protected Recipes() { }
}
