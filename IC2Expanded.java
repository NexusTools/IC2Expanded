package IC2Expanded;

import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "IC2Expanded", name = "IC2 Expanded", version = "0.1")
@NetworkMod(clientSideRequired = true)
public class IC2Expanded {
	@PreInit
	public void preload(FMLPreInitializationEvent iEvent) {
		Configuration conf = new Configuration(iEvent.getSuggestedConfigurationFile());
		conf.load();
		conf.save();
	}
	
	@Init
	public void load(FMLInitializationEvent iEvent) {
		
	}
}
