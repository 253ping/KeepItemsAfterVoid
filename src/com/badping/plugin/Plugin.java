package com.badping.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {

	private boolean instaDeath = true;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.instaDeath = getConfig().getBoolean("plugin.instadeath");
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onDamageTaken(EntityDamageEvent event) {
		if(event.getEntity().getType() != EntityType.PLAYER) return; 
		Player player = (Player) event.getEntity();
		if(event.getCause() == DamageCause.VOID) {
			if(instaDeath || (player.getHealth() - event.getFinalDamage()) <= 0) {
				event.setCancelled(true); 
				List<ItemStack> list = new ArrayList<>();
				PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, list, 0, null); // Fake Death Event
				FileConfiguration config = getConfig();
				
				getServer().getPluginManager().callEvent(deathEvent); // Emit Event
				player.setFallDistance(0); // Kein fallschaden
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // Spieler Leben auf 100% setzen
				player.teleport(new Location(player.getWorld(), config.getDouble("plugin.spawn.x"), // TP Spieler zu Spawn
										config.getDouble("plugin.spawn.y"),
										config.getDouble("plugin.spawn.z"),
									(float) config.getDouble("plugin.spawn.yaw"),
									(float) config.getDouble("plugin.spawn.pitch")));
				
			}
		}
	}
}
