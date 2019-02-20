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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.commandCraftCore.LineType.TypeLine;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleMultiHashMap;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleObject;
import com.fren_gor.commandCraftCore.vars.BooleanVar;
import com.fren_gor.commandCraftCore.vars.Type;
import com.fren_gor.commandCraftCore.vars.Variable;
import com.fren_gor.commandCraftCore.vars.VariableManager;

import lombok.Getter;

public class Executor {

	@Getter
	private final VariableManager manager = new VariableManager();

	@Getter
	private static List<File> hystory = new ArrayList<>();

	public boolean execute(final Reader r) {

		DoubleMultiHashMap<Integer, LineType, String> l = r.getLines();

		// System.out.println(l.toString());

		hystory.add(r.getFile());

		Deque<Integer> deque = new ArrayDeque<>();
		deque.addFirst(0);
		boolean toReturn = false;
		int loopLine = -1;
		long time = System.currentTimeMillis();

		Iterator<Entry<Integer, DoubleObject<LineType, String>>> it = l.getEntryIterator();

		if (ConfigManager.isActiveDebugMode()) {

			while (it.hasNext()) {

				Entry<java.lang.Integer, DoubleObject<LineType, String>> entry = it.next();
				int i = entry.getKey();
				String sl = entry.getValue().getValue();
				LineType t = entry.getValue().getKey();

				switch (t.getType()) {

					case LOOP:

						if (t.getInt() == deque.getFirst()) {

						}

						break;

					case COMMAND:

						if (t.getInt() == deque.getFirst()) {

							Bukkit.getConsoleSender().sendMessage("§6Debug: §5command §7" + sl + " §e-> "
									+ (invokeCommand(applyVars(sl.substring(1))) ? "§atrue" : "§cfalse"));

						}

						break;

					case PLAYERCOMMAND:

						if (t.getInt() == deque.getFirst()) {

							Player p = Bukkit.getPlayer(applyVars(t.getPlayerName()));

							if (p == null) {

								Bukkit.getConsoleSender()
										.sendMessage("[CommandCraftCore] §cPlayer " + applyVars(t.getPlayerName())
												+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e"
												+ i);

								return false;
							}

							Bukkit.getConsoleSender().sendMessage("§6Debug: §5player command §7" + sl + " §e-> "
									+ (invokePlayerCommand(applyVars(sl), p) ? "§atrue" : "§cfalse"));

						}

						break;

					case IF:

						if (t.getInt() != deque.getFirst()) {
							continue;
						}

						DoubleObject<LineType, String> c = Reader.getCommand(applyVars(sl));

						boolean b;

						if (c.getKey().getType() == TypeLine.COMMAND) {

							b = invokeCommand(c.getValue().substring(1));

						} else {
							Player p = Bukkit.getPlayer(c.getKey().getPlayerName());
							if (p == null) {

								Bukkit.getConsoleSender()
										.sendMessage("[CommandCraftCore] §cPlayer " + c.getKey().getPlayerName()
												+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e"
												+ i);

								return false;
							}

							b = invokePlayerCommand(c.getValue().substring(1), p);
						}

						Bukkit.getConsoleSender()
								.sendMessage("§6Debug: §5if §7" + sl + " §e-> " + (b ? "§atrue" : "§cfalse"));

						if (c.getValue().startsWith("\\")) {
							Bukkit.getConsoleSender().sendMessage("§6Debug: §echange if value: "
									+ (b ? "§atrue" : "§cfalse") + " §e-> " + (!b ? "§atrue" : "§cfalse"));
							b = !b;
						}

						if (b)
							deque.addFirst(t.ifnum);
						else
							deque.addFirst(t.ifnum * -1);

						break;

					case VAR_IF:

						if (t.getInt() != deque.getFirst()) {
							continue;
						}

						Variable v = manager.execute(sl);

						if (v.getType() != Type.BOOLEAN) {
							throw new IllegalArgumentException(
									"'!if' statements support only commands or boolean variables");
						}

						if ((boolean) v.get())
							deque.addFirst(t.ifnum);
						else
							deque.addFirst(t.ifnum * -1);

						Bukkit.getConsoleSender().sendMessage(
								"§6Debug: §5if §7" + sl + " §e-> " + ((boolean) v.get() ? "§atrue" : "§cfalse"));

						break;

					case WAIT:
						if (t.getInt() == deque.getFirst()) {
							Bukkit.getConsoleSender().sendMessage("§eDebug: §5wait §e(" + t.wait
									+ " ticks), cancelled: " + (toReturn ? "§atrue" : "§cfalse"));
							Wait(t.wait, it, r, l, deque, toReturn, loopLine, time);
						}
						return toReturn;

					case VAR:

						if (t.getInt() != deque.getFirst()) {
							continue;
						}

						try {
							manager.execute(sl);
						} catch (Exception e) {
							Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + e.getMessage()
									+ "! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);
							e.printStackTrace();

							return false;
						}
						Bukkit.getConsoleSender().sendMessage("Executed statement -> '" + sl + "'");

						break;

					case BREAK:

						if (loopLine != -1) {
							i = loopLine;
							continue;
						}

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst())) {
							deque.removeFirst();
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug §5break");

						break;
					case RETURN:

						int f = deque.getFirst();

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
							deque.removeFirst();
						}

						if (t.getInt() != f) {
							continue;
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug §5return");

						if (ConfigManager.isActiveViewExecuteTime()) {

							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}

						return toReturn;

					case RETURN_TRUE:

						f = deque.getFirst();

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
							deque.removeFirst();
						}

						if (t.getInt() != f) {
							continue;
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug §5return true");

						if (ConfigManager.isActiveViewExecuteTime()) {

							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}

						return true;

					case RETURN_FALSE:

						f = deque.getFirst();

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
							deque.removeFirst();
						}

						if (t.getInt() != f) {
							continue;
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug §5return false");

						if (ConfigManager.isActiveViewExecuteTime()) {

							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}

						return false;

					case CANCEL_TRUE:

						if (t.getInt() == deque.getFirst()) {
							toReturn = true;
							// !cancel_true prevented in commands and loops
							// if (r.getType() ==
							// com.fren_gor.commandCraftCore.Reader.Type.EVENT)
							// {
							manager.getVars().remove("$cancelled");
							new BooleanVar(manager, "$cancelled", true);
							// }
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug §5cancel §e-> §atrue");

						break;
					case CANCEL_FALSE:

						if (t.getInt() == deque.getFirst()) {
							toReturn = false;
							// !cancel_true prevented in commands and loops
							// if (r.getType() ==
							// com.fren_gor.commandCraftCore.Reader.Type.EVENT)
							// {
							manager.getVars().remove("$cancelled");
							new BooleanVar(manager, "$cancelled", false);
							// }
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug §5cancel §e-> §cfalse");

						break;

					default:
						continue;
				}
			}

		} else {

			while (it.hasNext()) {

				Entry<java.lang.Integer, DoubleObject<LineType, String>> entry = it.next();
				int i = entry.getKey();
				String sl = entry.getValue().getValue();
				LineType t = entry.getValue().getKey();

				switch (t.getType()) {

					case COMMAND:
						if (t.getInt() == deque.getFirst()) {

							invokeCommand(applyVars(sl.substring(1)));

						}

						break;

					case PLAYERCOMMAND:

						if (t.getInt() == deque.getFirst()) {

							Player p = Bukkit.getPlayer(applyVars(t.getPlayerName()));

							if (p == null) {

								Bukkit.getConsoleSender()
										.sendMessage("[CommandCraftCore] §cPlayer " + applyVars(t.getPlayerName())
												+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e"
												+ i);

								return false;
							}

							invokePlayerCommand(applyVars(sl), p);

						}

						break;

					// TODO
					/*
					 * case LOOP:
					 * 
					 * if (t.getInt() != deque.getFirst()) { continue; }
					 * 
					 * DoubleObject<LineType, String> d =
					 * Reader.getCommand(applyVars(sl));
					 * 
					 * boolean bool;
					 * 
					 * if (d.getKey().getType() == TypeLine.COMMAND) {
					 * 
					 * bool = Bukkit.getServer().dispatchCommand(Bukkit.
					 * getConsoleSender(), d.getValue().substring(1));
					 * 
					 * } else { Player p =
					 * Bukkit.getPlayer(d.getKey().getPlayerName()); if (p ==
					 * null) {
					 * 
					 * Bukkit.getConsoleSender()
					 * .sendMessage("[CommandCraftCore] §cPlayer " +
					 * d.getKey().getPlayerName() + " isn't online! §7File: " +
					 * r.getFile().getPath() + " §6Line: §e" + i);
					 * 
					 * return false; }
					 * 
					 * bool = p.performCommand(d.getValue().substring(1)); }
					 * 
					 * if (bool) { loopLine = i - 1; deque.addFirst(t.ifnum); }
					 * else { loopLine = -1; }
					 * 
					 * break;
					 */

					case WAIT:
						if (t.getInt() == deque.getFirst()) {
							Wait(t.wait, it, r, l, deque, toReturn, loopLine, time);
						}
						return toReturn;

					case IF:

						if (t.getInt() != deque.getFirst()) {
							continue;
						}

						DoubleObject<LineType, String> c = Reader.getCommand(applyVars(sl));

						boolean b;

						if (c.getKey().getType() == TypeLine.COMMAND) {

							b = invokeCommand(c.getValue().substring(1));

						} else {
							Player p = Bukkit.getPlayer(c.getKey().getPlayerName());
							if (p == null) {

								Bukkit.getConsoleSender()
										.sendMessage("[CommandCraftCore] §cPlayer " + c.getKey().getPlayerName()
												+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e"
												+ i);

								return false;
							}

							b = invokePlayerCommand(c.getValue().substring(1), p);
						}

						if (c.getValue().startsWith("\\")) {
							b = !b;
						}

						if (b)
							deque.addFirst(t.ifnum);
						else
							deque.addFirst(t.ifnum * -1);

						break;

					case VAR_IF:

						if (t.getInt() != deque.getFirst()) {
							continue;
						}

						Variable v = manager.execute(sl);

						if (v.getType() != Type.BOOLEAN) {
							throw new IllegalArgumentException(
									"'!if' statements support only commands or boolean variables");
						}

						if ((boolean) v.get())
							deque.addFirst(t.ifnum);
						else
							deque.addFirst(t.ifnum * -1);

						break;

					case VAR:

						if (t.getInt() != deque.getFirst()) {
							continue;
						}

						try {
							manager.execute(sl);
						} catch (Exception e) {
							Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + e.getMessage()
									+ "! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);
							e.printStackTrace();

							return false;
						}
						break;

					case BREAK:

						if (loopLine != -1) {
							i = loopLine;
							continue;
						}

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst())) {
							deque.removeFirst();
						}

						break;
					case RETURN:

						int f = deque.getFirst();

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
							deque.removeFirst();
						}

						if (t.getInt() != f) {
							continue;
						}

						if (ConfigManager.isActiveViewExecuteTime()) {

							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}

						return toReturn;

					case RETURN_TRUE:

						f = deque.getFirst();

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
							deque.removeFirst();
						}

						if (t.getInt() != f) {
							continue;
						}

						if (ConfigManager.isActiveViewExecuteTime()) {

							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}

						return true;

					case RETURN_FALSE:

						f = deque.getFirst();

						if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
							deque.removeFirst();
						}

						if (t.getInt() != f) {
							continue;
						}

						if (ConfigManager.isActiveViewExecuteTime()) {

							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}

						return false;

					case CANCEL_TRUE:

						if (t.getInt() == deque.getFirst()) {
							toReturn = true;
							// !cancel_true prevented in commands and loops
							// if (r.getType() ==
							// com.fren_gor.commandCraftCore.Reader.Type.EVENT)
							// {
							manager.getVars().remove("$cancelled");
							new BooleanVar(manager, "$cancelled", true);
							// }
						}

						break;
					case CANCEL_FALSE:

						if (t.getInt() == deque.getFirst()) {
							toReturn = false;
							// !cancel_true prevented in commands and loops
							// if (r.getType() ==
							// com.fren_gor.commandCraftCore.Reader.Type.EVENT)
							// {
							manager.getVars().remove("$cancelled");
							new BooleanVar(manager, "$cancelled", false);
							// }
						}

						break;

					default:
						continue;
				}

			}
		}

		if (ConfigManager.isActiveViewExecuteTime()) {

			long ms = System.currentTimeMillis() - time;

			Bukkit.getConsoleSender().sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
					+ r.getName() + " in -> " + ms + " ms");

		}

		return toReturn;

	}

	private void Wait(int late, Iterator<Entry<Integer, DoubleObject<LineType, String>>> it, Reader r,
			DoubleMultiHashMap<Integer, LineType, String> l, Deque<Integer> deque, boolean toReturn, int loopLine,
			long time) {
		new BukkitRunnable() {
			@Override
			public void run() {
				while (it.hasNext()) {
					Entry<java.lang.Integer, DoubleObject<LineType, String>> entry = it.next();
					int i = entry.getKey();
					String sl = entry.getValue().getValue();
					LineType t = entry.getValue().getKey();
					if (t.getType() == TypeLine.WAIT) {
						Wait(t.wait, it, r, l, deque, toReturn, loopLine, time);
						cancel();
						return;
					}
					if (runExecute(r, i, t, sl, deque, toReturn, loopLine, time)) {
						if (ConfigManager.isActiveViewExecuteTime()) {
							long ms = System.currentTimeMillis() - time;

							Bukkit.getConsoleSender()
									.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
											+ r.getName() + " in -> " + ms + " ms");

						}
						cancel();
						return;
					}
				}
				if (ConfigManager.isActiveViewExecuteTime()) {
					long ms = System.currentTimeMillis() - time;

					Bukkit.getConsoleSender().sendMessage("Executed " + StringUtils.capitalize(r.getType().toString())
							+ " " + r.getName() + " in -> " + ms + " ms");

				}
			}
		}.runTaskLater(CommandCraftCore.getInstance(), late);
	}

	// TODO
	private boolean runExecute(Reader r, int i, LineType t, String sl, Deque<Integer> deque, boolean toReturn,
			int loopLine, long time) {

		if (ConfigManager.isActiveDebugMode()) {

			switch (t.getType()) {

				case LOOP:

					if (t.getInt() == deque.getFirst()) {

					}

					break;

				case COMMAND:

					if (t.getInt() == deque.getFirst()) {

						Bukkit.getConsoleSender().sendMessage("§6Debug: §5command §7" + sl + " §e-> "
								+ (invokeCommand(applyVars(sl.substring(1))) ? "§atrue" : "§cfalse"));

					}

					break;

				case PLAYERCOMMAND:

					if (t.getInt() == deque.getFirst()) {

						Player p = Bukkit.getPlayer(applyVars(t.getPlayerName()));

						if (p == null) {

							Bukkit.getConsoleSender()
									.sendMessage("[CommandCraftCore] §cPlayer " + applyVars(t.getPlayerName())
											+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);

							return false;
						}

						Bukkit.getConsoleSender().sendMessage("§6Debug: §5player command §7" + sl + " §e-> "
								+ (invokePlayerCommand(applyVars(sl), p) ? "§atrue" : "§cfalse"));

					}

					break;

				case IF:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					DoubleObject<LineType, String> c = Reader.getCommand(applyVars(sl));

					boolean b;

					if (c.getKey().getType() == TypeLine.COMMAND) {

						b = invokeCommand(c.getValue().substring(1));

					} else {
						Player p = Bukkit.getPlayer(c.getKey().getPlayerName());
						if (p == null) {

							Bukkit.getConsoleSender()
									.sendMessage("[CommandCraftCore] §cPlayer " + c.getKey().getPlayerName()
											+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);

							return false;
						}

						b = invokePlayerCommand(c.getValue().substring(1), p);
					}

					Bukkit.getConsoleSender()
							.sendMessage("§6Debug: §5if §7" + sl + " §e-> " + (b ? "§atrue" : "§cfalse"));

					if (c.getValue().startsWith("\\")) {
						Bukkit.getConsoleSender().sendMessage("§6Debug: §echange if value: "
								+ (b ? "§atrue" : "§cfalse") + " §e-> " + (!b ? "§atrue" : "§cfalse"));
						b = !b;
					}

					if (b)
						deque.addFirst(t.ifnum);
					else
						deque.addFirst(t.ifnum * -1);

					break;

				case VAR_IF:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					Variable v = manager.execute(sl);

					if (v.getType() != Type.BOOLEAN) {
						throw new IllegalArgumentException(
								"'!if' statements support only commands or boolean variables");
					}

					if ((boolean) v.get())
						deque.addFirst(t.ifnum);
					else
						deque.addFirst(t.ifnum * -1);

					Bukkit.getConsoleSender().sendMessage(
							"§6Debug: §5if §7" + sl + " §e-> " + ((boolean) v.get() ? "§atrue" : "§cfalse"));

					break;

				case VAR:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					try {
						manager.execute(sl);
					} catch (Exception e) {
						Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + e.getMessage()
								+ "! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);
						e.printStackTrace();

						return false;
					}
					Bukkit.getConsoleSender().sendMessage("Executed statement -> '" + sl + "'");

					break;

				case BREAK:

					if (loopLine != -1) {

					}

					if (Math.abs(t.getInt()) == Math.abs(deque.getFirst())) {
						deque.removeFirst();
					}

					Bukkit.getConsoleSender().sendMessage("§6Debug §5break");

					break;
				case RETURN:

					int f = deque.getFirst();

					if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
						deque.removeFirst();
					}

					if (t.getInt() != f) {
						return false;
					}

					Bukkit.getConsoleSender().sendMessage("§6Debug §5return");

					if (ConfigManager.isActiveViewExecuteTime()) {

						long ms = System.currentTimeMillis() - time;

						Bukkit.getConsoleSender()
								.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
										+ r.getName() + " in -> " + ms + " ms");

					}

					return true;

				case RETURN_TRUE:

					f = deque.getFirst();

					if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
						deque.removeFirst();
					}

					if (t.getInt() != f) {
						return false;
					}

					Bukkit.getConsoleSender().sendMessage("§6Debug §5return true");

					if (ConfigManager.isActiveViewExecuteTime()) {

						long ms = System.currentTimeMillis() - time;

						Bukkit.getConsoleSender()
								.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
										+ r.getName() + " in -> " + ms + " ms");

					}

					return true;

				case RETURN_FALSE:

					f = deque.getFirst();

					if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
						deque.removeFirst();
					}

					if (t.getInt() != f) {
						return false;
					}

					Bukkit.getConsoleSender().sendMessage("§6Debug §5return false");

					if (ConfigManager.isActiveViewExecuteTime()) {

						long ms = System.currentTimeMillis() - time;

						Bukkit.getConsoleSender()
								.sendMessage("Executed " + StringUtils.capitalize(r.getType().toString()) + " "
										+ r.getName() + " in -> " + ms + " ms");

					}

					return true;

				default:
					return false;
			}

		} else {

			switch (t.getType()) {

				case COMMAND:

					if (t.getInt() == deque.getFirst()) {

						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), applyVars(sl.substring(1)));

					}

					break;

				case PLAYERCOMMAND:

					if (t.getInt() == deque.getFirst()) {

						Player p = Bukkit.getPlayer(applyVars(t.getPlayerName()));

						if (p == null) {

							Bukkit.getConsoleSender()
									.sendMessage("[CommandCraftCore] §cPlayer " + applyVars(t.getPlayerName())
											+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);

							return false;
						}

						p.performCommand(applyVars(sl));

					}

					break;

				case LOOP:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					DoubleObject<LineType, String> d = Reader.getCommand(applyVars(sl));

					boolean bool;

					if (d.getKey().getType() == TypeLine.COMMAND) {

						bool = Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), d.getValue().substring(1));

					} else {
						Player p = Bukkit.getPlayer(d.getKey().getPlayerName());
						if (p == null) {

							Bukkit.getConsoleSender()
									.sendMessage("[CommandCraftCore] §cPlayer " + d.getKey().getPlayerName()
											+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);

							return false;
						}

						bool = p.performCommand(d.getValue().substring(1));
					}

					if (bool) {
						loopLine = i - 1;
						deque.addFirst(t.ifnum);
					} else {
						loopLine = -1;
					}

					break;

				case IF:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					DoubleObject<LineType, String> c = Reader.getCommand(applyVars(sl));

					boolean b;

					if (c.getKey().getType() == TypeLine.COMMAND) {

						b = invokeCommand(c.getValue().substring(1));

					} else {
						Player p = Bukkit.getPlayer(c.getKey().getPlayerName());
						if (p == null) {

							Bukkit.getConsoleSender()
									.sendMessage("[CommandCraftCore] §cPlayer " + c.getKey().getPlayerName()
											+ " isn't online! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);

							return false;
						}

						b = invokePlayerCommand(c.getValue().substring(1), p);
					}

					if (c.getValue().startsWith("\\")) {
						b = !b;
					}

					if (b)
						deque.addFirst(t.ifnum);
					else
						deque.addFirst(t.ifnum * -1);

					break;

				case VAR_IF:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					Variable v = manager.execute(sl);

					if (v.getType() != Type.BOOLEAN) {
						throw new IllegalArgumentException(
								"'!if' statements support only commands or boolean variables");
					}

					if ((boolean) v.get())
						deque.addFirst(t.ifnum);
					else
						deque.addFirst(t.ifnum * -1);

					break;

				case VAR:

					if (t.getInt() != deque.getFirst()) {
						return false;
					}

					try {
						manager.execute(sl);
					} catch (Exception e) {
						Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + e.getMessage()
								+ "! §7File: " + r.getFile().getPath() + " §6Line: §e" + i);
						e.printStackTrace();

						return false;
					}
					break;

				case BREAK:

					if (loopLine != -1) {
						i = loopLine;
						return false;
					}

					if (Math.abs(t.getInt()) == Math.abs(deque.getFirst())) {
						deque.removeFirst();
					}

					break;
				case RETURN:

					int f = deque.getFirst();

					if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) && deque.getFirst() != 0) {
						deque.removeFirst();
					}

					if (t.getInt() != f) {
						return false;
					}

					return true;
				/*
				 * case RETURN_TRUE:
				 * 
				 * f = deque.getFirst();
				 * 
				 * if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) &&
				 * deque.getFirst() != 0) { deque.removeFirst(); }
				 * 
				 * if (t.getInt() != f) { return false; }
				 * 
				 * return true; case RETURN_FALSE:
				 * 
				 * f = deque.getFirst();
				 * 
				 * if (Math.abs(t.getInt()) == Math.abs(deque.getFirst()) &&
				 * deque.getFirst() != 0) { deque.removeFirst(); }
				 * 
				 * if (t.getInt() != f) { return false; }
				 * 
				 * return true;
				 */

				default:
					return false;
			}

		}
		return false;
	}

	private String applyVars(String s) {
		return manager.applyVars(s);
	}

	private boolean invokeCommand(String cmd) {

		/*
		 * for (Entry<String, Command> e :
		 * CommandCraftCore.getKnownCommands().entrySet()) {
		 * 
		 * System.out.println(e.getKey() + " -> (" +
		 * e.getValue().getClass().getSimpleName() + ") label:" +
		 * e.getValue().getLabel() + " name:" + e.getValue().getName());
		 * 
		 * }
		 */

		String[] a = cmd.split(" ");

		Command c = CommandCraftCore.getCommandMap().getCommand(a[0]);

		Validate.notNull(c, "Invalid command " + a[0]);

		String[] a1 = new String[a.length - 1];

		System.arraycopy(a, 1, a1, 0, a1.length);

		try {
			return c.execute(Bukkit.getConsoleSender(), a[0], a1);
		} catch (Throwable ex) {
			throw new CommandException("Unhandled exception executing command '" + c.getLabel() + "' in plugin "
					+ CommandCraftCore.getInstance().getDescription().getFullName(), ex);
		}

	}

	private boolean invokePlayerCommand(String cmd, Player p) {

		String[] a = cmd.split(" ");

		Command c = CommandCraftCore.getCommandMap().getCommand(a[0]);

		Validate.notNull(c, "Invalid command " + a[0]);

		if (!c.testPermission(p)) {
			return false;
		}

		String[] a1 = new String[a.length - 1];

		System.arraycopy(a, 1, a1, 0, a1.length);

		try {
			return c.execute(p, a[0], a1);
		} catch (Throwable ex) {
			throw new CommandException("Unhandled exception executing command '" + c.getLabel() + "' in plugin "
					+ CommandCraftCore.getInstance().getDescription().getFullName(), ex);
		}

	}

}
