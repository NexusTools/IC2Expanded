package nexustools.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IArmorTextureProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Based around IC2's Quantum Suit, Lap-pack and Jet-pack.
 */
public class ItemArmorQuantumSuitMk2 extends ItemArmorLapJet implements IArmorTextureProvider {
	public ItemArmorQuantumSuitMk2(int id) {
		super(id, 1, 1000000 + 300000 + 30000);
	}
	
	@Override
	public void onArmorTickUpdate(World w, EntityPlayer p, ItemStack itemStack) {
		p.extinguish();
		super.onArmorTickUpdate(w, p, itemStack);
	}
	
	@Override
	public int getTransferLimit() {
		return 1000;
	}
	
	public int getEnergyPerDamage() {
		return 900;
	}
	
	@Override
	public double getDamageAbsorptionRatio() {
		return 1.1;
	}
	
	@Override
	public int getTier() {
		return 3;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack var1) {
		return EnumRarity.rare;
	}
	
	@Override
	public String getArmorTextureFile(ItemStack itemstack) {
		return "/nexustools/images/armor/quantum_suit_mk2_1.png";
	}
}
