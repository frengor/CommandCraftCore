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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdvancedSetBlock implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!cmd.getName().equalsIgnoreCase("advancedsetblock")) {
			return false;
		}

		if (args.length != 6) {
			sender.sendMessage("§cUsage: /advancedsetblock <world> <x> <y> <z> <material> <data>");
			return false;
		}

		World w = Bukkit.getWorld(args[0]);

		if (w == null) {

			sender.sendMessage("§cWorld " + args[0] + " doesn't exist");

			return false;
		}

		int x, y, z;

		try {
			x = Integer.parseInt(args[1]);
			y = Integer.parseInt(args[2]);
			z = Integer.parseInt(args[3]);
		} catch (NumberFormatException e) {
			sender.sendMessage("§cUsage: /advancedsetblock <world> <x> <y> <z> <material> <data>");
			return false;
		}

		Block b = w.getBlockAt(x, y, z);

		try {
			b.setType(Material.valueOf(args[4].toUpperCase()));
		} catch (Exception e) {
			sender.sendMessage("§cInvalid Material " + args[4]);
			return false;
		}

		try {
			b.setData(Byte.parseByte(args[5]));
		} catch (Exception e) {
			sender.sendMessage("§cInvalid <data>");
			return false;
		}

		sender.sendMessage("§aSuccessfully set block");

		return true;
	}

}
