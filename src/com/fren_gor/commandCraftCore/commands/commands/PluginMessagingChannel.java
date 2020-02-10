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

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class PluginMessagingChannel implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length < 3) {
			sender.sendMessage(
					"§cUsage: /pluginmessagingchannel <channel> <subchannel> <player> <message like (text with no prefix = string), char%%character, string%%text, byte%%number, short%%number, int%%integer, long%%number, float%%decimalNum, double%%decimalNum, boolean%%true-or-false>");
			return false;
		}

		Player p = Bukkit.getPlayer(args[2]);

		if (p == null) {
			sender.sendMessage("§cPlayer " + args[2] + "isn't online");
			return false;
		}

		if (!Bukkit.getServer().getMessenger().isOutgoingChannelRegistered(CommandCraftCore.getInstance(), args[0])) {
			sender.sendMessage("§cChannel '" + args[0]
					+ "' is not registered yet, you may use command /registerpluginmessagingchannel to register it.");
			return false;
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(args[1]);

		try {
			for (int i = 3; i < args.length; i++) {

				String[] split = args[i].split("%%");

				if (split.length == 1) {
					out.writeUTF(split[0]);
					continue;
				}
				switch (split[0].toLowerCase()) {
					case "int":
						out.writeInt(Integer.parseInt(split[1]));
						break;

					case "boolean":
						out.writeBoolean(Boolean.parseBoolean(split[1]));
						break;

					case "double":
						out.writeDouble(Double.parseDouble(split[1]));
						break;

					case "float":
						out.writeFloat(Float.parseFloat(split[1]));
						break;

					case "short":
						out.writeShort(Short.parseShort(split[1]));
						break;

					case "long":
						out.writeLong(Long.parseLong(split[1]));
						break;

					case "byte":

						out.writeByte(Byte.parseByte(split[1]));

						break;

					case "char":

						out.writeChar(split[1].length() == 0 ? Character.MIN_VALUE : split[1].charAt(0));

						break;

					case "string":

						out.writeUTF(split[1]);

						break;

					default:
						out.writeUTF(split[0] + "%%" + split[1]);
						break;
				}

			}
		} catch (NumberFormatException e) {
			sender.sendMessage("§cMalformed number value: \n" + e.getMessage());
			return false;
		}

		p.sendPluginMessage(CommandCraftCore.getInstance(), "BungeeCord", out.toByteArray());

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		return new ArrayList<>();
	}

}
