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

package com.fren_gor.commandCraftCore.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitTask;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.ConfigManager;
import com.fren_gor.commandCraftCore.ScriptType;
import com.fren_gor.commandCraftCore.exceptions.ReaderException;
import com.fren_gor.commandCraftCore.reader.controlFlow.ControlFlowStatement;
import com.fren_gor.commandCraftCore.reader.controlFlow.ControlFlowType;
import com.fren_gor.commandCraftCore.reader.controlFlow.ElseStatement;
import com.fren_gor.commandCraftCore.reader.controlFlow.ForEachStatement;
import com.fren_gor.commandCraftCore.reader.controlFlow.ForStatement;
import com.fren_gor.commandCraftCore.reader.controlFlow.IfStatement;
import com.fren_gor.commandCraftCore.reader.controlFlow.WhileStatement;
import com.fren_gor.commandCraftCore.reader.lines.CancelBooleanLine;
import com.fren_gor.commandCraftCore.reader.lines.CancelLine;
import com.fren_gor.commandCraftCore.reader.lines.CommandLine;
import com.fren_gor.commandCraftCore.reader.lines.ForEachLine;
import com.fren_gor.commandCraftCore.reader.lines.GotoLine;
import com.fren_gor.commandCraftCore.reader.lines.IfLine;
import com.fren_gor.commandCraftCore.reader.lines.Line;
import com.fren_gor.commandCraftCore.reader.lines.ReturnBooleanLine;
import com.fren_gor.commandCraftCore.reader.lines.ReturnLine;
import com.fren_gor.commandCraftCore.reader.lines.VarLine;
import com.fren_gor.commandCraftCore.reader.lines.WaitLine;
import com.fren_gor.commandCraftCore.utils.Utils;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleObject;

import lombok.Getter;

public class Reader {

	@Getter
	private ScriptType type = null;
	@Getter
	private String scriptName;
	@Getter
	private EventPriority eventPriority;
	@Getter
	private String[] commandAliseas = new String[0];
	@Getter
	private String commandPermission = "";
	@Getter
	private List<List<String>> tabCompleter = new ArrayList<>();
	@Getter
	// -ExecutionLineNum--Line
	private Map<Integer, Line> lines = new HashMap<>();
	@Getter
	private File file;
	@Getter
	private DoubleObject<Long, Long> loopInfo = new DoubleObject<>();
	@Getter
	private String eventName = "";
	@Getter
	private BukkitTask runnable = null;
	private boolean continueReading = true;
	private int currentLine = 0;

	public void setRunnable(BukkitTask runnable) {
		this.runnable = runnable;
	}

	public Reader(File f) {

		long time = System.currentTimeMillis();
		int executionLine = 0;
		boolean isReturned = false;
		boolean inWait = false;

		file = f;

		List<String> l = null;
		try {
			l = CommandCraftCore.readFile(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Deque<ControlFlowStatement> controlFlow = new ArrayDeque<>();

		Iterator<String> it = l.iterator();

		main: while (continueReading && it.hasNext()) {

			/*
			 * for (ControlFlowStatement c : controlFlow) { if (c.getType() ==
			 * ControlFlowType.WHILE || c.getType() == ControlFlowType.FOR) { if
			 * (!c.getGotoLines().isEmpty()) for (GotoLine g : c.getGotoLines())
			 * { System.out.println(g.getGotoLine()); } }
			 * 
			 * }
			 */

			try {

				String currentString = it.next().trim();

				currentLine++;

				// Checking comments or empty lines
				if (currentString.startsWith("#") || currentString.isEmpty()) {
					continue;
				}

				// Checking !info and !tab before others lines, since they
				// aren't
				// executed and are !info is the first line to put
				if (currentString.startsWith("!info")) {

					if (type != null) {
						throwError("Only one '!info' is admitted");
						return;
					}

					if (!Utils.check(currentString, "!info ")) {
						System.out.println(currentString);
						throwError("Invaild '!info'");
						return;
					}

					String[] split = currentString.split(" ");

					if (split.length < 3) {
						throwError("Invaild '!info' parameters");
						return;
					}

					try {
						type = ScriptType.valueOf(split[1].toUpperCase());
					} catch (Exception e) {
						throwError("Cannot recognize '!info' parameter: '" + split[1] + "'");
						return;
					}

					if (type == ScriptType.COMMAND) {

						scriptName = split[2];

						if (split.length == 4)
							commandPermission = split[3];

						if (split.length > 4) {
							commandPermission = split[3];
							String[] a = new String[split.length - 4];

							for (int ii = 4; ii < split.length; ii++) {
								a[ii - 4] = split[ii];
							}

							commandAliseas = a;
						}

						// Looking for !tab
						int linesToSkip = 0;
						int linesOfCode = 0;
						for (int i = currentLine; i < l.size(); i++) {
							linesToSkip++;
							String li = l.get(i).trim();
							if (li.isEmpty() || li.startsWith("#")) {
								continue;
							}
							linesOfCode++;
							if (li.startsWith("!tab")) {

								if (linesOfCode > 1) {
									throwError(
											"'!tab' must be put right below the '!info' without any code line in between",
											currentLine + linesToSkip);
									return;
								}

								if (!Utils.check(li, "!tab ")) {
									throwError("Invalid '!tab'" + linesToSkip);
									return;
								}

								tabCompleter.clear();

								String[] split1 = li.split(" ");

								for (int ii = 1; ii < split1.length; ii++) {

									String[] ss = split1[ii].split(",");
									List<String> list = new ArrayList<>(ss.length);

									for (String st : ss) {

										list.add(st);

									}

									tabCompleter.add(list);

								}

								// Skip lines to !tab
								for (int n = 0; n < linesToSkip; n++) {
									it.next();
								}

								continue main;
							}
						}

					} else if (type == ScriptType.EVENT) {

						scriptName = file.getName().substring(0, file.getName().length() - 4);
						eventName = split[2];

						if (split.length == 4)
							eventPriority = EventPriority.valueOf(split[3].toUpperCase());
						else
							eventPriority = EventPriority.NORMAL;

					} else if (type == ScriptType.LOOP) {

						scriptName = file.getName().substring(0, file.getName().length() - 4);

						if (!Utils.isInteger(split[2])) {
							throwError("Invaild loop declaration");
							return;
						}

						long n = Long.valueOf(split[2]);

						if (n < 0) {
							throwError("Invaild loop: delay must be positive");
							return;
						}

						if (split.length == 3) {

							loopInfo.put(n, -1L);

							if (loopInfo.getKey() < 0) {
								throwError("Invaild loop: delay must be positive");
								return;
							}

						} else if (split.length == 4) {

							if (!Utils.isInteger(split[3])) {
								throwError("Invaild loop declaration");
								return;
							}

							long nn = Long.valueOf(split[3]);

							if (nn < 0) {
								throwError("Invaild loop: period must be positive");
								return;
							}

							loopInfo.put(n, nn);

							if (loopInfo.getKey() < 0) {
								throwError("Invaild loop: delay must be positive");
								return;
							}

							if (loopInfo.getValue() < 0) {
								throwError("Invaild loop: period must be positive");
								return;
							}

						} else {
							throwError("Invaild loop declaration");
							return;
						}

					} else {
						throwError("Invalid '!info' type");
						return;
					}

					continue;
				}

				if (type == null) {
					throwError("Before '!info' only comments are allowed");
					return;
				}

				// Already readed in '!info command' part
				if (currentString.startsWith("!tab")) {

					if (type != ScriptType.COMMAND) {
						throwError("'!tab' is allowed only in command scripts");
						return;
					}

					throwError("Only one '!tab' per script is allowed");
					return;
				}

				executionLine++;

				if (currentString.startsWith("!elseif")) {

					if (!Utils.check(currentString, "!elseif ")) {
						throwError("Invalid '!elseif'");
						return;
					}

					if (controlFlow.size() == 0 || controlFlow.getLast().getType() != ControlFlowType.IF) {
						throwError("There must be an '!if' statement before an '!elseif'");
						return;
					}

					IfLine line = new IfLine(this, currentLine, currentString.substring(8).trim());
					GotoLine g = new GotoLine(this, currentLine);

					IfStatement old = (IfStatement) controlFlow.pollLast();

					controlFlow.add(new IfStatement(line, g, old.getGotoLines()));

					lines.put(executionLine, g);
					lines.put(++executionLine, line);

					old.setElseLine(executionLine);

					isReturned = false;

					continue;
				}

				if (currentString.equals("!else")) {

					if (controlFlow.size() == 0 || controlFlow.getLast().getType() != ControlFlowType.IF) {
						throwError("There must be an '!if' or an '!elseif' statement before an '!else'");
						return;
					}

					GotoLine g = new GotoLine(this, currentLine);

					IfStatement old = (IfStatement) controlFlow.pollLast();

					controlFlow.add(new ElseStatement(g, old.getGotoLines()));

					lines.put(executionLine, g);

					old.setElseLine(executionLine + 1);

					isReturned = false;

					continue;
				}

				if (currentString.equals("!continue")) {

					if (controlFlow.size() == 0) {
						throwError("'!continue' must be inside a '!while' or '!for' loop");
						return;
					}

					Iterator<ControlFlowStatement> iterator = controlFlow.descendingIterator();
					while (iterator.hasNext()) {
						ControlFlowStatement c = iterator.next();

						if (c.getType() != ControlFlowType.WHILE && c.getType() != ControlFlowType.FOR)
							continue;

						GotoLine g = new GotoLine(this, currentLine);

						if (c.getType() == ControlFlowType.WHILE) {
							g.setGotoLine(((WhileStatement) c).getExecutionLine());
						} else if (c.getType() == ControlFlowType.FOR) {
							g.setGotoLine(((ForStatement) c).getExecutionLine());
						}

						lines.put(executionLine, g);
						isReturned = true;
						continue main;

					}

					throwError("'!continue' must be inside a '!while' or '!for' loop");
					return;
				}

				if (currentString.equals("!end")) {

					if (controlFlow.size() == 0) {
						throwError("'!continue' must be inside a '!while' or '!for' loop");
						return;
					}

					Iterator<ControlFlowStatement> iterator = controlFlow.descendingIterator();
					while (iterator.hasNext()) {
						ControlFlowStatement c = iterator.next();

						if (c.getType() != ControlFlowType.WHILE && c.getType() != ControlFlowType.FOR)
							continue;

						GotoLine g = new GotoLine(this, currentLine);

						c.getGotoLines().add(g);

						lines.put(executionLine, g);
						isReturned = true;
						continue main;

					}

					throwError("'!continue' must be inside a '!while' or '!for' loop");
					return;
				}

				if (currentString.equals("!break")) {

					if (controlFlow.size() == 0) {
						throwError("Unbalanced '!break's");
						return;
					}

					ControlFlowStatement c = controlFlow.pollLast();

					if (c.getType() == ControlFlowType.IF) {
						((IfStatement) c).setElseLine(executionLine);
					} else if (c.getType() == ControlFlowType.WHILE) {

						WhileStatement w = (WhileStatement) c;
						GotoLine g = new GotoLine(this, currentLine);
						g.setGotoLine(w.getExecutionLine());
						lines.put(executionLine++, g);

						w.setElseLine(executionLine);

					} else if (c.getType() == ControlFlowType.FOR) {

						ForStatement fs = (ForStatement) c;
						if (!fs.getIncrement().getVar().isEmpty())
							lines.put(executionLine++, fs.getIncrement());
						GotoLine g = new GotoLine(this, currentLine);
						g.setGotoLine(fs.getExecutionLine());
						lines.put(executionLine++, g);
						fs.setElseLine(executionLine);

					} else if (c.getType() == ControlFlowType.FOREACH) {

						ForEachStatement w = (ForEachStatement) c;
						GotoLine g = new GotoLine(this, currentLine);
						g.setGotoLine(w.getExecutionLine());
						lines.put(executionLine++, g);

						w.setGotoLine(executionLine);

					}

					if (!c.getGotoLines().isEmpty())
						for (GotoLine g : c.getGotoLines()) {
							g.setGotoLine(executionLine);
						}

					executionLine--;

					isReturned = false;

					continue;
				}

				if (isReturned) {
					throwError("Unreachable line");
					return;
				}

				if (currentString.startsWith("!if")) {

					if (!Utils.check(currentString, "!if ")) {
						throwError("Invalid '!if'");
						return;
					}

					IfLine line = new IfLine(this, currentLine, currentString.substring(4).trim());

					controlFlow.add(new IfStatement(line));

					lines.put(executionLine, line);

					isReturned = false;

					continue;
				}

				if (currentString.startsWith("!while")) {

					if (!Utils.check(currentString, "!while ")) {
						throwError("Invalid '!while'");
						return;
					}

					IfLine line = new IfLine(this, currentLine, currentString.substring(7).trim());

					controlFlow.add(new WhileStatement(line, executionLine));

					lines.put(executionLine, line);

					continue;
				}

				if (currentString.startsWith("!for")) {

					if (!Utils.check(currentString, "!for ")) {
						throwError("Invalid '!for'");
						return;
					}

					// Foreach
					if (currentString.contains(" : ")) {

						String[] parts = currentString.substring(5).trim().split(" : ");

						if (parts.length != 2) {
							throwError("Invalid foreach");
							return;
						}

						ForEachLine line = new ForEachLine(this, currentLine, parts[0], parts[1]);

						controlFlow.add(new ForEachStatement(line, executionLine));

						lines.put(executionLine, line);

						continue;
					}

					String[] parts = currentString.substring(5).trim().split(" ; ");

					if (parts.length != 3) {
						throwError("Invalid '!for'");
						return;
					}

					IfLine line = new IfLine(this, currentLine, parts[1].trim().isEmpty() ? "true" : parts[1]);

					String pre = parts[0].trim();

					controlFlow.add(new ForStatement(line, new VarLine(this, currentLine, parts[2]),
							pre.isEmpty() ? executionLine : executionLine + 1));

					if (!pre.isEmpty()) {
						lines.put(executionLine, new VarLine(this, currentLine, parts[0]));
					} else
						lines.put(++executionLine, line);

					continue;
				}

				if (currentString.startsWith("/") || currentString.startsWith("\\") || currentString.startsWith("(")) {

					lines.put(executionLine, new CommandLine(this, currentLine, currentString));

					continue;
				}

				if (currentString.startsWith("!var") || currentString.startsWith("$")
						|| currentString.startsWith("@")) {

					lines.put(executionLine, new VarLine(this, currentLine, currentString));

					continue;
				}

				if (currentString.equals("!return")) {
					lines.put(executionLine, new ReturnLine(this, currentLine));
					isReturned = true;
					continue;
				}
				if (currentString.equals("!return_true")) {
					lines.put(executionLine, new ReturnBooleanLine(this, currentLine, true));
					isReturned = true;
					continue;
				}
				if (currentString.equals("!return_false")) {
					lines.put(executionLine, new ReturnBooleanLine(this, currentLine, false));
					isReturned = true;
					continue;
				}

				if (currentString.equals("!cancel")) {
					if (inWait) {
						throwError("Cannot use '!cancel' after '!wait'");
					}
					lines.put(executionLine, new CancelLine(this, currentLine));
					continue;
				}
				if (currentString.equals("!cancel_true")) {
					if (inWait) {
						throwError("Cannot use '!cancel_true' after '!wait'");
					}
					lines.put(executionLine, new CancelBooleanLine(this, currentLine, true));
					continue;
				}
				if (currentString.equals("!cancel_false")) {
					if (inWait) {
						throwError("Cannot use '!cancel_false' after '!wait'");
					}
					lines.put(executionLine, new CancelBooleanLine(this, currentLine, true));
					continue;
				}

				if (currentString.startsWith("!wait")) {

					if (!Utils.check(currentString, "!wait ")) {
						throwError("Invalid '!wait'");
						return;
					}

					String ticks = currentString.substring(6).trim();

					if (!Utils.isInteger(ticks)) {
						throwError("Invalid '!wait' ticks");
						return;
					}

					inWait = true;

					lines.put(executionLine, new WaitLine(this, currentLine, Long.parseLong(ticks)));
					continue;
				}

				throwError("Can't recognize '" + currentString + "'");
				return;

			} catch (ReaderException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				throwError(e.getMessage());
				return;
			}

		}

		if (!continueReading) {
			return;
		}

		if (controlFlow.size() != 0) {
			if (controlFlow.size() == 1)
				throwError("A '!break' keyword is missing");
			else
				throwError(controlFlow.size() + " '!break' keywords are missing");
			return;
		}

		if (type == ScriptType.COMMAND && !checkCommandReturn(1)) {
			throwError("Commands must return true or false");
			return;
		}

		if (ConfigManager.isActiveViewReadingTime()) {

			long ms = System.currentTimeMillis() - time;

			Bukkit.getConsoleSender().sendMessage("§eReaded file §7" + f.getName() + " §ein §7" + ms + " ms");

		}

		Bukkit.getLogger().info(toString());

	}

	private boolean checkCommandReturn(int startLine) {
		for (int line = startLine; line <= lines.size(); line++) {
			Line ll = lines.get(line);
			if (ll == null) {
				continue;
			}
			if (ll instanceof IfLine) {
				IfLine ifline = (IfLine) ll;
				if (!checkCommandReturn(ifline.getElseLine())) {
					return false;
				}
			} else if (ll instanceof GotoLine) {
				int l = ((GotoLine) ll).getGotoLine() - 1;
				if (l < line) {
					return true;
				}
				line = l;
				continue;
			} else if (ll instanceof ReturnBooleanLine) {
				return true;
			}
		}
		return false;

	}

	public void throwError(String error) {
		continueReading = false;
		lines.clear();
		Utils.printRidingError(error, file.getPath(), currentLine);
	}

	public void throwError(String error, int line) {
		continueReading = false;
		lines.clear();
		Utils.printRidingError(error, file.getPath(), line);
	}

	/**
	 * Useless, may be slow
	 * 
	 * @deprecated
	 * @param list
	 *            A list containing a file's lines
	 * @return Number of !if, !elseif, !else of the script
	 */
	@Deprecated
	public static int getControlFlowLenght(List<String> list) {

		int count = 0;

		for (int i = list.size() - 1; i >= 0; i--) {

			String s = list.get(i).trim().toLowerCase();

			if (s.startsWith("!if") || s.startsWith("!elseif") || s.startsWith("!else")) {
				count++;
			}

		}

		return count;

	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Script Type: ");
		b.append(type.toString());
		b.append("\n");
		for (Entry<Integer, Line> e : lines.entrySet()) {

			b.append(e.getKey());
			b.append(" -> ");
			b.append(e.getValue().toString());
			b.append(" (");
			b.append(e.getValue().getClass().getSimpleName());
			b.append(")");
			b.append("\n");

		}
		return b.toString();
	}

}
