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
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.events.events.PluginMessagingChannelEvent;

public class RegisterPluginMessagingChannel implements CommandExecutor, TabCompleter, PluginMessageListener {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length < 2) {
			sender.sendMessage("§cUsage /registerpluginmessagingchannel <incoming|outgoing> <channelName>");
			return false;
		}

		if (args[0].equalsIgnoreCase("incoming")) {

			if (Bukkit.getMessenger().isIncomingChannelRegistered(CommandCraftCore.getInstance(), args[1])) {
				sender.sendMessage("That incoming channel has already been registered, you are free to use it");
				return false;
			}

			Bukkit.getMessenger().registerIncomingPluginChannel(CommandCraftCore.getInstance(), args[1], this);
			sender.sendMessage("§aSuccesfully registered incoming channel §e" + args[1]);

		} else if (args[0].equalsIgnoreCase("outgoing")) {

			if (Bukkit.getMessenger().isOutgoingChannelRegistered(CommandCraftCore.getInstance(), args[1])) {
				sender.sendMessage("That outgoing channel has already been registered, you are free to use it");
				return false;
			}

			Bukkit.getMessenger().registerOutgoingPluginChannel(CommandCraftCore.getInstance(), args[1]);
			sender.sendMessage("§aSuccesfully registered outgoing channel §e" + args[1]);

		} else {
			sender.sendMessage("§cUsage /registerpluginmessagingchannel <ingoing|outgoing> <channelName>");
			return false;
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<>();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		PluginMessagingChannelEvent e = new PluginMessagingChannelEvent(channel, player, message);

		Bukkit.getPluginManager().callEvent(e);

	}

}
