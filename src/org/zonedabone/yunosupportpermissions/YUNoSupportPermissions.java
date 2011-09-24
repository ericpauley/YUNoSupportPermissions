package org.zonedabone.yunosupportpermissions;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class YUNoSupportPermissions extends JavaPlugin {

	Configuration config;
	Map<String,Object> perms;
	
	
	@Override
	public void onDisable() {
		System.out.println(this.getDescription().getName()+" version "+this.getDescription().getVersion()+" disabled!");

	}

	@Override
	public void onEnable() {
		System.out.println(this.getDescription().getName()+" version "+this.getDescription().getVersion()+" enabling...");
		config = this.getConfiguration();
		perms = config.getAll();
		if(perms.size()==0){
			config.setProperty("examplecommand", false);
			config.save();
		}
		perms = config.getAll();
		for(Entry<String, Object> entry:perms.entrySet()){
			PermissionDefault def;
			if(entry.getValue() instanceof String){
				String defString = (String) entry.getValue();
				if(defString.equalsIgnoreCase("true")){
					def = PermissionDefault.TRUE;
				}else if(defString.equalsIgnoreCase("false")){
					def = PermissionDefault.FALSE;
				}else if(defString.equalsIgnoreCase("op")){
					def = PermissionDefault.OP;
				}else if(defString.equalsIgnoreCase("notop")){
					def = PermissionDefault.NOT_OP;
				}else{
					continue;
				}
			}else if (entry.getValue() instanceof Boolean){
				if((Boolean) entry.getValue()){
					def = PermissionDefault.TRUE;
				}else{
					def = PermissionDefault.FALSE;
				}
			}else{
				continue;
			}
			this.getServer().getPluginManager().addPermission(new Permission(entry.getKey().toLowerCase(),def));
			System.out.println("Added permission: "+entry.getKey().toLowerCase()+" - "+def);
			this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, new PlayerListener(){
				@Override
				public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
					if(e.isCancelled())return;
					Player p = e.getPlayer();
					String perm = "";
					boolean result = true;
					for(String s:e.getMessage().split("\\W")){
						if(!perm.equals("")){
							perm = perm+".";
						}
						perm = perm+s.toLowerCase();
						Permission tocheck = getServer().getPluginManager().getPermission(perm);
						if(tocheck!=null){
							result = p.hasPermission(tocheck);
						}
					}
					if(!result){
						p.sendMessage(ChatColor.RED+"You don't have permission to do that!");
						e.setCancelled(true);
					}
				}
			}, Event.Priority.Normal, this);
		}
		System.out.println(this.getDescription().getName()+" version "+this.getDescription().getVersion()+" enabled!");
	}

}
