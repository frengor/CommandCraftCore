//  MIT License
//  
//  Copyright (c) 2020 fren_gor
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

package com.fren_gor.commandCraftCore.lines;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.utils.Utils;
import com.fren_gor.commandCraftCore.vars.VariableManager;

import lombok.Getter;

public class CommandLine extends Line {

	@Getter
	private String command;
	@Getter
	private String player;
	@Getter
	private boolean playerCommand = false;
	@Getter
	private boolean negate;

	public CommandLine(Reader reader, int line, String command) {
		super(reader, line);
		Validate.notNull(command, "Command can't be null");
		/*
		 * if (command.length() < 6) { reader.throwError("Invalid command: §f'"
		 * + command + "'"); return; }
		 */
		command = command.trim();
		if (Utils.check(command, "(") && command.contains(")")) {
			int sindex = command.indexOf("/");
			int bindex = command.indexOf("\\");
			int index = sindex == -1 ? bindex : bindex == -1 ? sindex : sindex < bindex ? sindex : bindex;
			String playerName = command.substring(0, index);
			int close = playerName.lastIndexOf(")");

			if (close == -1) {
				reader.throwError("Invalid command: §f'" + command + "'");
				return;
			}

			playerCommand = true;
			command = command.substring(index);

			player = getPlayerFromLine(playerName);

		}

		if (Utils.check(command, "/")) {
			this.command = command.substring(1);
			this.negate = false;
		} else if (Utils.check(command, "\\")) {
			this.command = command.substring(1);
			this.negate = true;
		} else
			reader.throwError("Invalid command: §f'" + command + "'");

	}

	private String getPlayerFromLine(String line) {

		String s = line.trim().substring(1, line.length() - 2).trim();

		if (s.contains("(") || s.contains(")")) {
			reader.throwError("Invalid command: §f'" + command + "'");
			return null;
		}

		return s;

	}

	public boolean execute(VariableManager manager) {

		if (playerCommand) {
			String pl = manager.applyVars(this.player);
			Player player = Bukkit.getPlayer(pl);
			Validate.isTrue(player != null, "Player " + pl + " isn't online, cannot perform any command.");
			return player.performCommand(manager.applyVars(command)) ^ negate;
		}
		return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), manager.applyVars(command)) ^ negate;

	}

	@Override
	public LineType getType() {
		return LineType.COMMAND;
	}

	@Override
	public String toString() {
		return (negate ? "\\" : "/") + (playerCommand ? "(" + player + ") " + command : command);
	}

}
