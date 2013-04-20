package nexustools.handle;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.packet.Packet250CustomPayload;


@SideOnly(Side.CLIENT)
public class KeyboardClient extends Keyboard {
	public static KeyBinding hoverKey = new KeyBinding("Hover Key", 35); //H
	public static KeyBinding hoverDown = new KeyBinding("Hover Down", 42); //Ctrl
	private int lastKeyState = 0;
	
	public KeyboardClient() {
		KeyBindingRegistry.registerKeyBinding(new KeyHandle(new KeyBinding[] {hoverKey, hoverDown}));
	}
	
	public void sendKeyUpdate() throws Exception {
        int newState = (Minecraft.getMinecraft().gameSettings.keyBindForward.pressed ? 1 : 0) << 0 | (Minecraft.getMinecraft().gameSettings.keyBindJump.pressed ? 1 : 0) << 1 | (hoverKey.pressed ? 1 : 0) << 2 | (hoverDown.pressed ? 1 : 0) << 3;

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

class KeyHandle extends KeyHandler {
	public KeyHandle(KeyBinding[] keys) {
		super(keys);
	}

	@Override
	public String getLabel() {
		return "IC2Expanded";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}
}
