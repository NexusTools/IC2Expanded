package nexustools.item;

import ic2.api.ElectricItem;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.world.World;
import nexustools.IC2Expanded;

/*
 * Stub for Lappack and Jetpack based armors
 */

public class ItemArmorLapJet extends ItemArmorLap {
	private Map<EntityPlayer, Boolean> hoverKeyDown = new HashMap<EntityPlayer, Boolean>();

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

		int targetHeight = NBTData.getInteger("targetHeight");
		boolean hoverEnabled = NBTData.getBoolean("hoverMode");
		boolean hoverKey = hoverKeyDown.containsKey(p) ? hoverKeyDown.get(p) : false;
		if(hoverKey != IC2Expanded.keyboard.isHoverKeyDown(p)) {
			hoverKey = IC2Expanded.keyboard.isHoverKeyDown(p);
			if(hoverKey) {
				hoverEnabled = !hoverEnabled;
				if(hoverEnabled) {
					targetHeight = (int) p.posY;
					NBTData.setInteger("targetHeight", targetHeight);
				}

				String message = hoverEnabled ? "Hover Mode Enabled." : "Hover Mode Disabled.";
				if(p instanceof EntityPlayerMP)
					((EntityPlayerMP) p).playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(message));
				else
					p.addChatMessage(message);

				NBTData.setBoolean("hoverMode", hoverEnabled);
			}

			hoverKeyDown.put(p, hoverKey);
		}

		boolean thrust;
		if(hoverEnabled) {
			int newTargetHeight = targetHeight;
			if(p.isOnLadder() || p.isCollidedVertically)
				newTargetHeight = (int) p.posY;
			else if(IC2Expanded.keyboard.isJumpKeyDown(p))
				newTargetHeight += 1;
			else if(IC2Expanded.keyboard.isHoverDownKeyDown(p))
				newTargetHeight -= 1;

			if(targetHeight != newTargetHeight) {
				targetHeight = newTargetHeight;
				NBTData.setInteger("targetHeight", targetHeight);
				NBTData.setBoolean("hoverMode", true);

				String message = "Hover Target set to: " + targetHeight;
				if(p instanceof EntityPlayerMP)
					((EntityPlayerMP) p).playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(message));
				else
					p.addChatMessage(message);
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
					if(newPosY > p.worldObj.getHeight())
						newPosY = p.worldObj.getHeight();

					adjustmentY = (float) (adjustmentY * ((p.worldObj.getHeight() - newPosY) / 25.0D));
				}

				double motionY = p.motionY;
				p.motionY = Math.min(p.motionY + (adjustmentY * 0.2F), 0.6);

				// TODO: Play jetpack sound?

				p.fallDistance = 0;
				p.distanceWalkedModified = 0;
				ElectricItem.use(itemEquipped, 9, p);
				if(p instanceof EntityPlayerMP)
					((EntityPlayerMP) p).playerNetServerHandler.ticksForFloatKick = 0;
			}
			p.inventoryContainer.detectAndSendChanges();
		}
	}
}
