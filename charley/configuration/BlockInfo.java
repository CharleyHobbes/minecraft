package charley.configuration;

import net.minecraftforge.common.Configuration;

public class BlockInfo {

	
	public int id;
	public String internalName;
	public String name;
	public String unlocalizedName;
	
	public BlockInfo(int id, String internalName)
	{
		this.id = id;
		this.internalName = internalName;
		this.name = internalName;
		this.unlocalizedName = internalName;
	}
	
	public BlockInfo(int id, String internalName, String name)
	{
		this.id = id;
		this.internalName = internalName;
		this.name = name;
		this.unlocalizedName = name;
	}
	
	public BlockInfo(int id, String internalName, String name, String unlocalizedName)
	{
		this.id = id;
		this.internalName = internalName;
		this.name = name;
		this.unlocalizedName = unlocalizedName;
	}
	
	
	// Default ID's and names
	public static final BlockInfo cellCleaner = new BlockInfo(3100, "cellCleaner", "Cell Cleaner");
}
