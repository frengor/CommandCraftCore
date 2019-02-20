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

package com.fren_gor.commandCraftCore;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LineType {

	public TypeLine getType() {
		return type;
	}

	public int getInt() {
		return i;
	}

	public enum TypeLine {
		COMMAND, RETURN, BREAK, IF, ELSE, INFO, TAB, PLAYERCOMMAND, RETURN_TRUE, RETURN_FALSE, CANCEL_TRUE, CANCEL_FALSE, WAIT, VAR_IF,

		// planned
		LOOP, VAR, STOP;
	}

	private final TypeLine type;
	int i;
	int wait;
	private final String player;
	int ifnum = 0;
	String variableName;

	public LineType(TypeLine type, int i) throws Exception {

		if (type == TypeLine.BREAK || type == TypeLine.ELSE || type == TypeLine.IF || type == TypeLine.RETURN
				|| type == TypeLine.COMMAND || type == TypeLine.RETURN_TRUE || type == TypeLine.RETURN_FALSE
				|| type == TypeLine.CANCEL_FALSE || type == TypeLine.CANCEL_TRUE || type == TypeLine.VAR
				|| type == TypeLine.VAR_IF) {

			this.type = type;
			this.i = i;
			player = "";
			return;
		}

		throw new Exception("Invalid inizialization of TypeLine");

	}

	public LineType(int wait, int i) {
		type = TypeLine.WAIT;
		player = "";
		this.i = i;
		this.wait = wait;
	}

	public LineType(String player) {
		type = TypeLine.PLAYERCOMMAND;
		this.player = player;
		i = 0;
	}

	public LineType(String player, int i) {
		type = TypeLine.PLAYERCOMMAND;
		this.player = player;
		this.i = i;
	}

	public LineType(TypeLine type) throws Exception {

		if (type == TypeLine.INFO || type == TypeLine.TAB || type == TypeLine.COMMAND) {
			this.type = type;
			this.i = 0;
			player = "";
			return;
		}

		throw new Exception("Invalid inizialization of TypeLine");

	}

	public String getPlayerName() {
		return player;
	}

	@Nullable
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("type = ");
		b.append(type.toString());
		b.append(", i = ");
		b.append(i);
		b.append(", player = ");
		b.append(player);
		b.append(", ifnum = ");
		b.append(ifnum);
		return b.toString();
	}

}
