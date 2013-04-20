package nexustools.item;

import ic2.api.ElectricItem;
import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import ic2.api.Items;
import ic2.core.util.StackUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.ISpecialArmor;
import nexustools.IC2Expanded;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Based around IC2's Nano Suit, Lap-pack and Jet-pack.
 */
public class ItemArmorNanoSuitMk2 extends ItemArmor implements IArmorTextureProvider, ISpecialArmor, IElectricItem, IMetalArmor {
	private Map<EntityPlayer, Boolean> hoverKeyDown = new HashMap<EntityPlayer, Boolean>();
	
	public ItemArmorNanoSuitMk2(int id) {
		super(id, EnumArmorMaterial.DIAMOND, 1, 1);
		this.setMaxDamage(27);
	}
	
	@Override
	public void onArmorTickUpdate(World w, EntityPlayer p, ItemStack itemStack) {
		NBTTagCompound NBTData = itemStack.getTagCompound();
		if(NBTData == null)
		{
			NBTData = new NBTTagCompound("tag");
			itemStack.setTagCompound(NBTData);
		}
		
		int targetHeight = NBTData.getInteger("targetHeight");
		boolean hoverEnabled = NBTData.getBoolean("hoverMode");
		boolean hoverKey = hoverKeyDown.containsKey(p) ? hoverKeyDown.get(p) : false;
		if(hoverKey != IC2Expanded.keyboard.isHoverKeyDown(p)) {
			hoverKey = IC2Expanded.keyboard.isHoverKeyDown(p);
			if(hoverKey) {
				hoverEnabled = !hoverEnabled;
				if(hoverEnabled) {
					targetHeight = (int)p.posY;
					NBTData.setInteger("targetHeight", targetHeight);
				}
				
				String message = hoverEnabled ? "Hover Mode Enabled." : "Hover Mode Disabled.";
				if (p instanceof EntityPlayerMP)
		        {
		            ((EntityPlayerMP)p).playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(message));
		        }
		        else
		        {
		            p.addChatMessage(message);
		        }
				
				NBTData.setBoolean("hoverMode", hoverEnabled);
			}
			
			hoverKeyDown.put(p, hoverKey);
		}
		
		boolean thrust;
		if(hoverEnabled) {
			int newTargetHeight = targetHeight;
			if(p.isOnLadder() || p.isCollidedVertically) {
				newTargetHeight = (int)p.posY;
			} else if(IC2Expanded.keyboard.isJumpKeyDown(p) || IC2Expanded.keyboard.isHoverUpKeyDown(p)) {
				newTargetHeight += 1;
			} else if(IC2Expanded.keyboard.isHoverDownKeyDown(p)) {
				newTargetHeight -= 1;
			}
			
			if(targetHeight != newTargetHeight) {
				targetHeight = newTargetHeight;
				NBTData.setInteger("targetHeight", targetHeight);
				NBTData.setBoolean("hoverMode", true);
				
				String message = "Hover Target set to: " + targetHeight;
				if (p instanceof EntityPlayerMP)
		        {
		            ((EntityPlayerMP)p).playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(message));
		        }
		        else
		        {
		            p.addChatMessage(message);
		        }
			}
			
			thrust = !p.isOnLadder() && targetHeight > p.posY;
		} else
			thrust = IC2Expanded.keyboard.isJumpKeyDown(p);
		
		if(thrust) {
			ItemStack itemEquipped = p.inventory.armorInventory[2];
			
			if(ElectricItem.canUse(itemEquipped, 9)) {
				float adjustmentY = 0.7F;
				
				int worldHeight = p.worldObj.getHeight();
				double newPosY = p.posY;
				
				if(newPosY > (p.worldObj.getHeight() - 25)) {
					if(newPosY > p.worldObj.getHeight()) {
						newPosY = p.worldObj.getHeight();
					}
					
					adjustmentY = (float) (adjustmentY * ((p.worldObj.getHeight() - newPosY) / 25.0D));
				}
				
				double motionY = p.motionY;
				p.motionY = Math.min(p.motionY + (adjustmentY * 0.2F), 0.6);
				
				// TODO: Play jetpack sound?
				
				p.fallDistance = 0;
				p.distanceWalkedModified = 0;
				ElectricItem.use(itemEquipped, 9, p);
				if(p instanceof EntityPlayerMP) {
					((EntityPlayerMP) p).playerNetServerHandler.ticksForFloatKick = 0;
				}
			}
			p.inventoryContainer.detectAndSendChanges();
		}
	}
	
	@Override
	public ArmorProperties getProperties(EntityLiving player, ItemStack armor, DamageSource source, double damage, int slot) {
		double var7 = getDamageAbsorptionRatio() * getBaseAbsorptionRatio();
		int var10 = getEnergyPerDamage() > 0 ? 25 * ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / getEnergyPerDamage() : 0;
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
		return 100000 + 300000 + 30000;
	}
	
	@Override
	public int getTransferLimit() {
		return 160;
	}
	
	public int getEnergyPerDamage() {
		return 800;
	}
	
	public double getBaseAbsorptionRatio() {
		return 0.4;
	}
	
	public double getDamageAbsorptionRatio() {
		return 0.9;
	}
	
	@Override
	public int getTier() {
		return 2;
	}
	
	@Override
	public void getSubItems(int stub, CreativeTabs tab, List list) {
		ItemStack chargedVariant = new ItemStack(this, 1);
		ElectricItem.charge(chargedVariant, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		list.add(chargedVariant);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack var1) {
		return EnumRarity.uncommon;
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
		return "/nexustools/images/armor/nano_suit_mk2_1.png";
	}
}
