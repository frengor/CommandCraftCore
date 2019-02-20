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

package com.fren_gor.commandCraftCore.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.Executor;
import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.Reader.Type;
import com.fren_gor.commandCraftCore.ReflectionUtil;
import com.fren_gor.commandCraftCore.utils.Utils;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleMultiHashMap;
import com.fren_gor.commandCraftCore.vars.ListVar;
import com.fren_gor.commandCraftCore.vars.NullVar;
import com.fren_gor.commandCraftCore.vars.PlayerVar;
import com.fren_gor.commandCraftCore.vars.StringVar;

public class CommandManager {

	private DoubleMultiHashMap<String, NewCommand, File> commands;

	private File datafolder;

	public CommandManager() {

		commands = new DoubleMultiHashMap<>();
		datafolder = new File(CommandCraftCore.getInstance().getDataFolder(), "commands");

	}

	@SuppressWarnings("unchecked")
	public void registerCommand(NewCommand command, File file) {

		if (CommandCraftCore.getCommandMap().getCommand(command.getName()) != null) {

			((Map<String, Command>) ReflectionUtil.getField(CommandCraftCore.getCommandMap(), "knownCommands"))
					.remove(command.getName());

		}

		CommandCraftCore.getCommandMap().register(CommandCraftCore.getInstance().getDescription().getName(),
				command.getCommand());

		commands.put(command.getName(), command, file);

	}

	public DoubleMultiHashMap<String, NewCommand, File> getCommands() {
		return commands;
	}

	public NewCommand buildCommand(File f) throws IOException {
		return buildCommand(new Reader(readFile(f), f), f);
	}

	public NewCommand buildCommand(Reader reader, File f) throws IOException {

		if (reader.getType() != Type.COMMAND) {
			Bukkit.getConsoleSender()
					.sendMessage("[CommandCraftCore] §cFile " + f.getPath() + " is not a command file");
			throw new IllegalArgumentException("File " + f.getPath() + " is not a command file");
		}

		if (reader.getType() != Reader.Type.COMMAND)
			throw new RuntimeException("Invalid File " + f.getName() + " in Command Folder!");

		String commandName = reader.getName();
		String perm = reader.getPermission();
		String[] aliseas = reader.getAliseas();
		List<List<String>> tab = reader.getTabCompleter();

		if (tab.isEmpty()) {

			return new NewCommand(commandName, perm, false, aliseas) {

				@Override
				public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

					Executor e = new Executor();

					new StringVar(e.getManager(), "commandName", command.getName()).setFinal();

					new StringVar(e.getManager(), "senderType", sender instanceof Player ? "PLAYER"
							: sender instanceof ConsoleCommandSender ? "CONSOLE" : "COMMAND_BLOCK").setFinal();
					new StringVar(e.getManager(), "senderName", sender.getName()).setFinal();

					if (sender instanceof Player) {
						new PlayerVar(e.getManager(), "player", (Player) sender);
					} else {
						new NullVar(e.getManager(), "player");
					}

					new StringVar(e.getManager(), "label", label).setFinal();

					ListVar v = new ListVar(e.getManager(), "args", args);

					for (String s : args) {
						v.getValue().add(new StringVar(v.getManager(), v.getManager().generateInternalName(), s));
					}

					v.setFinal();

					return e.execute(reader);

				}

			};
		} else {
			return new NewCommand(commandName, perm, true, aliseas) {

				@Override
				public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

					Executor e = new Executor();

					new StringVar(e.getManager(), "commandName", command.getName()).setFinal();

					new StringVar(e.getManager(), "senderType", sender instanceof Player ? "PLAYER"
							: sender instanceof ConsoleCommandSender ? "CONSOLE" : "COMMAND_BLOCK").setFinal();
					new StringVar(e.getManager(), "senderName", sender.getName()).setFinal();

					if (sender instanceof Player) {
						new PlayerVar(e.getManager(), "player", (Player) sender);
					} else {
						new NullVar(e.getManager(), "player");
					}

					new StringVar(e.getManager(), "label", label).setFinal();

					ListVar v = new ListVar(e.getManager(), "args", args);

					for (String s : args) {
						v.getValue().add(new StringVar(v.getManager(), v.getManager().generateInternalName(), s));
					}

					v.setFinal();

					return e.execute(reader);

				}

				@Override
				public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
					List<String> completions = new ArrayList<>(tab.get(args.length == 0 ? 0 : args.length - 1));
					completions = Utils.filterTabCompleteOptions(completions, args);
					return completions;
				}

			};
		}

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
