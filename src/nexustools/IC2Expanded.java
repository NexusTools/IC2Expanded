package nexustools;

import ic2.api.Ic2Recipes;
import ic2.api.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import nexustools.handle.Keyboard;
import nexustools.handle.PacketHandler;
import nexustools.handle.TickHandler;
import nexustools.item.ItemArmorNanoSuitMk1;
import nexustools.item.ItemArmorNanoSuitMk2;
import nexustools.item.ItemArmorQuantumSuitMk1;
import nexustools.item.ItemArmorQuantumSuitMk2;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "IC2Expanded", name = "IC2 Expanded", version = "0.2.6", dependencies = "required-after:IC2")
@NetworkMod(clientSideRequired = true, channels={"IC2Expanded"}, packetHandler = PacketHandler.class)
public class IC2Expanded {
	public static int nanoSuitMk1ID, nanoSuitMk2ID;
	public static Item nanoSuitMk1, nanoSuitMk2;
	
	public static int quantumSuitMk1ID, quantumSuitMk2ID;
	public static Item quantumSuitMk1, quantumSuitMk2;
	
	@SidedProxy(clientSide = "steve4448.handle.KeyboardClient", serverSide = "steve4448.handle.Keyboard")
    public static Keyboard keyboard;
	
	@PreInit
	public void preload(FMLPreInitializationEvent iEvent) {
		if(FMLCommonHandler.instance().getSide().isClient()) {
			MinecraftForgeClient.preloadTexture("/nexustools/images/item/item.png");
			MinecraftForgeClient.preloadTexture("/nexustools/images/armor/nano_suit_mk1_1.png");
			MinecraftForgeClient.preloadTexture("/nexustools/images/armor/nano_suit_mk2_1.png");
			MinecraftForgeClient.preloadTexture("/nexustools/images/armor/quantum_suit_mk1_1.png");
			MinecraftForgeClient.preloadTexture("/nexustools/images/armor/quantum_suit_mk2_1.png");
		}
		Configuration conf = new Configuration(iEvent.getSuggestedConfigurationFile());
		conf.load();
		nanoSuitMk1ID = conf.getItem("nanoSuitMk1ID", 7000).getInt();
		nanoSuitMk2ID = conf.getItem("nanoSuitMk2ID", 7001).getInt();
		quantumSuitMk1ID = conf.getItem("quantumSuitMk1ID", 7002).getInt();
		quantumSuitMk2ID = conf.getItem("quantumSuitMk2ID", 7003).getInt();
		conf.save();
	}
	
	@Init
	public void load(FMLInitializationEvent iEvent) {
		// TODO: Base textures around what we can find, merge them together, and save them to a temporary directory, would also have to hook into when a texture pack is changed.
		// TODO: Allow use of Mk1/Mk2 nano suit in creation of Quantum Suit.
		
		nanoSuitMk1 = new ItemArmorNanoSuitMk1(nanoSuitMk1ID).setItemName("nanoSuitMk1").setIconIndex(0);
		nanoSuitMk2 = new ItemArmorNanoSuitMk2(nanoSuitMk2ID).setItemName("nanoSuitMk2").setIconIndex(1);
		
		quantumSuitMk1 = new ItemArmorQuantumSuitMk1(quantumSuitMk1ID).setItemName("quantumSuitMk1").setIconIndex(2);
		quantumSuitMk2 = new ItemArmorQuantumSuitMk2(quantumSuitMk2ID).setItemName("quantumSuitMk2").setIconIndex(3);
		
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(nanoSuitMk1), Items.getItem("nanoBodyarmor"), Items.getItem("lapPack"));
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(nanoSuitMk2), Items.getItem("nanoBodyarmor"), Items.getItem("lapPack"), Items.getItem("electricJetpack"));
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(nanoSuitMk2), nanoSuitMk1, Items.getItem("electricJetpack"));

		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(quantumSuitMk1), Items.getItem("quantumBodyarmor"), Items.getItem("lapPack"));
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(quantumSuitMk2), Items.getItem("quantumBodyarmor"), Items.getItem("lapPack"), Items.getItem("electricJetpack"));
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(quantumSuitMk2), quantumSuitMk1, Items.getItem("electricJetpack"));
		Ic2Recipes.addCraftingRecipe(new ItemStack(quantumSuitMk1), "AnA", "ILI", "IAI", 'n', nanoSuitMk1, 'I', Items.getItem("iridiumPlate"), 'L', Items.getItem("lapotronCrystal"), 'A', Items.getItem("advancedAlloy"));
		Ic2Recipes.addCraftingRecipe(new ItemStack(quantumSuitMk2), "AnA", "ILI", "IAI", 'n', nanoSuitMk2, 'I', Items.getItem("iridiumPlate"), 'L', Items.getItem("lapotronCrystal"), 'A', Items.getItem("advancedAlloy"));
		
		LanguageRegistry.addName(nanoSuitMk1, "NanoSuit Bodyarmor Mk1");
		LanguageRegistry.addName(nanoSuitMk2, "NanoSuit Bodyarmor Mk2");
		
		LanguageRegistry.addName(quantumSuitMk1, "QuantumSuit Bodyarmor Mk1");
		LanguageRegistry.addName(quantumSuitMk2, "QuantumSuit Bodyarmor Mk2");
		
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
	}
	
	public void onPlayerLogout(EntityPlayer p) {
		if(FMLCommonHandler.instance().getSide().isServer())
			keyboard.removePlayerReferences(p);
    }
}
