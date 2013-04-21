package nexustools.IC2Expanded.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IArmorTextureProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Based around IC2's Nano Suit and Lap Pack
 */
public class ItemArmorNanoSuitMk1 extends ItemArmorLap implements IArmorTextureProvider {
	public ItemArmorNanoSuitMk1(int id) {
		super(id, 1, 100000 + 300000);
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
		return "/nexustools/IC2Expanded/images/armor/nano_suit_mk1_1.png";
	}
}
