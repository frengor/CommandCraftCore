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

package com.fren_gor.commandCraftCore.executor;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.ConfigManager;
import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.ScriptType;
import com.fren_gor.commandCraftCore.lines.CancelBooleanLine;
import com.fren_gor.commandCraftCore.lines.CommandLine;
import com.fren_gor.commandCraftCore.lines.GotoLine;
import com.fren_gor.commandCraftCore.lines.IfLine;
import com.fren_gor.commandCraftCore.lines.Line;
import com.fren_gor.commandCraftCore.lines.ReturnBooleanLine;
import com.fren_gor.commandCraftCore.lines.VarLine;
import com.fren_gor.commandCraftCore.lines.WaitLine;
import com.fren_gor.commandCraftCore.vars.BooleanVar;
import com.fren_gor.commandCraftCore.vars.VariableManager;

import lombok.Getter;

public class Executor {

	public static final Result DEFAULT = new Result();

	@Getter
	private final VariableManager manager;
	private final Reader reader;
	private final Map<Integer, Line> lines;

	public Executor(final Reader reader) {
		this.reader = reader;
		lines = reader.getLines();
		manager = new VariableManager();
	}

	public boolean execute() {
		return execute(false);
	}

	public boolean execute(boolean isAlreadyCancelled) {

		boolean cancelled = isAlreadyCancelled;

		long time = System.currentTimeMillis();

		for (CurrentLine currentLine = new CurrentLine(); currentLine.getCurrentLine() <= lines.size(); currentLine
				.increase()) {

			Line line = lines.get(currentLine.getCurrentLine());

			Result res = subExecute(line, currentLine);
			if (res.isReturned()) {
				cancelled = res.getValue();
				break;
			}
			if (res.isCancelled()) {
				cancelled = res.getValue();
			}

		}

		if (ConfigManager.isActiveViewExecutingTime()
				&& (!ConfigManager.ignoreLoop() || reader.getType() != ScriptType.LOOP)) {
			long ms = System.currentTimeMillis() - time;

			Bukkit.getConsoleSender()
					.sendMessage("§eExecuted file §7" + reader.getScriptName() + " §ein §7" + ms + " ms");
		}

		return cancelled;

	}

	public Result subExecute(Line line, CurrentLine currentLine) {

		switch (line.getType()) {
			case GOTO: {
				currentLine.setCurrentLine(((GotoLine) line).getGotoLine());
				break;
			}
			case IF: {
				IfLine ifline = (IfLine) line;
				try {
					if (!ifline.execute(manager) && ifline.hasElse()) {
						currentLine.setCurrentLine(ifline.getElseLine());
					}
				} catch (IllegalArgumentException e) {
					Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + e.getMessage()
							+ "§c! §7File: " + reader.getFile().getPath() + " §6Line: §e" + line);
					e.printStackTrace();
				}
				break;
			}
			case COMMAND: {
				((CommandLine) line).execute(manager);
				break;
			}
			case VAR: {
				try {
					((VarLine) line).execute(manager);
				} catch (IllegalArgumentException e) {
					Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + e.getMessage()
							+ "§c! §7File: " + reader.getFile().getPath() + " §6Line: §e" + line);
					e.printStackTrace();
				}
				break;
			}
			case CANCEL: {
				reader.getRunnable().cancel();
				return new Result(false, true, true);
			}
			case CANCEL_BOOLEAN: {
				boolean value = ((CancelBooleanLine) line).isCancelled();
				if (reader.getType() == ScriptType.EVENT) {
					manager.getVars().remove("$cancelled");
					new BooleanVar(manager, "$cancelled", value);
				}
				return new Result(false, true, value);
			}
			case RETURN: {
				return new Result(true, false, false);
			}
			case RETURN_BOOLEAN: {
				return new Result(true, false, ((ReturnBooleanLine) line).getValueToReturn());
			}
			case WAIT: {
				if (reader.getType() == ScriptType.LOOP)
					reader.getRunnable().cancel();
				new BukkitRunnable() {
					@Override
					public void run() {

						for (; currentLine.getCurrentLine() <= lines.size(); currentLine.increase()) {

							Line line = lines.get(currentLine.getCurrentLine());

							Result res = subExecute(line, currentLine);

							if (res.isReturned())
								return;

						}

					}
				}.runTaskLater(CommandCraftCore.getInstance(), ((WaitLine) line).getTicksToWait());
				return new Result(true, false, false);
			}
			default:
				break;
		}

		return DEFAULT;

	}

}
