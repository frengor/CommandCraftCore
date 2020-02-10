//  MIT License
//  
//  Copyright (c) 2019 fren_gor
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package com.fren_gor.commandCraftCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.commandCraftCore.commands.CommandManager;
import com.fren_gor.commandCraftCore.commands.NewCommand;
import com.fren_gor.commandCraftCore.commands.commands.AdvancedClone;
import com.fren_gor.commandCraftCore.commands.commands.AdvancedSetBlock;
import com.fren_gor.commandCraftCore.commands.commands.AdvancedTestforblocks;
import com.fren_gor.commandCraftCore.commands.commands.Cmc;
import com.fren_gor.commandCraftCore.commands.commands.PluginMessagingChannel;
import com.fren_gor.commandCraftCore.commands.commands.RegisterPluginMessagingChannel;
import com.fren_gor.commandCraftCore.commands.commands.TestForPermissions;
import com.fren_gor.commandCraftCore.commands.commands.VarHelp;
import com.fren_gor.commandCraftCore.commands.commands.WorldTp;
import com.fren_gor.commandCraftCore.events.EventManager;
import com.fren_gor.commandCraftCore.events.NewEvent;
import com.fren_gor.commandCraftCore.events.RegisterVarTypesEvent;
import com.fren_gor.commandCraftCore.events.RegisterVariablesEvent;
import com.fren_gor.commandCraftCore.events.events.OnDisableEvent;
import com.fren_gor.commandCraftCore.events.events.OnEnableEvent;
import com.fren_gor.commandCraftCore.loops.LoopManager;
import com.fren_gor.commandCraftCore.loops.NewLoop;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleObject;
import com.fren_gor.commandCraftCore.utils.saveUtils.SavingObject;
import com.fren_gor.commandCraftCore.vars.StaticActions;

import lombok.Getter;

public class CommandCraftCore extends JavaPlugin implements Listener {

	private static CommandCraftCore instance;
	private static CommandManager commandManager;
	private static EventManager eventManager;
	private static LoopManager loopManager;
	private int reloadCount;
	private static Object craftServer;
	private static File SAVES_FOLDER;

	public static File getSavesFolder() {
		return SAVES_FOLDER;
	}

	static {
		craftServer = ReflectionUtil.cast(Bukkit.getServer(), ReflectionUtil.getCBClass("CraftServer"));
	}

	@Getter
	private static CommandMap commandMap;
	@Getter
	private static Map<String, Command> knownCommands;

	public static LoopManager getLoopManager() {
		return loopManager;
	}

	public static CommandCraftCore getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		instance = this;

		commandMap = (CommandMap) ReflectionUtil.getField(Bukkit.getServer(), "commandMap");
		knownCommands = ((Map<String, Command>) ReflectionUtil.getField(commandMap, "knownCommands"));
		commandManager = new CommandManager();
		eventManager = new EventManager();
		loopManager = new LoopManager();

		reloadCount = (int) ReflectionUtil.getField(craftServer, "reloadCount");

		Bukkit.getPluginManager().registerEvents(this, this);

		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}

		SAVES_FOLDER = new File(getDataFolder(), "saves");

		if (SAVES_FOLDER.exists()) {

			SavingObject<Map<String, String>> s = new SavingObject<>(new File(SAVES_FOLDER, "savedVars"));

			StaticActions.writtenVariables = s.getObject();

		} else
			SAVES_FOLDER.mkdirs();

		if (!new File(getDataFolder(), "config.yml").exists()) {
			getConfig().options().copyDefaults(true);
			try {
				getConfig().save(new File(getDataFolder(), "config.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			getConfig().load(new File(getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}

		ConfigManager.debugMode = getConfig().getBoolean("debug-mode");
		ConfigManager.viewReadingTime = getConfig().getBoolean("view-reading-time");
		ConfigManager.viewExecutingTime = getConfig().getBoolean("view-executing-time");
		ConfigManager.ignoreLops = instance.getConfig().getBoolean("ignore-loops");
		ConfigManager.createFolders = instance.getConfig().getBoolean("create-folders");
		ConfigManager.setDisabledScripts(getConfig().getStringList("disabled-scripts"));

		if (ConfigManager.getDisabledScripts().size() > 0) {
			System.out.println("Disabled Scripts:");
			for (String s : ConfigManager.getDisabledScripts()) {
				System.out.println(s);
			}
		}

		if (!new File(getDataFolder(), "commands").exists()) {
			new File(getDataFolder(), "commands").mkdir();
		}
		if (!new File(getDataFolder(), "events").exists()) {
			new File(getDataFolder(), "events").mkdir();
		}
		if (!new File(getDataFolder(), "loops").exists()) {
			new File(getDataFolder(), "loops").mkdir();
		}

		for (File f : getDataFolder().listFiles()) {
			loadDir(f);
		}

		new EventVarsRegisterListener();

		new BukkitRunnable() {

			@Override
			public void run() {

				Bukkit.getPluginManager().callEvent(new RegisterVarTypesEvent());

				Bukkit.getPluginManager().callEvent(new RegisterVariablesEvent());

				Bukkit.getPluginManager().callEvent(new OnEnableEvent());

			}

		}.runTask(this);

		Bukkit.getPluginCommand("worldtp").setExecutor(new WorldTp());

		// Main command of the plugin
		// Moved to com.fren_gor.commandCraftCore.commands.commands.CCC
		Bukkit.getPluginCommand("commandCraftCore").setExecutor(new Cmc());
		Bukkit.getPluginCommand("commandCraftCore").setTabCompleter(new Cmc());
		/*
		 * NewCommand n = new NewCommand("commandCraftCore",
		 * "commandCraftCore.mainCommand", true, "ccc") {
		 * 
		 * @Override public boolean onCommand(CommandSender sender, Command
		 * command, String label, String[] args) {
		 * 
		 * if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
		 * reload();
		 * 
		 * sender.sendMessage("§eCommandCraftCore Reloaded");
		 * 
		 * return true; }
		 * 
		 * sender.sendMessage("§cUsage: /commandCraftCore <reload>");
		 * 
		 * return false; }
		 * 
		 * @Override public List<String> onTabComplete(CommandSender sender,
		 * Command command, String alias, String[] args) {
		 * 
		 * return filterTabCompleteOptions( args.length == 1 ? Arrays.asList(new
		 * String[] { "reload" }) : new ArrayList<String>(), args);
		 * 
		 * }
		 * 
		 * public List<String> filterTabCompleteOptions(List<String> options,
		 * String[] args) { String lastArg = ""; if (args.length > 0) { lastArg
		 * = args[(args.length - 1)].toLowerCase(); } for (int i = 0; i <
		 * options.size(); i++) { if
		 * (!options.get(i).toLowerCase().startsWith(lastArg)) {
		 * options.remove(i--); } } return options; }
		 * 
		 * };
		 * 
		 * if (CommandCraftCore.getCommandMap().getCommand(n.getName()) != null)
		 * {
		 * 
		 * ((Map<String, Command>)
		 * ReflectionUtil.getField(CommandCraftCore.getCommandMap(),
		 * "knownCommands")) .remove(n.getName());
		 * 
		 * }
		 * 
		 * CommandCraftCore.getCommandMap().register(CommandCraftCore.
		 * getInstance().getDescription().getName(), n.getCommand());
		 */

		Bukkit.getPluginCommand("varhelp").setExecutor(new VarHelp());
		Bukkit.getPluginCommand("varhelp").setTabCompleter(new VarHelp());
		Bukkit.getPluginCommand("testforpermissions").setExecutor(new TestForPermissions());
		Bukkit.getPluginCommand("testforpermissions").setTabCompleter(new TestForPermissions());
		Bukkit.getPluginCommand("createscript").setExecutor(new CreateScript());
		Bukkit.getPluginCommand("createscript").setTabCompleter(new CreateScript());
		Bukkit.getPluginCommand("updatescript").setExecutor(new CreateScript.ScriptUpdater());
		Bukkit.getPluginCommand("updatescript").setTabCompleter(new CreateScript.ScriptUpdater());
		// Bukkit.getPluginCommand("varhelp").setExecutor(new Inventories());
		Bukkit.getPluginCommand("advancedclone").setExecutor(new AdvancedClone());
		Bukkit.getPluginCommand("advancedsetblock").setExecutor(new AdvancedSetBlock());
		Bukkit.getPluginCommand("advancedtestforblocks").setExecutor(new AdvancedTestforblocks());
		Bukkit.getPluginCommand("pluginmessagingchannel").setTabCompleter(new PluginMessagingChannel());
		Bukkit.getPluginCommand("pluginmessagingchannel").setExecutor(new PluginMessagingChannel());
		Bukkit.getPluginCommand("registerpluginmessagingchannel").setTabCompleter(new RegisterPluginMessagingChannel());
		Bukkit.getPluginCommand("registerpluginmessagingchannel").setExecutor(new RegisterPluginMessagingChannel());

	}

	private static void loadDir(File f) {
		try {
			if (!f.isDirectory()) {
				if (!f.getName().endsWith(".cmc") || ConfigManager.getDisabledScripts().contains(f.getName()))
					return;
				Reader r = new Reader(f);
				if (r.getType() == ScriptType.COMMAND)
					commandManager.registerCommand(commandManager.buildCommand(r, f), f);
				else if (r.getType() == ScriptType.EVENT)
					eventManager.registerEvent(eventManager.buildEvent(r, f), f);
				else
					loopManager.registerLoop(new NewLoop(r), f);
				return;
			}
			for (File ff : f.listFiles()) {
				loadDir(ff);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public void onDisable() {

		// Cannot use Bukkit.getPluginManager().callEvent(new OnStopEvent());
		// during the server shutdown
		Map<EventPriority, List<NewEvent>> map = new EnumMap<>(EventPriority.class);

		map.put(EventPriority.LOWEST, new LinkedList<>());
		map.put(EventPriority.LOW, new LinkedList<>());
		map.put(EventPriority.NORMAL, new LinkedList<>());
		map.put(EventPriority.HIGH, new LinkedList<>());
		map.put(EventPriority.HIGHEST, new LinkedList<>());
		map.put(EventPriority.MONITOR, new LinkedList<>());

		Map<File, NewEvent> ev = eventManager.getEvent("OnDisableEvent");

		if (ev != null) {

			for (NewEvent e : ev.values()) {
				map.get(e.getReader().getEventPriority()).add(e);
			}

			// reloadcount
			int rc = (int) ReflectionUtil.getField(craftServer, "reloadCount");

			OnDisableEvent stopEvent = new OnDisableEvent(rc > reloadCount);

			reloadCount = rc;

			try {

				for (NewEvent e : map.get(EventPriority.LOWEST))
					e.execute(e.getListener(), stopEvent);

				for (NewEvent e : map.get(EventPriority.LOW))
					e.execute(e.getListener(), stopEvent);

				for (NewEvent e : map.get(EventPriority.NORMAL))
					e.execute(e.getListener(), stopEvent);

				for (NewEvent e : map.get(EventPriority.HIGH))
					e.execute(e.getListener(), stopEvent);

				for (NewEvent e : map.get(EventPriority.HIGHEST))
					e.execute(e.getListener(), stopEvent);

				for (NewEvent e : map.get(EventPriority.MONITOR))
					e.execute(e.getListener(), stopEvent);

			} catch (EventException e1) {
				e1.printStackTrace();
			}

		}

		for (NewCommand n : commandManager.getCommands().listValue1()) {

			n.unregister();

		}

		for (Map<File, NewEvent> n : eventManager.getEvents().values()) {
			for (NewEvent ee : n.values()) {
				ee.unregister(false);
			}
		}

		eventManager.getEvents().clear();

		StaticActions.savedVariables.clear();

		SavingObject<Map<String, String>> s = new SavingObject<>(StaticActions.writtenVariables);

		s.save(new File(SAVES_FOLDER, "savedVars"));

		commandManager.getCommands().clear();
		eventManager.getEvents().clear();

		eventManager.getEventVars().clear();

	}

	public static boolean reloadScript(File script) {

		if (!script.exists() || script.isDirectory() || !script.getName().endsWith(".cmc"))
			return false;

		boolean b = true;

		if (commandManager.getCommands().containsValue2(script)) {

			Iterator<DoubleObject<NewCommand, File>> it = commandManager.getCommands().getValuesIterator();
			NewCommand e1 = null;
			while (it.hasNext()) {
				Entry<NewCommand, File> e = it.next();

				if (e.getValue().equals(script)) {
					e1 = e.getKey();
					b = false;
				}

			}
			if (e1 != null)
				e1.unregister();
		} else if (loopManager.getLoops().containsValue2(script)) {

			Iterator<DoubleObject<NewLoop, File>> it = loopManager.getLoops().getValuesIterator();
			NewLoop e1 = null;
			while (it.hasNext()) {
				Entry<NewLoop, File> e = it.next();

				if (e.getValue().equals(script)) {
					e1 = e.getKey();
					b = false;
					break;
				}

			}
			if (e1 != null)
				e1.unregister();
		} else {
			for (Map<File, NewEvent> map : eventManager.getEvents().values()) {
				Iterator<Entry<File, NewEvent>> it = map.entrySet().iterator();
				NewEvent e1 = null;
				while (it.hasNext()) {
					Entry<File, NewEvent> e = it.next();

					if (e.getKey().equals(script)) {
						e1 = e.getValue();
						b = false;
					}
				}
				if (e1 != null)
					e1.unregister();
			}
		}

		if (b) {
			return false;
		}

		loadDir(script);
		return true;
	}

	public static void reload() {

		Bukkit.getPluginManager().callEvent(new OnDisableEvent(true));

		Bukkit.getScheduler().cancelTasks(instance);

		for (NewCommand n : commandManager.getCommands().listValue1()) {

			n.unregister();

		}

		for (Map<File, NewEvent> n : eventManager.getEvents().values()) {
			for (NewEvent ee : n.values()) {
				ee.unregister(false);
			}
		}

		eventManager.getEvents().clear();

		StaticActions.savedVariables.clear();
		commandManager.getCommands().clear();
		eventManager.getEvents().clear();

		eventManager.getEventVars().clear();
		eventManager.getEventTasks().clear();

		if (!instance.getDataFolder().exists()) {
			instance.getDataFolder().mkdirs();
		}
		if (!new File(instance.getDataFolder(), "config.yml").exists()) {
			instance.getConfig().options().copyDefaults(true);
			try {
				instance.getConfig().save(new File(instance.getDataFolder(), "config.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			instance.getConfig().load(new File(instance.getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}

		ConfigManager.debugMode = instance.getConfig().getBoolean("debug-mode");
		ConfigManager.viewReadingTime = instance.getConfig().getBoolean("view-reading-time");
		ConfigManager.viewExecutingTime = instance.getConfig().getBoolean("view-executing-time");
		ConfigManager.ignoreLops = instance.getConfig().getBoolean("ignore-loops");
		ConfigManager.createFolders = instance.getConfig().getBoolean("create-folders");
		ConfigManager.setDisabledScripts(instance.getConfig().getStringList("disabled-scripts"));

		if (ConfigManager.getDisabledScripts().size() > 0) {
			System.out.println("Disabled Scripts:");
			for (String s : ConfigManager.getDisabledScripts()) {
				System.out.println(s);
			}
		}

		if (!new File(instance.getDataFolder(), "commands").exists()) {
			new File(instance.getDataFolder(), "commands").mkdir();
		}
		if (!new File(instance.getDataFolder(), "events").exists()) {
			new File(instance.getDataFolder(), "events").mkdir();
		}
		if (!new File(instance.getDataFolder(), "loops").exists()) {
			new File(instance.getDataFolder(), "loops").mkdir();
		}

		for (File f : instance.getDataFolder().listFiles()) {
			loadDir(f);
		}

		Bukkit.getPluginManager().callEvent(new RegisterVariablesEvent());
		Bukkit.getPluginManager().callEvent(new OnEnableEvent());

	}

	public static List<String> readFile(File file) throws IOException {
		List<String> l = new ArrayList<>();
		BufferedReader reader = null;
		String currentLine = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

			while ((currentLine = reader.readLine()) != null) {
				l.add(currentLine);
			}
		} finally {

			reader.close();

		}
		return l;
	}

}
