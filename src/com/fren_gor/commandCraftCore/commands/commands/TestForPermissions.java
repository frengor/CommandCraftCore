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

package com.fren_gor.commandCraftCore.commands.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.fren_gor.commandCraftCore.utils.Utils;

public class TestForPermissions implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length < 2) {
			sender.sendMessage("§cUsage: /testforpermissions <player> <permissions...>");
			return true;
		}

		Player p = Bukkit.getPlayer(args[0]);

		if (p == null) {
			sender.sendMessage("§cPlayer " + args[0] + " isn't online");
			return false;
		}

		for (int i = 1; i < args.length; i++) {
			if (!p.hasPermission(args[i])) {
				sender.sendMessage("§c" + args[0] + " doesn't have permission " + args[i]);
				return false;
			}
		}
		sender.sendMessage("§a" + args[0] + " has all the tested permissions");
		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			List<String> online = new ArrayList<>(Bukkit.getOnlinePlayers().size());
			for (Player p : Bukkit.getOnlinePlayers())
				online.add(p.getName());
			return Utils.filterTabCompleteOptions(online, args);
		}
		return new ArrayList<>();
	}

}
