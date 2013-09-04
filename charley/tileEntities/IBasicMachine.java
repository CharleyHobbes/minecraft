package charley.tileEntities;

import net.minecraft.item.ItemStack;

public interface IBasicMachine {
	public int getWorkTicks();
	public int getWorkTotal();
	public float getWorkProgress();
	public float getWorkProgressScaled(float scale);
	public boolean isWorking();
	public boolean canProcess(ItemStack item);
}
