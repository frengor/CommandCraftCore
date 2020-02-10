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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.utils.Utils;

public class Cmc implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			CommandCraftCore.reload();

			sender.sendMessage("§eCommandCraftCore Reloaded");

			return true;
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("reloadscript") && args[1].length() > 0) {

			File f = findFile(args[1].endsWith(".cmc") ? args[1] : args[1] + ".cmc",
					CommandCraftCore.getInstance().getDataFolder());

			if (f == null) {
				sender.sendMessage(
						"§cCouldn't find file '" + (args[1].endsWith(".cmc") ? args[1] : args[1] + ".cmc") + "'");
				return false;
			}

			if (!CommandCraftCore.reloadScript(f)) {
				sender.sendMessage(
						"§cCouldn't reload script '" + (args[1].endsWith(".cmc") ? args[1] : args[1] + ".cmc") + "'");
				return false;
			}

			sender.sendMessage("§eSuccesfully reloaded script §7" + f.getName());

			return true;
		}

		sender.sendMessage("§cUsage: /commandCraftCore <reload> or /commandCraftCore <reloadscript> <scriptFileName>");

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		return Utils.filterTabCompleteOptions(
				args.length == 1 ? Arrays.asList(new String[] { "reload", "reloadscript" }) : new ArrayList<String>(),
				args);

	}

	public static File findFile(String name, File file) {
		if (file.isDirectory()) {
			for (File fil : file.listFiles()) {
				if (fil.isDirectory()) {
					File f = findFile(name, fil);
					if (f != null)
						return f;
				} else if (name.equals(fil.getName())) {
					return fil;
				}
			}
		} else {
			if (file.getName().equals(name))
				return file;
		}
		return null;
	}

}
