package charley.interfaces;

import java.awt.Point;
import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import charley.TextureCfg;
import charley.configuration.BlockInfo;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiCellCleaner extends GuiContainer {

	private TileEntityCellCleaner tileEntity;
	
	public GuiCellCleaner(InventoryPlayer invPlayer, TileEntityCellCleaner tileEntity) {
		super(new ContainerCellCleaner(invPlayer, tileEntity));
		
		this.tileEntity = tileEntity;
		xSize = guiSize.width;
		ySize = guiSize.height;
	}
	

	private static final String textrueLocation = TextureCfg.guiTextureDir + "GUI" + BlockInfo.cellCleaner.internalName + ".png";
	
	private static final Rectangle guiSize = new Rectangle(176, 166);
	
	private static final Point liquidTextureSrc = new Point(176, 14);
	private static final Point progressTextureSrc = new Point(206, 0);
	private static final Point chargeTextureSrc = new Point(176, 0);
	
	private static final Point liquidTextureDst = new Point(120, 20);
	private static final Point progressTextureDst = new Point(45, 34);
	private static final Point chargeTextureDst = new Point(24, 37);
	
	private static final Rectangle liquidTextureRect = new Rectangle(30, 46);
	private static final Rectangle progressTextureRect = new Rectangle(22, 15);
	private static final Rectangle chargeTextureRect = new Rectangle(7, 13);
	

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		// TODO Auto-generated method stub

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(textrueLocation);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		
		
		
		
		// Drawing liquid in tank
		float liquidLevel = (float)tileEntity.visibleLiquidLevel / (float)tileEntity.tank.getCapacity();
		
		int liquidRenderHeight = (int) (liquidLevel * (liquidTextureRect.height - 5)) + 5;
		if(liquidRenderHeight > liquidTextureRect.height)
			liquidRenderHeight = liquidTextureRect.height;
		if(liquidRenderHeight < 0)
			liquidRenderHeight = 0;

		Point liquidDestination = new Point();
		
		liquidDestination.x = guiLeft + liquidTextureDst.x;
		liquidDestination.y = guiTop + liquidTextureDst.y + liquidTextureRect.height - liquidRenderHeight;
		
		drawTexturedModalRect(liquidDestination.x, liquidDestination.y, liquidTextureSrc.x, liquidTextureSrc.y, liquidTextureRect.width, liquidRenderHeight);
		
		
		// Drawing job progress bar
		float progress = (float)tileEntity.workProgress / (float)tileEntity.workTotal;
		
		int progressRenderWidth = (int) (progress * progressTextureRect.width);
		if(progressRenderWidth > progressTextureRect.width)
			progressRenderWidth = progressTextureRect.width;
		if(progressRenderWidth < 0)
			progressRenderWidth = 0;
		
		Point progressDestination = new Point();
		
		progressDestination.x = guiLeft + progressTextureDst.x;
		progressDestination.y = guiTop + progressTextureDst.y;
		
		drawTexturedModalRect(progressDestination.x, progressDestination.y, progressTextureSrc.x, progressTextureSrc.y, progressRenderWidth, progressTextureRect.height);
		
		
		// Drawing charge level
		float charge = (float)tileEntity.chargeLevel / (float)tileEntity.maxChargeLevel;
		
		int chargeRenderHeigh = (int) (charge * chargeTextureRect.height);
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
