package nexustools.ic2expanded.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IArmorTextureProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Based around IC2's Nano Suit, Lap-pack and Jet-pack.
 */
public class ItemArmorNanoSuitMk2 extends ItemArmorLapJet implements IArmorTextureProvider {
	public ItemArmorNanoSuitMk2(int id) {
		super(id, 1, 100000 + 300000 + 30000);
	}

	@Override
	public int getTransferLimit() {
		return 160;
	}

	@Override
	public int getEnergyPerDamage() {
		return 800;
	}

	@Override
	public int getTier() {
		return 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack var1) {
		return EnumRarity.uncommon;
	}

	@Override
	public String getArmorTextureFile(ItemStack itemstack) {
		return "/nexustools/ic2expanded/images/armor/nano_suit_mk2_1.png";
	}
}
