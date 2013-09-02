package charley.recipe;

import ic2.api.recipe.IMachineRecipeManager;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

public class RecipeManagerCellCleaner {

	private final Map<ItemStack, CellCleanerResult> recipes = new HashMap();
	

	public void addRecipe(ItemStack input, CellCleanerResult output) 
	{
		this.recipes.put(input, output);
	}

	public CellCleanerResult getOutputFor(ItemStack input) 
	{
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
		return recipes;
	}

}
