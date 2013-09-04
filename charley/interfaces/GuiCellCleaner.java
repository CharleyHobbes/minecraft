package charley.interfaces;

import java.awt.Point;
import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import charley.TextureCfg;
import charley.configuration.BlockInfo;
import charley.recipe.Recipes;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiCellCleaner extends GuiContainer {
	public static void dbg(Object msg)
	{
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " - " + (msg == null ? "null" : msg.toString()));
	}
	private TileEntityCellCleaner tileEntity;
	
	public GuiCellCleaner(InventoryPlayer invPlayer, TileEntityCellCleaner tileEntity) {
		super(new ContainerCellCleaner(invPlayer, tileEntity));
		
		this.tileEntity = tileEntity;
		xSize = guiSize.width;
		ySize = guiSize.height;
	}
	

	private static final String textrueLocation = TextureCfg.guiTextureDir + "GUI" + BlockInfo.cellCleaner.internalName + ".png";
	
	private static final Rectangle guiSize = new Rectangle(176, 166);
	
	private static final Point pistonTextureSrc = new Point(176, 31);
	private static final Point progressTextureSrc = new Point(176, 16);
	private static final Point chargeTextureSrc = new Point(176, 0);
	
	private static final Point pistonTextureDst = new Point(120, 61);
	private static final Point progressTextureDst = new Point(45, 34);
	private static final Point chargeTextureDst = new Point(20, 36);
	
	private static final Point chamberRefPoint = new Point(120, 66);
	
	private static final Rectangle pistonTextureRect = new Rectangle(30, 5);
	private static final Rectangle progressTextureRect = new Rectangle(22, 15);
	private static final Rectangle chargeTextureRect = new Rectangle(16, 16);
	private static final Rectangle chamberRect = new Rectangle(30, 46);

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		// TODO Auto-generated method stub

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(textrueLocation);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		
		
		
		
		// Drawing liquid in tank
		
		int pistonOffset = (int) tileEntity.getClientLiquidLevelScaled(chamberRect.height - pistonTextureRect.height);
		dbg(pistonOffset);
		if(pistonOffset + pistonTextureRect.height > chamberRect.height)
			pistonOffset = chamberRect.height - pistonTextureRect.height;
		if(pistonOffset < 0)
			pistonOffset = 0;
		
		int liquidRenderHeight = pistonOffset + (tileEntity.getClientLiquidAmount() > 0 ? 1 : 0);

		Point liquidDestination = new Point();
		Point pistonDestination = new Point();
		
		
		liquidDestination.x = guiLeft + chamberRefPoint.x;
		liquidDestination.y = guiTop + chamberRefPoint.y - liquidRenderHeight;
		
		pistonDestination.x = guiLeft + chamberRefPoint.x;
		pistonDestination.y = guiTop + chamberRefPoint.y - pistonOffset - pistonTextureRect.height;

		
		if(tileEntity.getClientLiquidLevel() > 0 && tileEntity.getClientLiquidId() != null)
		{
			Integer id = tileEntity.getClientLiquidId();
			Minecraft.getMinecraft().renderEngine.bindTexture(Recipes.cellCleaner.getTextureSheet(id));
			drawTexturedModelRectFromIcon(liquidDestination.x, liquidDestination.y, Recipes.cellCleaner.getRenderingIcon(id), chamberRect.width, liquidRenderHeight);

			Minecraft.getMinecraft().renderEngine.bindTexture(textrueLocation);
		}
		
		drawTexturedModalRect(pistonDestination.x, pistonDestination.y, pistonTextureSrc.x, pistonTextureSrc.y, pistonTextureRect.width, pistonTextureRect.height);
		
		// Drawing job progress bar
		
		int progressRenderWidth = (int) tileEntity.getWorkProgressScaled(progressTextureRect.width);
		if(progressRenderWidth > progressTextureRect.width)
			progressRenderWidth = progressTextureRect.width;
		if(progressRenderWidth < 0)
			progressRenderWidth = 0;
		
		Point progressDestination = new Point();
		
		progressDestination.x = guiLeft + progressTextureDst.x;
		progressDestination.y = guiTop + progressTextureDst.y;
		
		drawTexturedModalRect(progressDestination.x, progressDestination.y, progressTextureSrc.x, progressTextureSrc.y, progressRenderWidth, progressTextureRect.height);
		
		
		// Drawing charge level
		
		int chargeRenderHeigh = (int) tileEntity.getChargeLevelScaled(chargeTextureRect.height);
		if(chargeRenderHeigh > chargeTextureRect.height)
			chargeRenderHeigh = chargeTextureRect.height;
		if(chargeRenderHeigh < 0)
			chargeRenderHeigh = 0;
		
		Point chargeDestination = new Point();
		
		chargeDestination.x = guiLeft + chargeTextureDst.x;
		chargeDestination.y = guiTop + chargeTextureDst.y + chargeTextureRect.height - chargeRenderHeigh;
		
		drawTexturedModalRect(chargeDestination.x, chargeDestination.y, chargeTextureSrc.x, chargeTextureSrc.y + chargeTextureRect.height - chargeRenderHeigh, chargeTextureRect.width, chargeRenderHeigh);
		
	}

}
