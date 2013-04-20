package nexustools.item;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ic2.api.ElectricItem;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import nexustools.IC2Expanded;

/*
 * Stub for Lappack and Jetpack based armors
 */

public class ItemArmorLapJet extends ItemArmorLap {
	public static boolean tryToFindAudioSource = true;
	public static boolean lastUseSuccessfull = false;

	public static Method ic2AudioManageCreateSourceMethod;
	public static Method ic2AudioSourcePlayMethod;
	public static Method ic2AudioSourceRemoveMethod;
	public static Method ic2AudioSourceUpdatePositionMethod;

	public static Object ic2AudioSourceInstance;
	public static Object ic2PositionSpecBackPackEnum;

	public static Object ic2AudioManager;
	public static Object ic2AudioManagerDefaultVolume;
	public static String soundEffectFileName = "Tools/Jetpack/JetpackLoop.ogg";

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
				if(hoverBobUp) {
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

		if(FMLCommonHandler.instance().getSide().isClient() && tryToFindAudioSource) {
			try {
				if(ic2AudioManageCreateSourceMethod == null) {
					Class<?> ic2audioSourceManager = Class.forName("ic2.core.audio.AudioManager");
					ic2AudioManageCreateSourceMethod = ic2audioSourceManager.getDeclaredMethod("createSource", Object.class, Class.forName("ic2.core.audio.PositionSpec"), String.class, boolean.class, boolean.class, float.class);
					Class<?> ic2PositionSpec = Class.forName("ic2.core.audio.PositionSpec");
					if(ic2PositionSpec == null)
						throw new Exception("Missing ic2.core.audio.PositionSpec");

					ic2PositionSpecBackPackEnum = null;
					for(Object constant : ic2PositionSpec.getEnumConstants())
						if(constant.toString().equals("Backpack")) {
							ic2PositionSpecBackPackEnum = constant;
							break;
						}

					if(ic2PositionSpecBackPackEnum == null)
						throw new Exception("ic2PositionSpecBackPackEnum not found.");

					Class<?> ic2MainClass = Class.forName("ic2.core.IC2");
					Field ic2AudioManagerField = ic2MainClass.getDeclaredField("audioManager");
					ic2AudioManager = ic2AudioManagerField.get(null);

					Field ic2AudoManagerDefaultVolumeField = ic2audioSourceManager.getDeclaredField("defaultVolume");
					ic2AudioManagerDefaultVolume = ic2AudoManagerDefaultVolumeField.get(ic2AudioManager);
				} else {
					if(lastUseSuccessfull != thrust) {
						if(thrust) {
							if(ic2AudioSourceInstance == null) {
								ic2AudioSourceInstance = ic2AudioManageCreateSourceMethod.invoke(ic2AudioManager, p, ic2PositionSpecBackPackEnum, soundEffectFileName, true, false, ic2AudioManagerDefaultVolume);
								Class<?> ic2AudioSource = ic2AudioSourceInstance.getClass();

								ic2AudioSourcePlayMethod = ic2AudioSource.getDeclaredMethod("play");
								ic2AudioSourceRemoveMethod = ic2AudioSource.getDeclaredMethod("remove");
								ic2AudioSourceUpdatePositionMethod = ic2AudioSource.getDeclaredMethod("updatePosition");
							}
							if(ic2AudioSourceInstance != null)
								ic2AudioSourcePlayMethod.invoke(ic2AudioSourceInstance);
						} else {
							if(ic2AudioSourceInstance != null)
								ic2AudioSourceRemoveMethod.invoke(ic2AudioSourceInstance);
							ic2AudioSourceInstance = null;
							ic2AudioSourcePlayMethod = null;
							ic2AudioSourceRemoveMethod = null;
							ic2AudioSourceUpdatePositionMethod = null;
						}
					}
					if(thrust && ic2AudioSourceInstance != null)
						ic2AudioSourceUpdatePositionMethod.invoke(ic2AudioSourceInstance);
					lastUseSuccessfull = thrust;
				}
			} catch(Throwable t) {
				t.printStackTrace();
				tryToFindAudioSource = false;
			}
		}
	}
}
