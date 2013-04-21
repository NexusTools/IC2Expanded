package nexustools.IC2Expanded.handle;

import java.util.EnumSet;

import nexustools.IC2Expanded.IC2Expanded;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.CLIENT))
			try {
				IC2Expanded.keyboard.sendKeyUpdate();
			} catch(Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "IC2Expanded";
	}
}
