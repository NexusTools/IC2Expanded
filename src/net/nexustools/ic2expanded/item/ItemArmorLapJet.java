package nexustools.ic2expanded.item;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ic2.api.ElectricItem;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import nexustools.ic2expanded.IC2Expanded;
import nexustools.ic2expanded.IC2SoundEffectAdaptor;
import nexustools.ic2expanded.handle.Keyboard;

/*
 * Stub for Lappack and Jetpack based armors
 */

public class ItemArmorLapJet extends ItemArmorLap {
	
	IC2SoundEffectAdaptor soundEffect = new IC2SoundEffectAdaptor("Tools/Jetpack/JetpackLoop.ogg");

	public ItemArmorLapJet(int id, int armorSlot, int charge) {
		super(id, armorSlot, charge);
	}

	@Override
	public void onArmorTickUpdate(World w, EntityPlayer p, ItemStack itemStack) {

		NBTTagCompound NBTData = itemStack.getTagCompound();
		if(NBTData == null) {
			NBTData = new NBTTagCompound("tag");
			itemStack.setTagCompound(NBTData);
		}

		byte toggleTimeout = 0;
		boolean hoverEnabled = false;
		double targetHeight = 0;
		float hoverBob = 0;
		boolean hoverBobUp = false;
		try {
			toggleTimeout = NBTData.getByte("toggleTimeout");
			hoverEnabled = NBTData.getBoolean("hoverMode");
			hoverBob = NBTData.getFloat("hoverBob");
			hoverBobUp = NBTData.getBoolean("hoverBobUp");
			targetHeight = NBTData.getDouble("targetHeight");
		} catch(Exception e) {
			e.printStackTrace();
		}

		double groundY = Math.floor(p.posY - 1);
		if(toggleTimeout <= 0 && IC2Expanded.keyboard.isHoverKeyDown(p)) {
			hoverEnabled = !hoverEnabled;
			if(hoverEnabled) {
				targetHeight = groundY;
				hoverBobUp = false;
				hoverBob = 0f;

				NBTData.setDouble("targetHeight", targetHeight);
			}

			if(FMLCommonHandler.instance().getSide().isClient())
				p.addChatMessage(hoverEnabled ? "Hover Mode Enabled." : "Hover Mode Disabled.");

			toggleTimeout = 10;
			NBTData.setBoolean("hoverMode", hoverEnabled);
			NBTData.setByte("toggleTimeout", toggleTimeout);
		} else if(toggleTimeout > 0) {
			toggleTimeout--;
			NBTData.setByte("toggleTimeout", toggleTimeout);
		}

		boolean thrust;
		if(hoverEnabled) {
			if(p.isOnLadder() || p.isCollidedVertically || p.onGround) {
				NBTData.setDouble("targetHeight", groundY);
				NBTData.setBoolean("hoverBobUp", false);
				NBTData.setFloat("hoverBob", -1f);
				thrust = false;
			} else {
				if((hoverBobUp || IC2Expanded.keyboard.isJumpKeyDown(p)) && !IC2Expanded.keyboard.isHoverDownKeyDown(p)) {
					if(hoverBob < 0 && IC2Expanded.keyboard.isJumpKeyDown(p))
						hoverBob += 0.3;
					else
						hoverBob += 0.1;
				} else
					hoverBob -= 0.1;

				if(hoverBob < 0) {
					hoverBob = 0;
					hoverBobUp = true;
					NBTData.setBoolean("hoverBobUp", hoverBobUp);
				} else if(hoverBob > 0.3) {
					hoverBob = 0.3f;
					hoverBobUp = false;
					NBTData.setBoolean("hoverBobUp", hoverBobUp);
				}
				NBTData.setFloat("hoverBob", hoverBob);

				double yMovement = (p.motionY < 0.15 && p.motionY > -0.15) ? (p.motionY > 0 ? -0.15 : 0.15) : p.motionY;
				double effectiveY = p.posY + yMovement + hoverBob + 0.5;

				if(IC2Expanded.keyboard.isJumpKeyDown(p))
					targetHeight = targetHeight + 0.8;
				else if(IC2Expanded.keyboard.isHoverDownKeyDown(p))
					targetHeight = targetHeight - 0.8;

				targetHeight = Math.max(p.posY - 3, Math.min(p.posY + 8, targetHeight));
				NBTData.setDouble("targetHeight", targetHeight);
				thrust = targetHeight > effectiveY;
			}

			// System.out.println(FMLCommonHandler.instance().getSide().name() + ": " + targetHeight + " - " + hoverBob + " - " + (hoverBobUp ? "On" : "Off"));
		} else
			thrust = IC2Expanded.keyboard.isJumpKeyDown(p);

		if(thrust) {
			ItemStack itemEquipped = p.inventory.armorInventory[2];

			if(ElectricItem.canUse(itemEquipped, 9)) {
				float adjustmentY = 0.7F;
				double newPosY = p.posY;

				if(newPosY > (p.worldObj.getHeight() - 25)) {
					if(newPosY > p.worldObj.getHeight())
						newPosY = p.worldObj.getHeight();

					adjustmentY = (float) (adjustmentY * ((p.worldObj.getHeight() - newPosY) / 25.0D));
				}

				p.motionY = Math.min(p.motionY + (adjustmentY * 0.2F), 0.6);

				p.fallDistance = 0;
				p.distanceWalkedModified = 0;
				ElectricItem.use(itemEquipped, 9, p);
				if(p instanceof EntityPlayerMP)
					((EntityPlayerMP) p).playerNetServerHandler.ticksForFloatKick = 0;
			}
			p.inventoryContainer.detectAndSendChanges();
		}

		if(FMLCommonHandler.instance().getSide().isClient())
			soundEffect.tick(thrust, p);
		
	}
}
