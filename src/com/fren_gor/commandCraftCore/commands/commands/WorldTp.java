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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			if (args.length == 1) {

				World w = Bukkit.getWorld(args[0]);

				if (w == null) {
					sender.sendMessage("§cCouldn't find world " + args[0]);
					return false;
				}

				((Player) sender).teleport(w.getSpawnLocation());

				sender.sendMessage("§aYou have been teleported to world " + args[0]);

			} else if (args.length == 2) {
				Player p = Bukkit.getPlayer(args[0]);

				if (p == null) {
					sender.sendMessage("§cPlayer " + args[0] + " isn't online");
					return false;
				}

				World w = Bukkit.getWorld(args[1]);

				if (w == null) {
					sender.sendMessage("§cCouldn't find world " + args[1]);
					return false;
				}

				p.teleport(w.getSpawnLocation());

				sender.sendMessage("§aTeleported player " + args[0] + " to world " + args[1]);

			} else if (args.length == 4) {
				World w = Bukkit.getWorld(args[0]);

				if (w == null) {
					sender.sendMessage("§cCouldn't find world " + args[0]);
					return false;
				}
				double x, y, z = 0;

				try {
					x = Double.parseDouble(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[2]);
					return false;
				}

				try {
					y = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[3]);
					return false;
				}

				try {
					z = Double.parseDouble(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[4]);
					return false;
				}

				((Player) sender).teleport(new Location(w, x, y, z));
				sender.sendMessage("§aYou have been teleported to world " + args[0]);

			} else if (args.length == 5) {
				Player p = Bukkit.getPlayer(args[0]);

				if (p == null) {
					sender.sendMessage("§cPlayer " + args[0] + " isn't online");
					return false;
				}
				World w = Bukkit.getWorld(args[1]);

				if (w == null) {
					sender.sendMessage("§cCouldn't find world " + args[1]);
					return false;
				}

				double x, y, z = 0;

				try {
					x = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[2]);
					return false;
				}

				try {
					y = Double.parseDouble(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[3]);
					return false;
				}

				try {
					z = Double.parseDouble(args[4]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[4]);
					return false;
				}

				p.teleport(new Location(w, x, y, z));

				sender.sendMessage("§aTeleported player " + args[0] + " to world " + args[1]);

			} else if (args.length == 6) {

				World w = Bukkit.getWorld(args[0]);

				if (w == null) {
					sender.sendMessage("§cCouldn't find world " + args[0]);
					return false;
				}

				double x, y, z = 0;
				float yaw, pitch = 0;

				try {
					x = Double.parseDouble(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[2]);
					return false;
				}

				try {
					y = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[3]);
					return false;
				}

				try {
					z = Double.parseDouble(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[4]);
					return false;
				}

				try {
					yaw = Float.parseFloat(args[4]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[5]);
					return false;
				}

				try {
					pitch = Float.parseFloat(args[5]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[6]);
					return false;
				}

				((Player) sender).teleport(new Location(w, x, y, z, yaw, pitch));
				sender.sendMessage("§aYou have been teleported to world " + args[0]);

			} else if (args.length == 7) {
				Player p = Bukkit.getPlayer(args[0]);

				if (p == null) {
					sender.sendMessage("§cPlayer " + args[0] + " isn't online");
					return false;
				}
				World w = Bukkit.getWorld(args[1]);

				if (w == null) {
					sender.sendMessage("§cCouldn't find world " + args[1]);
					return false;
				}

				double x, y, z = 0;
				float yaw, pitch = 0;

				try {
					x = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[2]);
					return false;
				}

				try {
					y = Double.parseDouble(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[3]);
					return false;
				}

				try {
					z = Double.parseDouble(args[4]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[4]);
					return false;
				}

				try {
					yaw = Float.parseFloat(args[5]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[5]);
					return false;
				}

				try {
					pitch = Float.parseFloat(args[6]);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cInvalid number " + args[6]);
					return false;
				}

				p.teleport(new Location(w, x, y, z, yaw, pitch));
				sender.sendMessage("§aTeleported player " + args[0] + " to world " + args[1]);

			} else {
				sender.sendMessage(
						"§cUsage: /worldtp <worldName> [x] [y] [z] [yaw] [pitch] or /worldtp <playerName> <worldName> [x] [y] [z] [yaw] [pitch]");
				return false;
			}

			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(
					"§cUsage: /worldtp <playerName> <worldName> [x] [y] [z] or /worldtp <playerName> <worldName> [x] [y] [z] [yaw] [pitch]");
			return false;
		}

		Player p = Bukkit.getPlayer(args[0]);

		if (p == null) {
			sender.sendMessage("§cPlayer " + args[0] + " isn't online");
			return false;
		}

		World w = Bukkit.getWorld(args[1]);

		if (w == null) {
			sender.sendMessage("§cCouldn't find world " + args[1]);
			return false;
		}

		if (args.length == 2) {

			p.teleport(w.getSpawnLocation());

			sender.sendMessage("§aTeleported player " + args[0] + " to world " + args[1]);

		} else if (args.length == 5) {

			double x, y, z = 0;

			try {
				x = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[2]);
				return false;
			}

			try {
				y = Double.parseDouble(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[3]);
				return false;
			}

			try {
				z = Double.parseDouble(args[4]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[4]);
				return false;
			}

			p.teleport(new Location(w, x, y, z));
			sender.sendMessage("§aTeleported player " + args[0] + " to world " + args[1]);

		} else if (args.length == 7) {

			double x, y, z = 0;
			float yaw, pitch = 0;

			try {
				x = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[2]);
				return false;
			}

			try {
				y = Double.parseDouble(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[3]);
				return false;
			}

			try {
				z = Double.parseDouble(args[4]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[4]);
				return false;
			}

			try {
				yaw = Float.parseFloat(args[5]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[5]);
				return false;
			}

			try {
				pitch = Float.parseFloat(args[6]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cInvalid number " + args[6]);
				return false;
			}

			p.teleport(new Location(w, x, y, z, yaw, pitch));

			sender.sendMessage("§aTeleported player " + args[0] + " to world " + args[1]);

		} else {
			sender.sendMessage(
					"§cUsage: /worldtp <playerName> <worldName> [x] [y] [z] or /worldtp <playerName> <worldName> [x] [y] [z] [yaw] [pitch]");
			return false;
		}

		return true;
	}

}
