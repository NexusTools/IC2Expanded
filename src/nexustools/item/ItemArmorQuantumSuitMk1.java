package nexustools.item;

import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Based around IC2's Quantum Suit and Lap Pack
 */
public class ItemArmorQuantumSuitMk1 extends ItemArmor implements IArmorTextureProvider, ISpecialArmor, IElectricItem, IMetalArmor {
	public ItemArmorQuantumSuitMk1(int id) {
		super(id, EnumArmorMaterial.DIAMOND, 0, 1);
		this.setMaxDamage(27);
	}
	
	@Override
	public void onArmorTickUpdate(World w, EntityPlayer p, ItemStack itemStack) {
		p.extinguish();
	}
	
	@Override
	public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
		double var7 = getDamageAbsorptionRatio() * getBaseAbsorptionRatio();
		int var9 = getEnergyPerDamage();
		int var10 = var9 > 0 ? 25 * ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / var9 : 0;
		return new ISpecialArmor.ArmorProperties(0, var7, var10);
	}
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) >= getEnergyPerDamage() ? (int) Math.round(20.0D * getDamageAbsorptionRatio() * getBaseAbsorptionRatio()) : 0;
	}
	
	@Override
	public void damageArmor(EntityLiving entity, ItemStack stack, DamageSource source, int damage, int slot) {
		ElectricItem.discharge(stack, damage * getEnergyPerDamage(), Integer.MAX_VALUE, true, false);
	}
	
	@Override
	public boolean canProvideEnergy() {
		return true;
	}
	
	@Override
	public int getEmptyItemId() {
		return itemID;
	}
	
	@Override
	public int getChargedItemId() {
		return itemID;
	}
	
	@Override
	public int getMaxCharge() {
		return 1000000 + 300000;
	}
	
	@Override
	public int getTransferLimit() {
		return 1000;
	}
	
	public int getEnergyPerDamage() {
		return 900;
	}
	
	public double getBaseAbsorptionRatio() {
		return 0.4;
	}
	
	public double getDamageAbsorptionRatio() {
		return 1.1;
	}
	
	@Override
	public int getTier() {
		return 3;
	}
	
	@Override
	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		var3.add(var4);
		var3.add(new ItemStack(this, 1, this.getMaxDamage()));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack var1) {
		return EnumRarity.rare;
	}
	
	@Override
	public boolean isMetalArmor(ItemStack var1, EntityPlayer var2) {
		return true;
	}
	
	@Override
	public int getItemEnchantability() {
		return 0;
	}
	
	@Override
	public boolean isRepairable() {
		return false;
	}
	
	@Override
	public String getTextureFile() {
		return "/nexustools/images/item/item.png";
	}
	
	@Override
	public String getArmorTextureFile(ItemStack itemstack) {
		return "/nexustools/images/armor/quantum_suit_mk1_1.png";
	}
}
