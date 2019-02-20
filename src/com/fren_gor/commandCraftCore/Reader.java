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

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;

import com.fren_gor.commandCraftCore.LineType.TypeLine;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleMultiHashMap;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleObject;

public class Reader {

	public enum Type {
		COMMAND, EVENT, LOOP;
	}

	private Type type;
	private String name;
	private EventPriority priority;
	private String[] aliseas = new String[0];
	private String permission = "";
	private List<List<String>> tabCompleter = new ArrayList<>();
	private DoubleMultiHashMap<Integer, LineType, String> lines = new DoubleMultiHashMap<>();
	private File file;
	private int length;
	private DoubleObject<Integer, Integer> loopInfo = new DoubleObject<>();
	private String eventName = "";

	public Reader(List<String> l, File f) {

		long time = System.currentTimeMillis();
		file = f;
		int line = 1;
		int last = 0;
		int ifnum = 0;
		boolean b = false;
		boolean isReturned = false;
		length = getLenght(l);
		boolean inWait = false;

		Deque<Integer> deque = new ArrayDeque<>();
		deque.addFirst(-1);
		deque.addFirst(0);

		for (String s : l) {

			s = trim(s);

			if (s.isEmpty())
				continue;

			line++;

			if (s.startsWith("#") || s.isEmpty()) {
				continue;
			}

			s = ChatColor.translateAlternateColorCodes('&', s);

			if (isReturned) {
				Bukkit.getConsoleSender().sendMessage(
						"[CommandCraftCore] §cUnreachable code! §7File: " + f.getPath() + " §6Line: §e" + line);
				lines.clear();
				return;
			}

			if (s.startsWith("$") || s.startsWith("@")) {
				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {
					LineType ll = new LineType(TypeLine.VAR, last);

					lines.put(line, ll, trim(s));

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}
				continue;
			}

			if (s.startsWith("/") || s.startsWith("\\")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {
					lines.put(line, new LineType(TypeLine.COMMAND, last), s);
				} catch (Exception e) {
					e.printStackTrace();
					lines.clear();
					return;
				}

			} else if (s.startsWith("(")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				DoubleObject<LineType, String> cmd = getCommand(s);

				if (cmd != null) {

					try {

						cmd.getKey().i = last;
						lines.put(line, cmd.getKey(), cmd.getValue());

					} catch (Exception e) {
						Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError in §f\'" + s
								+ "\'§c! §7File: " + f.getPath() + " §6Line: §e" + line);
						lines.clear();
						return;
					}

				} else {

					Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cInvalid option §f\'" + s
							+ "\'§c! §7File: " + f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;

				}

			} else if (s.startsWith("!info ")) {

				if (b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cOnly one \'!info\' is admitted! §7File: " + f.getPath()
									+ " §6Line: §e" + line);
					lines.clear();
					return;
				}

				b = true;

				String[] split = s.split(" ");

				if (split.length < 3) {
					Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cInvaild \'!info\': " + s + "! §7File: "
							+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {
					type = Type.valueOf(split[1].toUpperCase());
				} catch (Exception e) {

					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cCannot recognize \'!info\' parameter: \'" + split[1]
									+ "\'! §7File: " + f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;

				}
				if (type == Type.COMMAND) {

					name = split[2];

					if (split.length == 4)
						permission = split[3];

					if (split.length > 4) {
						permission = split[3];
						String[] a = new String[split.length - 4];

						for (int ii = 4; ii < split.length; ii++) {
							a[ii - 4] = split[ii];
						}

						aliseas = a;

					}

				} else if (type == Type.EVENT) {

					name = file.getName().substring(0, file.getName().length() - 4);
					eventName = split[2];

					if (split.length == 4)
						priority = EventPriority.valueOf(split[3].toUpperCase());
					else
						priority = EventPriority.NORMAL;

				} else if (type == Type.LOOP) {

					name = file.getName().substring(0, file.getName().length() - 4);

					try {
						if (split.length == 3) {

							loopInfo.put(Integer.valueOf(split[2]), Integer.MIN_VALUE);

						} else if (split.length == 4) {

							loopInfo.put(Integer.valueOf(split[2]), Integer.valueOf(split[3]));

						} else {
							Bukkit.getConsoleSender()
									.sendMessage("[CommandCraftCore] §cInvaild \'!info\': " + s
											+ " §8(\'!info loop <number> [number]\')§c ! §7File: " + f.getPath()
											+ " §6Line: §e" + line);
							lines.clear();
							return;
						}
					} catch (NumberFormatException e) {

						Bukkit.getConsoleSender()
								.sendMessage("[CommandCraftCore] §cInvaild \'!info\': " + s
										+ " §8(\'!info loop <number> [number]\')§c ! §7File: " + f.getPath()
										+ " §6Line: §e" + line);
						lines.clear();
						return;

					}

					if ((loopInfo.getValue() < 0 && loopInfo.getValue() != Integer.MIN_VALUE)
							|| loopInfo.getKey() < 0) {
						Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cInvaild \'!info\': " + s
								+ ". Numbers must be positives! §7File: " + f.getPath() + " §6Line: §e" + line);
						lines.clear();
						return;
					}

				} else {
					Bukkit.getConsoleSender().sendMessage(
							"[CommandCraftCore] §cInvalid type! §7File: " + f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {
					lines.put(line, new LineType(TypeLine.INFO), s);
				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.startsWith("!tab ")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore the !info only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (type != Type.COMMAND) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §c\'!tab\' is allowed only in command files! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				tabCompleter.clear();

				String[] split = s.split(" ");

				for (int ii = 1; ii < split.length; ii++) {

					String[] ss = split[ii].split(",");
					List<String> list = new ArrayList<>(ss.length);

					for (String st : ss) {

						list.add(st);

					}

					tabCompleter.add(list);

				}

				/*
				 * try { lines.put(line, new LineType(TypeLine.TAB), s); } catch
				 * (Exception e) { Bukkit.getConsoleSender()
				 * .sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
				 * + f.getPath() + " §6Line: §e" + line); lines.clear(); return;
				 * }
				 */

			} else if (s.startsWith("!wait ")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (type != Type.EVENT) {

					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §c\'!wait\' is allowed only in events! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				// TODO
				try {
					int i = Integer.parseInt(s.substring(6));
					LineType t = new LineType(i, last);
					lines.put(line, t, s);
					inWait = true;
				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.startsWith("!if ")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				String ss = s.substring(4);

				if (!(ss.startsWith("/") || ss.startsWith("\\"))) {// TODO
					/*
					 * Bukkit.getConsoleSender()
					 * .sendMessage("[CommandCraftCore] §cInvalid \'!if\' command. There are no \'/\'! §7File: "
					 * + f.getPath() + " §6Line: §e" + line); lines.clear();
					 * return;
					 */

					try {
						int in = ++ifnum;
						deque.addFirst(in);
						LineType ll = new LineType(TypeLine.VAR_IF, last);
						ll.ifnum = ifnum;
						lines.put(line, ll, ss);

						last = in;
					} catch (Exception e) {
						Bukkit.getConsoleSender()
								.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
										+ f.getPath() + " §6Line: §e" + line);
						lines.clear();
						return;
					}

					continue;
				}

				try {
					int in = ++ifnum;
					deque.addFirst(in);
					LineType ll = new LineType(TypeLine.IF, last);
					ll.ifnum = ifnum;
					lines.put(line, ll, ss);

					last = in;
				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!else")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {

					last = last * -1;

					lines.put(line, new LineType(TypeLine.ELSE, last), s);

					deque.removeFirst();
					deque.addFirst(last);

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!break")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (last == 0) {
					Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cToo many \'break\' statements! §7File: "
							+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {

					lines.put(line, new LineType(TypeLine.BREAK, deque.removeFirst()), s);

					last = deque.getFirst();

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!return")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (type == Type.COMMAND) {
					Bukkit.getConsoleSender().sendMessage(
							"[CommandCraftCore] §cOnly in commands \'return_true\' and \'return_false\' are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.startsWith("!loop") || s.equals("!stop") || s.equals("!continue")) {
				// TODO

				Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cLoops aren't supported yet! §7File: "
						+ f.getPath() + " §6Line: §e" + line);
				lines.clear();
				return;

				/*
				 * if (!b) { Bukkit.getConsoleSender()
				 * .sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
				 * + f.getPath() + " §6Line: §e" + line); lines.clear(); return;
				 * }
				 * 
				 * try { ifnum++; int in = ifnum; deque.addFirst(in); LineType
				 * ll = new LineType(TypeLine.LOOP, last); ll.ifnum = ifnum;
				 * lines.put(line, ll, s.substring(6));
				 * 
				 * last = in;
				 * 
				 * } catch (Exception e) { Bukkit.getConsoleSender()
				 * .sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
				 * + f.getPath() + " §6Line: §e" + line); lines.clear(); return;
				 * }
				 */
			} else if (s.startsWith("!var ")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {
					LineType ll = new LineType(TypeLine.VAR, last);

					lines.put(line, ll, trim(s.substring(5)));

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!cancel_false")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (inWait) {
					Bukkit.getConsoleSender().sendMessage(
							"[CommandCraftCore] §cCannot cancel an event after the wait call, skipping this line! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					continue;
				}

				if (type == Type.COMMAND) {
					Bukkit.getConsoleSender().sendMessage(
							"[CommandCraftCore] §c\'!cancel_true\' and \'!cancel_false\' are allowed only in events! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {

					lines.put(line, new LineType(TypeLine.CANCEL_FALSE, last), s);

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!cancel_true")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (inWait) {
					Bukkit.getConsoleSender().sendMessage(
							"[CommandCraftCore] §cCannot uncancel an event after the \'!wait\' call, skipping this line! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					continue;
				}

				if (type == Type.COMMAND) {
					Bukkit.getConsoleSender().sendMessage(
							"[CommandCraftCore] §c\'!cancel_true\' and \'!cancel_false\' are allowed only in events! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {

					lines.put(line, new LineType(TypeLine.CANCEL_TRUE, last), s);

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!return_false")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				if (type == Type.EVENT) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cIn events only 'return\' is allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {

					lines.put(line, new LineType(TypeLine.RETURN_FALSE, deque.removeFirst()), s);

					last = line == length ? 0 : deque.getFirst();

					if (last == -1) {
						isReturned = true;
					}

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else if (s.equalsIgnoreCase("!return_true")) {

				if (!b) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cBefore \'!info\' only comments are allowed! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

				try {

					lines.put(line, new LineType(TypeLine.RETURN_TRUE, deque.removeFirst()), s);

					last = line == length ? 0 : deque.getFirst();

					if (last == -1) {
						isReturned = true;
					}

				} catch (Exception e) {
					Bukkit.getConsoleSender()
							.sendMessage("[CommandCraftCore] §cInvalid inizialization of TypeLine! §7File: "
									+ f.getPath() + " §6Line: §e" + line);
					lines.clear();
					return;
				}

			} else {
				Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cInvalid option §f\'" + s + "\'§c! §7File: "
						+ f.getPath() + " §6Line: §e" + line);
				lines.clear();
				return;
			}

		}

		if (last != 0) {

			if (last != -1 && !lines.getValue1(length).getType().toString().contains("RETURN")) {

				Bukkit.getConsoleSender().sendMessage(
						"[CommandCraftCore] §cUnbalanced \'if\', \'else\', \'break\' and \'return\' statements! §7File: "
								+ f.getPath() + " §6Line: §e" + line);
				lines.clear();
				return;

			}

		}

		if (ConfigManager.isActiveViewExecuteTime()) {

			long ms = System.currentTimeMillis() - time;

			Bukkit.getConsoleSender().sendMessage("Readed file " + f.getName() + " in -> " + ms + " ms");

		}

	}

	public String getEventName() {
		return eventName;
	}

	public DoubleObject<Integer, Integer> getLoopInfo() {
		return loopInfo;
	}

	public int getLength() {
		return length;
	}

	public File getFile() {
		return file;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public EventPriority getPriority() {
		return priority;
	}

	public String[] getAliseas() {
		return aliseas;
	}

	public String getPermission() {
		return permission;
	}

	public List<List<String>> getTabCompleter() {
		return tabCompleter;
	}

	public DoubleMultiHashMap<Integer, LineType, String> getLines() {
		return lines;
	}

	@Nullable
	public static DoubleObject<LineType, String> getCommand(String s) {

		s = trim(s);

		DoubleObject<LineType, String> d = new DoubleObject<>();

		if (s.startsWith("(")) {

			int in = s.indexOf(')');
			/*
			 * char[] c = s.toCharArray(); for (int i = 0; i < c.length; i++) {
			 * if (c[i] == ')') { in = i; break; } }
			 */

			String p = trim(s.substring(1, in));

			String command = trim(s.substring(in + 1));

			if (command.startsWith("/") || command.startsWith("\\")) {

				String cmd = trim(command.substring(1));

				d.put(new LineType(p), cmd);

			} else {
				return null;
			}
		} else if (s.startsWith("/") || s.startsWith("\\")) {

			try {
				d.put(new LineType(TypeLine.COMMAND), s);
			} catch (Exception e) {
				return null;
			}

		} else if (s.startsWith("!if ")) {

			return getCommand(s.substring(4));

		} else if (s.startsWith("!loop ")) {

			return getCommand(s.substring(6));

		} else
			return null;

		return d;

	}

	public static int getLenght(List<String> l) {

		for (int i = l.size() - 1; i >= 0; i--) {

			String s = l.get(i);

			s = trim(s);

			if (!s.isEmpty() && !s.startsWith("#")) {

				return i;

			}

		}

		return 0;

	}

	public static String trim(String s) {
		StringBuilder buil = new StringBuilder(s);
		while (buil.length() > 0 && buil.charAt(0) == ' ') {
			buil.deleteCharAt(0);
		}

		if (buil.length() == 0) {
			return "";
		}

		while (buil.length() > 0 && buil.charAt(0) == '\t') {
			buil.deleteCharAt(0);
		}

		if (buil.length() == 0) {
			return "";
		}

		while (buil.charAt(buil.length() - 1) == ' ') {
			buil.deleteCharAt(buil.length() - 1);
		}

		while (buil.charAt(buil.length() - 1) == '\t') {
			buil.deleteCharAt(buil.length() - 1);
		}
		return buil.toString();
	}

	public static String trim(StringBuilder buil) {
		while (buil.length() > 0 && buil.charAt(0) == ' ') {
			buil.deleteCharAt(0);
		}

		if (buil.length() == 0) {
			return "";
		}

		while (buil.length() > 0 && buil.charAt(0) == '\t') {
			buil.deleteCharAt(0);
		}

		if (buil.length() == 0) {
			return "";
		}

		while (buil.charAt(buil.length() - 1) == ' ') {
			buil.deleteCharAt(buil.length() - 1);
		}

		while (buil.charAt(buil.length() - 1) == '\t') {
			buil.deleteCharAt(buil.length() - 1);
		}
		return buil.toString();
	}

}
