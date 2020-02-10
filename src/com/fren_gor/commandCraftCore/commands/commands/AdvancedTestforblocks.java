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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;

public class AdvancedTestforblocks implements CommandExecutor {

	private final World w;

	public AdvancedTestforblocks() {
		w = Bukkit.getWorld("world");
		Bukkit.getPluginCommand("advancedtestforblocks").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!cmd.getName().equalsIgnoreCase("advancedtestforblocks")) {
			return false;
		}

		if (args.length != 9) {
			sender.sendMessage("Usage: /advancedtestforblocks <x1> <y1> <z1> <x2> <y2> <z2> <x> <y> <z>");
			return false;
		}

		int[] a = new int[9];
		boolean C = sender instanceof ConsoleCommandSender;
		boolean B = sender instanceof BlockCommandSender;
		boolean E = sender instanceof BlockCommandSender;
		int X = C ? 0
				: B ? ((BlockCommandSender) sender).getBlock().getX()
						: E ? ((Entity) sender).getLocation().getBlockX()
								: ((Entity) ((ProxiedCommandSender) sender).getCallee()).getLocation().getBlockX();
		int Y = C ? 0
				: B ? ((BlockCommandSender) sender).getBlock().getY()
						: E ? ((Entity) sender).getLocation().getBlockX()
								: ((Entity) ((ProxiedCommandSender) sender).getCallee()).getLocation().getBlockY();
		int Z = C ? 0
				: B ? ((BlockCommandSender) sender).getBlock().getZ()
						: E ? ((Entity) sender).getLocation().getBlockX()
								: ((Entity) ((ProxiedCommandSender) sender).getCallee()).getLocation().getBlockZ();

		for (int i = 0; i < 9; i++) {
			if (args[i].startsWith("~")) {
				String s = args[i].substring(1);
				if (!s.isEmpty())
					try {
						a[i] = get(i, X, Y, Z) + Integer.parseInt(s);
					} catch (NumberFormatException e) {
						sender.sendMessage(args[i] + " is not a valid number");
						return false;
					}
				else
					a[i] = get(i, X, Y, Z);
			} else
				try {
					a[i] = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					sender.sendMessage(args[i] + " is not a valid number");
					return false;
				}
		}

		if (a[0] > a[3]) {
			int i = a[0];
			a[0] = a[3];
			a[3] = i;
		}

		if (a[1] > a[4]) {
			int i = a[1];
			a[1] = a[4];
			a[4] = i;
		}

		if (a[2] > a[5]) {
			int i = a[2];
			a[2] = a[5];
			a[5] = i;
		}

		int x1 = a[6];
		for (int x = a[0]; x <= a[3]; x++) {
			int y1 = a[7];
			for (int y = a[1]; y <= a[4]; y++) {
				int z1 = a[8];
				for (int z = a[2]; z <= a[5]; z++) {

					Block b = w.getBlockAt(x1, y1, z1);
					Block b1 = w.getBlockAt(x, y, z);
					if (b.getType() != b1.getType() || b.getData() != b1.getData()) {
						return false;
					}

					z1++;
				}
				y1++;
			}
			x1++;
		}

		return true;
	}

	static int get(int i, int x, int y, int z) {
		if (i == 0 || i == 3 || i == 6)
			return x;
		if (i == 1 || i == 3 || i == 7)
			return y;
		if (i == 2 || i == 4 || i == 8)
			return z;
		return 0;
	}

}
