package charley.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import charley.configuration.ModInfo;
import charley.tileEntities.TileEntityCellCleaner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

	public static final byte requestUpdateTileEntity = 1;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
	{

//		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : onPacketData");
		
		if(packet.channel.equals(ModInfo.channel))
		{
//			System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : correct channel");
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if(side == Side.CLIENT) {
//				System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : Client side");

				System.err.println(manager);
				System.err.println(packet);
				System.err.println(player);
				
				handlePacketClient(manager, packet, (EntityClientPlayerMP)player);
			} else if(side == Side.SERVER) {
//				System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : Server side");
				handlePacketServer(manager, packet, (EntityPlayerMP)player);
			} else {
				System.err.println("Unknown side in packet handler: " + side);
			}
		}
	}

	
	public void handlePacketServer(INetworkManager manager, Packet250CustomPayload packet, EntityPlayerMP player)
	{
//		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : handlePacketServer");
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		byte type;
		try {
            type = inputStream.readByte();

			switch(type)
			{
			case requestUpdateTileEntity:
				{
					int x =  inputStream.readInt();
					int y =  inputStream.readInt();
					int z =  inputStream.readInt();
					TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
					if(te != null && te instanceof TileEntityCellCleaner)
					{
						Packet response = ((TileEntityCellCleaner)te).getDescriptionPacket();
						PacketDispatcher.sendPacketToPlayer(response, (Player)player);
					}
				}
				break;
			}
			
	    } catch (IOException e) {
            e.printStackTrace();
            return;
	    }
	}
	
	public void handlePacketClient(INetworkManager manager, Packet250CustomPayload packet, EntityClientPlayerMP player)
	{
//		System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : handlePacketClient");
		
	}
	
	public void handleRandomPacket(Packet250CustomPayload packet)
	{
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        
        int randomInt1;
        int randomInt2;
       
        try {
                randomInt1 = inputStream.readInt();
                randomInt2 = inputStream.readInt();
        } catch (IOException e) {
                e.printStackTrace();
                return;
        }
       
//        System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " : " + randomInt1 + " - " + randomInt2);
	}
	
	public Packet250CustomPayload makeRandomPacket()
	{
		Random random = new Random();
        int randomInt1 = random.nextInt();
        int randomInt2 = random.nextInt();
       
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
                outputStream.writeInt(randomInt1);
                outputStream.writeInt(randomInt2);
        } catch (Exception ex) {
                ex.printStackTrace();
        }
       
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = ModInfo.channel;
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        
        return packet;
	}

}
