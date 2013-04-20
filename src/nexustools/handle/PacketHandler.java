package nexustools.handle;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import nexustools.IC2Expanded;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
		try {
			if(FMLCommonHandler.instance().getSide().isServer())
				if(packet.channel.equals("IC2Expanded")) {
					DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
					IC2Expanded.keyboard.processKeyUpdate((EntityPlayerMP)playerEntity, inputStream.readInt());
				}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
