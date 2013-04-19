package steve4448.handle;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.Packet250CustomPayload;


public class KeyboardClient extends Keyboard {
	private int lastKeyState = 0;
	public void sendKeyUpdate() throws Exception {
        int newState = (Minecraft.getMinecraft().gameSettings.keyBindForward.pressed ? 1 : 0) | (Minecraft.getMinecraft().gameSettings.keyBindJump.pressed ? 1 : 0) << 1;

        if (newState != lastKeyState) {
        	ByteArrayOutputStream bOS = new ByteArrayOutputStream(8);
        	DataOutputStream outputStream = new DataOutputStream(bOS);
        	outputStream.writeInt(newState);
        	
        	Packet250CustomPayload packet = new Packet250CustomPayload();
        	packet.channel = "IC2Expanded";
        	packet.data = bOS.toByteArray();
        	packet.length = bOS.size();
        	
        	PacketDispatcher.sendPacketToServer(packet);
        	
        	super.processKeyUpdate(Minecraft.getMinecraft().thePlayer, newState);
            this.lastKeyState = newState;
        }
    }
}
