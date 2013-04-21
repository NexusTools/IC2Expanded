package nexustools.IC2Expanded.handle;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

public class Keyboard {
	private Map<EntityPlayer, Boolean> forwardKeyDown = new HashMap<EntityPlayer, Boolean>();
	private Map<EntityPlayer, Boolean> jumpKeyDown = new HashMap<EntityPlayer, Boolean>();
	private Map<EntityPlayer, Boolean> hoverKeyDown = new HashMap<EntityPlayer, Boolean>();
	private Map<EntityPlayer, Boolean> hoverDownKeyDown = new HashMap<EntityPlayer, Boolean>();

	public boolean isForwardKeyDown(EntityPlayer p) {
		return this.forwardKeyDown.containsKey(p) ? this.forwardKeyDown.get(p) : false;
	}

	public boolean isJumpKeyDown(EntityPlayer p) {
		return this.jumpKeyDown.containsKey(p) ? this.jumpKeyDown.get(p) : false;
	}

	public boolean isHoverKeyDown(EntityPlayer p) {
		return this.hoverKeyDown.containsKey(p) ? this.hoverKeyDown.get(p) : false;
	}

	public boolean isHoverDownKeyDown(EntityPlayer p) {
		return this.hoverDownKeyDown.containsKey(p) ? this.hoverDownKeyDown.get(p) : false;
	}

	public void sendKeyUpdate() throws Exception {}

	public void processKeyUpdate(EntityPlayer p, int val) {
		this.forwardKeyDown.put(p, (val & 1) != 0);
		this.jumpKeyDown.put(p, (val & 2) != 0);
		this.hoverKeyDown.put(p, (val & 4) != 0);
		this.hoverDownKeyDown.put(p, (val & 8) != 0);
	}

	public void removePlayerReferences(EntityPlayer p) {
		forwardKeyDown.remove(p);
		jumpKeyDown.remove(p);
		hoverKeyDown.remove(p);
		hoverDownKeyDown.remove(p);
	}
}
