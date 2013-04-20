package nexustools.handle;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

public class Keyboard {
	private Map<EntityPlayer, Boolean> forwardKeyDown = new HashMap<EntityPlayer, Boolean>();
	private Map<EntityPlayer, Boolean> jumpKeyDown = new HashMap<EntityPlayer, Boolean>();
	
	public boolean isJumpKeyDown(EntityPlayer p) {
		return this.jumpKeyDown.containsKey(p) ? this.jumpKeyDown.get(p) : false;
	}
	
	public void sendKeyUpdate() throws Exception {}
	
	public void processKeyUpdate(EntityPlayer p, int val) {
		this.forwardKeyDown.put(p, (val & 1) != 0);
		this.jumpKeyDown.put(p, (val & 2) != 0);
	}
	
	public void removePlayerReferences(EntityPlayer p) {
		forwardKeyDown.remove(p);
		jumpKeyDown.remove(p);
	}
}
