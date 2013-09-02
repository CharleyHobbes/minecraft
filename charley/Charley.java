package charley;


import ic2.api.item.Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import charley.blocks.BlockCellCleaner;
import charley.configuration.BlockInfo;
import charley.configuration.ModInfo;
import charley.interfaces.GuiHandler;
import charley.proxies.BaseProxy;
import charley.recipe.Recipes;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = ModInfo.id, name = ModInfo.name, version = ModInfo.version)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {ModInfo.channel}, packetHandler = charley.network.PacketHandler.class)

public class Charley 
{
	@Instance(ModInfo.id)
	public static Charley instance;
	
	@SidedProxy(clientSide="charley.proxies.ClientProxy", serverSide="charley.proxies.ServerProxy")
	public static BaseProxy proxy;
	
	
	public Configuration config;
	
	public Block block;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
	}
	
	@Init
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		
		
		block = new BlockCellCleaner(BlockInfo.cellCleaner, config).register();

		
		ic2.api.recipe.Recipes.advRecipes.addRecipe(new ItemStack(block), new Object[] {
			"CCC", " M ", "   ", 'C', Items.getItem("waterCell"), 'M', Items.getItem("machine")
		});
		
		new GuiHandler();
		
		Recipes.registerRecipes();
//		
//		System.out.println(FMLCommonHandler.instance().getEffectiveSide());
//		
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		config.save();
	}
}
