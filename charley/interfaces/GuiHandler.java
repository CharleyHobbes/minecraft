package charley.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import charley.Charley;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

	public GuiHandler() {
		NetworkRegistry.instance().registerGuiHandler(Charley.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		switch(ID)
		{
		case 0:

			if(te != null && te instanceof TileEntityCellCleaner)
			{
				return new ContainerCellCleaner(player.inventory, (TileEntityCellCleaner)te);
			}
			break;
		}
		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : GuiHandler : getClientGuiElement : Failed");
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		switch(ID)
		{
		case 0:
			
			if(te != null && te instanceof TileEntityCellCleaner)
			{
				return new GuiCellCleaner(player.inventory, (TileEntityCellCleaner)te);
			}
			break;
		}
		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : GuiHandler : getClientGuiElement : Failed");
		return null;
	}

}
