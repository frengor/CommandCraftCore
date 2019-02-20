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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import com.fren_gor.commandCraftCore.CommandCraftCore;

public abstract class NewCommand implements CommandExecutor, TabCompleter {

	private final String name;
	private final String permission;
	private final List<String> aliseas;
	private PluginCommand p = null;

	public NewCommand(String name, String permission, boolean tabCompleter, String... aliseas) {

		this.name = name;
		this.permission = permission;
		this.aliseas = Arrays.asList(aliseas);

		try {

			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);

			c.setAccessible(true);

			p = c.newInstance(name, CommandCraftCore.getInstance());

		} catch (Exception e) {
			e.printStackTrace();
		}

		p.setAliases(Arrays.asList(aliseas));
		p.setExecutor(this);
		if (tabCompleter)
			p.setTabCompleter(this);
		p.setPermission(permission);

	}

	public final String getName() {
		return name;
	}

	public final String getPermission() {
		return permission;
	}

	public final List<String> getAliseas() {
		return aliseas;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<>();
	}

	@Override
	public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

	public PluginCommand getCommand() {

		return p;

	}

	public void unregister() {

		if (!CommandCraftCore.getCommandManager().getCommands().containsKey(name))
			return;

		if (CommandCraftCore.getCommandMap().getCommand(name) != null) {

			CommandCraftCore.getKnownCommands().remove(name);

		}

		CommandCraftCore.getCommandManager().getCommands().remove(name);

	}

}
