package net.u1f401.amogus.event;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.u1f401.amogus.Amogus;

public class KeyInputHandler{
	public static class AmogusStand extends ArmorStandEntity{
		public AmogusStand(World world, double x, double y, double z){
			super(world, x, y, z);
		}
		public AmogusStand(EntityType<? extends ArmorStandEntity> entityType, World world){
			super(entityType, world);
		}
		@Override
		public boolean isGlowing() {
			return true;
		}
		public void setGlowing(){
			super.setGlowing(true);
			this.setFlag(GLOWING_FLAG_INDEX, true);
		}
		@Override
		public int getTeamColorValue(){
			return 0xbf1f00;
		}
		@Override
		public boolean handleAttack(Entity attacker){
			if(!(attacker instanceof AbstractClientPlayerEntity)) return false;
			AbstractClientPlayerEntity target = null;
			for(AbstractClientPlayerEntity player : markers.keySet()) if(markers.get(player) == this){
				return ((AbstractClientPlayerEntity)attacker).tryAttack(target);
			}
			return false;
		}
	}
	public static KeyBinding markerkey;
	public static KeyBinding gammakey;
	public static KeyBinding statskey;
	public static boolean gammaboost = true;
	public static boolean glowing = true;
	public static HashMap<AbstractClientPlayerEntity, AmogusStand> markers = new HashMap<>();
	public static void registerKeyInputs(){
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(markerkey.wasPressed()){
				glowing = !glowing;
				client.inGameHud.getChatHud().addMessage(Text.literal("§4§l[\u0d9e]:§r Migrator glowing: " + glowing));
				if(!glowing){
					for(AmogusStand marker : markers.values()) marker.remove(RemovalReason.DISCARDED);
					markers.clear();
				}
			}
			if(gammakey.wasPressed()){
				gammaboost = !gammaboost;
				client.inGameHud.getChatHud().addMessage(Text.literal("§4§l[\u0d9e]:§r Gamma Multiplier: " + (gammaboost ? 5 : 1)));
				Amogus.LOGGER.info("Set Gamma boost to " + gammaboost);
			}
			if(statskey.wasPressed()) for(AbstractClientPlayerEntity migrator : markers.keySet()){
				int protection = 0;
				for(ItemStack armoritem : migrator.getInventory().armor) for(NbtElement enchant : armoritem.getEnchantments())
					if(((NbtCompound)enchant).getString("id").equals("minecraft:protection")) protection += ((NbtCompound)enchant).getInt("lvl");
				client.inGameHud.getChatHud().addMessage(Text.literal("§4§l[\u0d9e]:§c ")
						.append(migrator.getDisplayName()).append(" §9" + migrator.getBlockPos().toShortString())
						.append(" §7🛡" + migrator.getArmor() + "§5" + protection));
			}
//			client.interactionManager.attackEntity(client.player, client.player);
//			client.gameRenderer.updateTargetedEntity(0); has reach hacks potential
		});
		ClientTickEvents.START_WORLD_TICK.register(world -> {
			if(glowing) for(Entity entity : world.getEntities()) if(entity instanceof AbstractClientPlayerEntity){
				AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)entity;
				if(player.isPartVisible(PlayerModelPart.CAPE)){
					if(player.getCapeTexture() != null){
						if(player.getCapeTexture().toString().equals("minecraft:capes/17f76a23ff4d227a94ea3d5802dccae9f2ae9aa9")){
							if(!markers.containsKey(player)){
								Amogus.LOGGER.info("Registered " + player.getName().getString() + " as Migrator");
								AmogusStand newmarker = new AmogusStand(world, player.getX(), player.getY(), player.getZ());
								newmarker.setGlowing();
								markers.put(player, newmarker);
								world.addEntity((int)player.getUuid().getMostSignificantBits(), newmarker);
							}
						}
					}
				}
			}
			Object[] oldmarkers = markers.keySet().toArray();
			for(Object object: oldmarkers){
				AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)object;
				if(!world.getPlayers().contains(player)){
					Amogus.LOGGER.info(player.getName().getString() + " left!");
					markers.get(player).remove(RemovalReason.DISCARDED);
					world.removeEntity((int)player.getUuid().getMostSignificantBits(), RemovalReason.DISCARDED);
					markers.remove(player);
				}
				else markers.get(player).setPos(player.getX(), player.getY(), player.getZ());
			}
		});
	}
	public static void register(){
		markerkey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.toggleglow", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.amogus"));
		gammakey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.gammaboost", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.amogus"));
		statskey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.amogus.showstats", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.categories.amogus"));
		registerKeyInputs();
	}
}