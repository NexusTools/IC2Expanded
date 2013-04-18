package IC2Expanded.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import ic2.api.IElectricItem;

public class QuantumSuitMk1 extends ItemArmor implements ISpecialArmor, IElectricItem {
	public QuantumSuitMk1(int id) {
		super(id, EnumArmorMaterial.DIAMOND, 0, 1);
		this.setItemName("Quantum Suit Mk1");
		this.setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public boolean canProvideEnergy() {
		return false;
	}

	@Override
	public int getChargedItemId() {
		return 0;
	}

	@Override
	public int getEmptyItemId() {
		return 0;
	}

	@Override
	public int getMaxCharge() {
		return 0;
	}

	@Override
	public int getTier() {
		return 0;
	}

	@Override
	public int getTransferLimit() {
		return 0;
	}

	@Override
	public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
		return null;
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return 0;
	}

	@Override
	public void damageArmor(EntityLiving entity, ItemStack stack,
			DamageSource source, int damage, int slot) {
		
	}
	
	public String getTextureFile()
    {
        return "/IC2Expanded/images/item.png";
    }
}
