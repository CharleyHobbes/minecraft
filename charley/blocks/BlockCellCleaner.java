package charley.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import charley.Charley;
import charley.configuration.BlockInfo;
import charley.configuration.ModInfo;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockCellCleaner extends BlockContainer
{
	protected BlockInfo blockInfo;
	
	public BlockInfo getBlockInfo()
	{
		return blockInfo;
	}
	
	@SideOnly(Side.CLIENT)
	Icon frontIcon;
	@SideOnly(Side.CLIENT)
	Icon rearIcon;
	
	
	public BlockCellCleaner(BlockInfo info)
	{
		super(info.id, Material.iron);
		
		blockInfo = info;
		
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(1.5F);
		setResistance(10.0F);
		setUnlocalizedName(blockInfo.unlocalizedName);
	}
	
	public BlockCellCleaner(BlockInfo info, Configuration config)
	{
		super(config.get("Blocks", info.internalName, info.id).getInt(), Material.iron);
		
		blockInfo = info;
		blockInfo.id = config.get("Blocks", blockInfo.internalName, info.id).getInt();
		
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(1.5F);
		setResistance(10.0F);
		setUnlocalizedName(blockInfo.unlocalizedName);
	}

	/**
	 * Registers block in game and language registers
	 * @return object instance 
	 */
	public BlockCellCleaner register()
	{
		GameRegistry.registerBlock(this, blockInfo.internalName);
		GameRegistry.registerTileEntity(TileEntityCellCleaner.class, blockInfo.internalName + "TileEntity");
		LanguageRegistry.addName(this, blockInfo.name);
		
		return this;
	}


	/**
	 * Creates new tile entity for this block
	 * @param world - current world
	 * @return New tile entity
	 */
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityCellCleaner();
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  G U I  /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
//		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : onBlockActivated");
		
		if(!world.isRemote)
		{
			FMLNetworkHandler.openGui(entityPlayer, Charley.instance, 0, world, x, y, z);
		}
		
//		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
//		{
//			TileEntity te = world.getBlockTileEntity(x, y, z);
//			if(te != null && te instanceof TileEntityCellCleaner)
//			{
//				((TileEntityCellCleaner)te).updateClientEntity();
//			}
//		}
		
		return true;
	}

	
	
	public static void updateBlockTileEntity(boolean working, World currentWorld, int x, int y, int z)
	{
		TileEntity tileentity = currentWorld.getBlockTileEntity(x, y, z);
		
		currentWorld.setBlockTileEntity(x, y, z, tileentity);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//  T E X T U R E S   A N D   I C O N S  ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Registers all block's icons and textures
	 */
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
	{
        this.frontIcon = iconRegister.registerIcon(ModInfo.id + ":" + blockInfo.internalName + "Front");
        this.rearIcon = iconRegister.registerIcon(ModInfo.id + ":" + blockInfo.internalName + "Rear");
    }
	
	
    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
	@Override
	@SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side)
    {
		return side == this.getFacing(iBlockAccess, x, y, z) ? this.frontIcon : this.rearIcon;
//        return this.getIcon(side, iBlockAccess.getBlockMetadata(x, y, z));
    }
	
	
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
	@Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
		// By default front side faces to South
		return side == 3 ? this.frontIcon : this.rearIcon;
    }
	
	
//	/**
//     * Called whenever the block is added into the world. Args: world, x, y, z
//     */
//	public void onBlockAdded(World world, int x, int y, int z)
//    {
//        super.onBlockAdded(world, x, y, z);
//        this.setDefaultDirection(world, x, y, z);
//    }
	

    
    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack stackOfTheItems)
    {
    	int blockDirection = MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    	int facing = directionToFacing(blockDirection);
    	this.setFacing(world, x, y, z, facing);
    }

    
    public int getFacing(IBlockAccess iBlockAccess, int x, int y, int z)
    {
    	TileEntityCellCleaner te = (TileEntityCellCleaner)iBlockAccess.getBlockTileEntity(x, y, z);
    	if(te != null && te instanceof TileEntityCellCleaner)
    	{
    		return te.getFacing();
    	}
    	System.err.println("Failed to get block facing @ CellCleaner.getFacing");
    	return 3; // Returning default facing to South
    }
    
    public void setFacing(IBlockAccess iBlockAccess, int x, int y, int z, int facing)
    {
    	TileEntityCellCleaner te = (TileEntityCellCleaner)iBlockAccess.getBlockTileEntity(x, y, z);
    	if(te != null && te instanceof TileEntityCellCleaner)
    	{
    		te.setFacing(facing);
    		return;
    	}
    	System.err.println("Failed to set block facing @ CellCleaner.setFacing");
    }
    
    public static int directionToFacing(int direction)
    {
    	switch(direction)
    	{
    	case 0:
    		return 2;				// Facing to North
    	case 1:
    		return 5;				// Facing to East
    	case 2:
    		return 3;				// Facing to South
    	case 3:
    		return 4;				// Facing to West
    	default:
    		System.err.println("Wrong block facing @ CellCleaner.setDirection");
    		return 3;				// Default facing to South
    	}
    }
	
	/**
     * Set default direction of the block
     */
    @Deprecated
    private void setDefaultDirection(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            int north = world.getBlockId(x, y, z - 1);
            int south = world.getBlockId(x, y, z + 1);
            int west = world.getBlockId(x - 1, y, z);		
            int east = world.getBlockId(x + 1, y, z);
            byte metadata = 3;

            if (Block.opaqueCubeLookup[north] && !Block.opaqueCubeLookup[south])
            {
            	metadata = 3;
            }

            if (Block.opaqueCubeLookup[south] && !Block.opaqueCubeLookup[north])
            {
            	metadata = 2;
            }

            if (Block.opaqueCubeLookup[west] && !Block.opaqueCubeLookup[east])
            {
            	metadata = 5;
            }

            if (Block.opaqueCubeLookup[east] && !Block.opaqueCubeLookup[west])
            {
            	metadata = 4;
            }

            int flag = 2;	
            world.setBlockMetadataWithNotify(x, y, z, metadata, flag);
        }
    }
}
