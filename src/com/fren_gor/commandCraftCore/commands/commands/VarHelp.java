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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.fren_gor.commandCraftCore.utils.Utils;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleObject;
import com.fren_gor.commandCraftCore.vars.VarType;

public class VarHelp implements CommandExecutor, TabCompleter {

	private final static List<String> list = new ArrayList<>(VarType.getRegisteredTypes().size());
	private final static Map<String, DoubleObject<Set<String>, String>> map = new HashMap<>();
	private final static String types;

	static {
		for (VarType t : VarType.getRegisteredTypes()) {
			list.add(t.toString());
			StringJoiner s = new StringJoiner("§f, §e");
			for (String ss : t.getMethods()) {

				s.add(ss);

			}
			map.put(t.toString(), new DoubleObject<>(t.getMethods(), s.toString()));
		}
		StringJoiner s1 = new StringJoiner("§f, §e");
		for (String ss : list) {

			s1.add(ss);

		}
		types = s1.toString();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length != 1) {
			sender.sendMessage("§aVariable types: §e" + types);
			return true;
		}

		DoubleObject<Set<String>, String> d = map.get(args[0].toUpperCase());

		if (d == null) {
			sender.sendMessage("Invalid variable type " + args[0]
					+ ". Try to tab-complete to have a full list of valid variable types");
			return false;
		}
		sender.sendMessage("§aMethods for variable type " + args[0] + ": §e" + d.getValue());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			return Utils.filterTabCompleteOptions(list, args);
		}
		if (args.length == 2) {
			DoubleObject<Set<String>, String> l = map.get(args[0].toUpperCase());
			if (l == null)
				return new ArrayList<>();
			return Utils.filterTabCompleteOptions(l.getKey(), args);
		}
		return new ArrayList<>();
	}

}
