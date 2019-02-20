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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.reflections.Reflections;

import com.fren_gor.commandCraftCore.utils.Utils;
import com.fren_gor.commandCraftCore.utils.saveUtils.TripleObject;
import com.fren_gor.commandCraftCore.vars.Type;
import com.fren_gor.commandCraftCore.vars.VariableManager;

public class CreateScript implements CommandExecutor, TabCompleter {

	private static String def1 = "## Event Script";
	private static String def2 = "## Variables:";
	private static String def3 = "##   $ -> Type: &";
	private static String def4 = "!info event ";
	private static List<String> defOp = Arrays.asList("event", "command", "loop");
	private static List<String> cmds = Arrays.asList("## Command Script", "##", "## Variables:",
			"##   $senderType -> Type: String (PLAYER, CONSOLE or COMMAND_BLOCK)",
			"##   $senderName -> Type: String (sender name)",
			"##   $player -> Type: Player (if sender is a player, else type => null)",
			"##   $label -> Type: String (command as player wrote it)",
			"##   $commandName -> Type: String (command name as defined by the plugin)",
			"##   $args -> Type: List of Stirng (every String is an argument of the command, the argunemt)", "",
			"!info command &", "!tab prova0 prova1,prova", "", "!return_false");
	private static List<String> updateCmds = Arrays.asList("## Command Script", "##", "## Variables:",
			"##   $senderType -> Type: String (PLAYER, CONSOLE or COMMAND_BLOCK)",
			"##   $senderName -> Type: String (sender name)",
			"##   $player -> Type: Player (if sender is a player, else type => null)",
			"##   $label -> Type: String (command as player wrote it)",
			"##   $commandName -> Type: String (command name as defined by the plugin)",
			"##   $args -> Type: List of Stirng (every String is an argument of the command, the argunemt)", "");
	private static List<String> priorities = Arrays.asList("Lowest", "Low", "Normal", "High", "Highest", "Monitor");

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§cUsage: /createscript <event, command, loop> ...");
			return false;
		}
		if (args[0].equalsIgnoreCase("event")) {

			if (args.length < 2 || args.length > 3) {
				sender.sendMessage("§cUsage: /createscript event <eventName> [eventPriority]");
				return false;
			}

			String s = args[1];

			Class<? extends Event> clazz = null;
			boolean b = false;

			EventPriority p;

			try {
				p = args.length == 3 ? EventPriority.valueOf(args[2].toUpperCase()) : null;
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§cIllegal EventPriority " + args[2]);
				return false;
			}

			if (s.contains(".")) {
				b = true;
				try {
					clazz = (Class<? extends Event>) Class.forName(s);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					sender.sendMessage("§cInvalid event " + s);
					return false;
				}

			} else {

				Reflections reflections = new Reflections("org.bukkit.event");

				Set<Class<? extends Event>> allClasses = reflections.getSubTypesOf(Event.class);

				for (Class<? extends Event> c : allClasses) {

					if (c.getSimpleName().equalsIgnoreCase(s)) {

						clazz = c;
						break;

					}

				}

				if (clazz == null) {
					Reflections reflections1 = new Reflections("com.fren_gor.commandCraftCore.events.events");

					Set<Class<? extends Event>> allClasses1 = reflections1.getSubTypesOf(Event.class);

					for (Class<? extends Event> c : allClasses1) {

						if (c.getSimpleName().equalsIgnoreCase(s)) {

							clazz = c;
							break;

						}

					}
				}
			}

			if (clazz == null) {
				sender.sendMessage("§cEvent '" + s + "' does not exist");
				return false;
			}

			List<String> l = new LinkedList<>();

			l.add(def1);

			boolean isCancellable = false;

			for (Class<?> in : clazz.getInterfaces()) {
				if (in.equals(Cancellable.class)) {
					isCancellable = true;
					break;
				}
			}

			l.add(def2);

			if (CommandCraftCore.getEventManager().getEventVars().containsKey(clazz)
					&& !CommandCraftCore.getEventManager().getEventVars().get(clazz).isEmpty()) {
				for (Entry<String, TripleObject<Type, String, BiConsumer<Event, VariableManager>>> e : CommandCraftCore
						.getEventManager().getEventVars().get(clazz).getHashMap().entrySet()) {

					if (e.getValue().getValue1() != null && !e.getValue().getValue1().isEmpty()) {
						l.add(def3.replace("$", e.getKey()).replace("&",
								StringUtils.capitalize(e.getValue().getKey().toString()) + " ("
										+ e.getValue().getValue1() + ")"));
					} else {
						l.add(def3.replace("$", e.getKey()).replace("&",
								StringUtils.capitalize(e.getValue().getKey().toString())));
					}
				}

				l.add("##");
			}

			if (isCancellable) {
				l.add("##   $cancelled -> Type: BOOLEAN (true if the event is cancelled, false if it's not)");
			} else {
				l.add("##   $cancelled -> Type: BOOLEAN (everytime false because this event is not cancellable)");
			}

			if (CommandCraftCore.getEventManager().getEventTasks().containsKey(clazz)
					&& !CommandCraftCore.getEventManager().getEventTasks().getValue1(clazz).isEmpty()) {
				l.add("##");
				for (String ss : CommandCraftCore.getEventManager().getEventTasks().getValue1(clazz)) {
					if (ss != null && !ss.isEmpty()) {
						l.add("## " + ss);
					}
				}
				l.add("");
			} else {
				l.add("");
			}

			l.add(isCancellable ? "## This event is cancellable" : "## This event is not cancellable");
			l.add("");

			String st = (b ? clazz.getName() : clazz.getSimpleName());

			l.add(def4 + st + (p == null ? "" : " " + capitalize(p.toString())));

			File f = new File(CommandCraftCore.getInstance().getDataFolder(), st + ".cmc");

			for (int loop = 1; f.exists(); loop++) {

				f = new File(CommandCraftCore.getInstance().getDataFolder(), st + "-" + loop + ".cmc");

			}

			try {
				writeFile(f, l);
			} catch (IOException e) {
				sender.sendMessage("§cFailed to generate the default event script");
				e.printStackTrace();
				return false;
			}

			sender.sendMessage("§aSuccesfully generated default script for event " + st);

		} else if (args[0].equalsIgnoreCase("command")) {

			if (args.length < 2) {
				sender.sendMessage("§cUsage: /createscript command <commandName> [permission] [aliases]");
				return false;
			}

			String s = args[1];

			if (CommandCraftCore.getCommandManager().getCommands().containsKey(s)
					|| new File(CommandCraftCore.getInstance().getDataFolder(), s + ".cmc").exists()) {
				sender.sendMessage("§cA command with name '" + s + "' already exist!");
				return false;
			}

			List<String> c = new ArrayList<>(cmds);
			String ss = c.get(10).replace("&", s);

			if (args.length > 2) {
				if (args.length == 3) {

					ss += " " + args[2];

				} else {

					StringJoiner arg = new StringJoiner(" ");

					for (int i = 3; i < args.length; i++) {
						arg.add(args[i]);
					}

					ss += " " + args[2] + " " + arg.toString();

				}
			}

			c.set(10, ss);
			try {
				writeFile(new File(CommandCraftCore.getInstance().getDataFolder(), s + ".cmc"), c);
			} catch (IOException e) {
				sender.sendMessage("§cFailed to generate the default event script");
				e.printStackTrace();
				return false;
			}
			/*
			 * try {
			 * FileUtils.copyInputStreamToFile(CommandCraftCore.getInstance().
			 * getResource("command.cmc"), new
			 * File(CommandCraftCore.getInstance().getDataFolder(), s +
			 * ".cmc")); } catch (IOException e) { e.printStackTrace(); }
			 */

			sender.sendMessage("§aSuccesfully generated default command script");

		} else if (args[0].equalsIgnoreCase("loop")) {
			// ----- Temp lines ---------
			sender.sendMessage("§cComing soon");
			return false;
			// -----------------------
			/*
			 * try { if (args.length == 2) {
			 * 
			 * } else if (args.length == 3) {
			 * 
			 * } else {
			 * sender.sendMessage("§cUsage: /createscript loop <num> [num]");
			 * return false; } } catch (NumberFormatException e) {
			 * sender.sendMessage("§cUsage: /createscript loop <num> [num]");
			 * return false; }
			 */
		} else {
			sender.sendMessage("§cUsage: /createscript <event, command, loop> ...");
			return false;
		}
		return true;
	}

	private static String capitalize(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static void writeFile(File file, List<String> lines) throws IOException {

		if (!file.exists() || file.isDirectory())

			file.createNewFile();

		if (lines.size() == 0)
			return;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));

			for (String s : lines) {

				writer.write(s);
				writer.newLine();

			}
		} finally {

			writer.close();

		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 1) {
			return Utils.filterTabCompleteOptions(defOp, args);
		}
		if (args[0].equalsIgnoreCase("event")) {
			if (args.length == 2) {

				return Utils.filterTabCompleteOptions(CommandCraftCore.getEventManager().getRegistredEvents(), args);

			} else if (args.length == 3) {

				return Utils.filterTabCompleteOptions(priorities, args);
			}
		}

		return new ArrayList<>();
	}

	static class ScriptUpdater implements CommandExecutor, TabCompleter {

		@SuppressWarnings("unchecked")
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

			if (args.length == 0) {
				sender.sendMessage(
						"§cUsage: /updatescript <fileName> or /updatescript <directory> [subdirectory] ... <fileName>");
				return false;
			}

			File f = CommandCraftCore.getInstance().getDataFolder();

			if (args.length == 1) {
				f = new File(f, args[0].endsWith(".cmc") ? args[0] : args[0] + ".cmc");
			} else {
				StringJoiner j = new StringJoiner(File.pathSeparator);
				for (String s : args) {
					j.add(s);
				}
				String s = j.toString();
				if (!args[args.length - 1].endsWith(".cmc")) {
					s += ".cmc";
				}
				f = new File(f, s);
			}

			if (!f.exists()) {
				sender.sendMessage("§cFile §e'" + f.getPath() + "'§c doesn't exist");
				return false;
			}
			List<String> file;
			try {
				file = clean(CommandCraftCore.readFile(f));
			} catch (IOException e1) {
				sender.sendMessage("§cInvalid File §e'" + f.getPath() + "'");
				return false;
			}

			for (String s : file) {
				s = Reader.trim(s);
				if (s.startsWith("!info event ")) {

					String st = s.substring(12).split(" ")[0];

					if (st.isEmpty()) {
						sender.sendMessage("§cInvalid '!info' in file §e'" + f.getPath() + "'");
						return false;
					}

					Class<? extends Event> clazz = null;

					if (st.contains(".")) {
						try {
							clazz = (Class<? extends Event>) Class.forName(st);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							sender.sendMessage("§cInvalid event " + st);
							return false;
						}

					} else {

						Reflections reflections = new Reflections("org.bukkit.event");

						Set<Class<? extends Event>> allClasses = reflections.getSubTypesOf(Event.class);

						for (Class<? extends Event> c : allClasses) {

							if (c.getSimpleName().equalsIgnoreCase(st)) {

								clazz = c;
								break;

							}

						}

						if (clazz == null) {
							Reflections reflections1 = new Reflections("com.fren_gor.commandCraftCore.events.events");

							Set<Class<? extends Event>> allClasses1 = reflections1.getSubTypesOf(Event.class);

							for (Class<? extends Event> c : allClasses1) {

								if (c.getSimpleName().equalsIgnoreCase(st)) {

									clazz = c;
									break;

								}

							}
						}
					}

					if (clazz == null) {
						sender.sendMessage("§cEvent '" + st + "' does not exist");
						return false;
					}

					List<String> l = new LinkedList<>();

					l.add(def1);

					boolean isCancellable = false;

					for (Class<?> in : clazz.getInterfaces()) {
						if (in.equals(Cancellable.class)) {
							isCancellable = true;
							break;
						}
					}

					l.add(def2);

					if (CommandCraftCore.getEventManager().getEventVars().containsKey(clazz)
							&& !CommandCraftCore.getEventManager().getEventVars().get(clazz).isEmpty()) {
						for (Entry<String, TripleObject<Type, String, BiConsumer<Event, VariableManager>>> e : CommandCraftCore
								.getEventManager().getEventVars().get(clazz).getHashMap().entrySet()) {

							if (e.getValue().getValue1() != null && !e.getValue().getValue1().isEmpty()) {
								l.add(def3.replace("$", e.getKey()).replace("&",
										StringUtils.capitalize(e.getValue().getKey().toString()) + " ("
												+ e.getValue().getValue1() + ")"));
							} else {
								l.add(def3.replace("$", e.getKey()).replace("&",
										StringUtils.capitalize(e.getValue().getKey().toString())));
							}
						}

						l.add("##");
					}

					if (isCancellable) {
						l.add("##   $cancelled -> Type: BOOLEAN (true if the event is cancelled, false if it's not)");
					} else {
						l.add("##   $cancelled -> Type: BOOLEAN (initially false because this event is not cancellable)");
					}

					if (CommandCraftCore.getEventManager().getEventTasks().containsKey(clazz)
							&& !CommandCraftCore.getEventManager().getEventTasks().getValue1(clazz).isEmpty()) {
						l.add("##");
						for (String ss : CommandCraftCore.getEventManager().getEventTasks().getValue1(clazz)) {
							if (ss != null && !ss.isEmpty()) {
								l.add("## " + ss);
							}
						}
						l.add("");
					} else {
						l.add("");
					}

					l.add(isCancellable ? "## This event is cancellable" : "## This event is not cancellable");

					l.add("");

					l.addAll(file);

					try {
						writeFile(f, l);
					} catch (IOException e) {
						sender.sendMessage("§cFailed to create the default event script");
						e.printStackTrace();
						return false;
					}

					sender.sendMessage("§aSuccesfully updated file " + st);

					return true;
				}

				if (s.startsWith("!info command ")) {

					String st = s.substring(14).split(" ")[0];

					if (st.isEmpty()) {
						sender.sendMessage("§cInvalid '!info' in file §e'" + f.getPath() + "'");
						return false;
					}

					List<String> c = new LinkedList<>(updateCmds);

					c.addAll(file);

					try {
						writeFile(f, c);
					} catch (IOException e) {
						sender.sendMessage("§cFailed to create the default event script");
						e.printStackTrace();
						return false;
					}
					/*
					 * try { FileUtils.copyInputStreamToFile(CommandCraftCore.
					 * getInstance( ). getResource("command.cmc"), new
					 * File(CommandCraftCore.getInstance().getDataFolder(), s +
					 * ".cmc")); } catch (IOException e) { e.printStackTrace();
					 * }
					 */

					sender.sendMessage("§aSuccesfully updated file " + st);

					return true;
				}

				if (s.startsWith("!info loop")) {
					sender.sendMessage("§cLoops are not supported yed");
					return false;
				}
			}

			sender.sendMessage("§cCouldn't find '!info' in file §e'" + f.getPath() + "'");
			return false;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

			return Utils.filterTabCompleteOptions(getFiles("", CommandCraftCore.getInstance().getDataFolder()), args);

		}

	}

	private static List<String> clean(List<String> l) {
		List<String> ll = new ArrayList<>(l.size());

		boolean info = false;
		for (String s : l) {
			String fixed = Reader.trim(s);
			if (!fixed.startsWith("##")) {
				if (!info && fixed.isEmpty())
					continue;
				ll.add(s);
				if (fixed.startsWith("!info "))
					info = true;
			}
		}
		return ll;
	}

	private static List<String> getFiles(String prefix, File file) {

		if (!file.isDirectory())
			return new ArrayList<>();

		List<String> l = new LinkedList<>();

		for (File f : file.listFiles()) {

			if (f.isDirectory()) {
				l.addAll(getFiles(prefix.isEmpty() ? f.getName() : prefix + " " + f.getName(), f));
				continue;
			}

			if (f.getName().endsWith(".cmc"))
				l.add(prefix.isEmpty() ? f.getName() : prefix + " " + f.getName());

		}

		return l;

	}

}
