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

package com.fren_gor.commandCraftCore.vars;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.fren_gor.commandCraftCore.utils.Utils;

public class StaticActions {

	private VariableManager manager;
	public static Map<String, String> savedVariables = new HashMap<>();
	public static Map<String, String> writtenVariables = new HashMap<>();

	public StaticActions(VariableManager m) {
		manager = m;
	}

	private static Map<String, StaticAction> map = new HashMap<>();

	static {
		map.put("Bukkit", (manager, action, parameters) -> {
			switch (action) {
				case "getOnlinePlayers":
					Collection<? extends Player> c = Bukkit.getOnlinePlayers();
					ListVar l = new ListVar(manager, manager.generateInternalName(), new LinkedList<>());
					for (Player p : c) {
						l.add(new PlayerVar(manager, manager.generateInternalName(), p));
					}
					l.setFinal();
					return l;
				case "getPlayer":

					Validate.isTrue(parameters.length == 1, "Invalid parameter! @Bukkit@getPlayer name/UUID");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"Parameter must be a String with the player name or with the player's UUID");

					String s = ((StringVar) parameters[0]).getValue();

					if (s.length() <= 16) {

						OfflinePlayer p = Bukkit.getPlayer(s);
						if (p == null || !p.isOnline()) {
							return new NullVar(manager, manager.generateInternalName());
						}
						if (p.getPlayer() == null) {
							return new NullVar(manager, manager.generateInternalName());
						}
						return new PlayerVar(manager, manager.generateInternalName(), p.getPlayer());

					} else if (s.length() == 36) {

						UUID u = UUID.fromString(s);
						OfflinePlayer p = Bukkit.getPlayer(u);
						Validate.isTrue(p == null || !p.isOnline(),
								"Player with UUID '" + s + "' is invalid or offline");

						return new PlayerVar(manager, manager.generateInternalName(), p.getPlayer());

					} else {
						throw new IllegalArgumentException(
								"Invalid parameter! Parameter must be a String with the player name or with the player's UUID");
					}

				case "getBlockTypeAt":
					Validate.isTrue(parameters.length == 4,
							"Invalid parameters! @Bukkit@getBlockTypeAt <string worldName> <int x> <int y> <int z>");

					Validate.isTrue(
							parameters[0].getType() == VarType.STRING && parameters[1].getType() == VarType.INT
									&& parameters[2].getType() == VarType.INT && parameters[3].getType() == VarType.INT,
							"Invalid parameters! @Bukkit@getBlockTypeAt <string worldName> <int x> <int y> <int z>");

					World w = Bukkit.getWorld(((StringVar) parameters[0]).getValue());

					return new StringVar(
							manager, manager.generateInternalName(), w
									.getBlockAt(((IntVar) parameters[1]).getValue(),
											((IntVar) parameters[2]).getValue(), ((IntVar) parameters[3]).getValue())
									.getType().toString().toLowerCase());

				default:
					throw new IllegalArgumentException("StaticAction '" + action + "' does not exist");
			}
		});

		map.put("VarUtils", (manager, action, parameters) -> {
			switch (action) {
				case "saveVar": {

					Validate.isTrue(parameters.length == 2,
							"Invalid parameters! @VarUtils@setVar <string variableName> <variableToSave>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					String ret = savedVariables.put(s, parameters[1].toString());
					Variable v = ret != null && !ret.isEmpty() ? manager.craftVar(manager.generateInternalName(), ret)
							: new NullVar(manager, manager.generateInternalName());
					return v;
				}
				case "loadVar": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @VarUtils@getVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					String ret = savedVariables.get(s);
					Variable v = ret != null && !ret.isEmpty() ? manager.craftVar(manager.generateInternalName(), ret)
							: new NullVar(manager, manager.generateInternalName());
					return v;
				}
				case "isSavedVar": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @VarUtils@isSavedVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					return new BooleanVar(manager, manager.generateInternalName(), savedVariables.get(s) != null);
				}
				case "unsaveVar": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @VarUtils@unsaveVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					savedVariables.remove(s);

					return new NullVar(manager, manager.generateInternalName());
				}
				case "writeVar": {

					Validate.isTrue(parameters.length == 2,
							"Invalid parameters! @VarUtils@writeVar <string variableName> <variableToSave>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					String ret = writtenVariables.put(s, parameters[1].toString());
					Variable v = ret != null && !ret.isEmpty() ? manager.craftVar(manager.generateInternalName(), ret)
							: new NullVar(manager, manager.generateInternalName());
					return v;
				}
				case "readVar": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @VarUtils@readVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					String ret = writtenVariables.get(s);
					Variable v = ret != null && !ret.isEmpty() ? manager.craftVar(manager.generateInternalName(), ret)
							: new NullVar(manager, manager.generateInternalName());
					return v;
				}
				case "isWrittenVar": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @VarUtils@isWrittenVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					return new BooleanVar(manager, manager.generateInternalName(), writtenVariables.get(s) != null);
				}
				case "removeVar": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @VarUtils@removeVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					writtenVariables.remove(s);

					return new NullVar(manager, manager.generateInternalName());
				}
				case "randomizeInteger": {

					Validate.isTrue(parameters.length == 2,
							"Invalid parameters! @VarUtils@randomizeInteger <int min> <int max>");
					Validate.isTrue(parameters[0].getType() == VarType.INT,
							"'" + parameters[0] + "' must be an Integer");
					Validate.isTrue(parameters[1].getType() == VarType.INT,
							"'" + parameters[1] + "' must be an Integer");

					return new IntVar(manager, manager.generateInternalName(),
							Utils.nextInt((int) parameters[0].get(), (int) parameters[1].get()));
				}
				case "randomize": {

					Validate.isTrue(parameters.length == 0, "Invalid parameters! @VarUtils@randomize");

					return new DoubleVar(manager, manager.generateInternalName(), Math.random());
				}
				default:
					throw new IllegalArgumentException("StaticAction '" + action + "' does not exist");
			}
		});

		map.put("Convert", (manager, action, parameters) -> {
			switch (action) {
				case "toInt": {
					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Convert@toInt <variable>");

					if (parameters[0].getType() == VarType.INT || parameters[0].getType() == VarType.DOUBLE) {
						return new IntVar(manager, manager.generateInternalName(), (int) (double) parameters[0].get());
					}

					if (parameters[0].getType() == VarType.BOOLEAN) {
						return new IntVar(manager, manager.generateInternalName(),
								((BooleanVar) parameters[0]).getValue() ? 1 : 0);
					}

					if (parameters[0].getType() == VarType.STRING) {
						return new IntVar(manager, manager.generateInternalName(),
								Integer.parseInt((String) parameters[0].get()));
					}

					if (parameters[0].getType() == VarType.STRUCT) {
						return new IntVar(manager, manager.generateInternalName(),
								((StructVar) parameters[0]).getValue().size());
					}

					if (parameters[0].getType() == VarType.LIST) {
						return new IntVar(manager, manager.generateInternalName(),
								((ListVar) parameters[0]).getValue().size());
					}

					return new IntVar(manager, manager.generateInternalName(), 0);
				}
				case "toDouble": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Convert@toDouble <variable>");

					if (parameters[0].getType() == VarType.INT) {
						return new DoubleVar(manager, manager.generateInternalName(),
								((IntVar) parameters[0]).getValue());
					}

					if (parameters[0].getType() == VarType.DOUBLE) {
						return new DoubleVar(manager, manager.generateInternalName(), (double) parameters[0].get());
					}

					if (parameters[0].getType() == VarType.BOOLEAN) {
						return new DoubleVar(manager, manager.generateInternalName(),
								((BooleanVar) parameters[0]).getValue() ? 1 : 0);
					}

					if (parameters[0].getType() == VarType.STRING) {
						return new DoubleVar(manager, manager.generateInternalName(),
								Double.parseDouble((String) parameters[0].get()));
					}

					if (parameters[0].getType() == VarType.STRUCT) {
						return new DoubleVar(manager, manager.generateInternalName(),
								((StructVar) parameters[0]).getValue().size());
					}

					if (parameters[0].getType() == VarType.LIST) {
						return new DoubleVar(manager, manager.generateInternalName(),
								((ListVar) parameters[0]).getValue().size());
					}

					return new DoubleVar(manager, manager.generateInternalName(), 0);
				}
				case "toString": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Convert@toString <variable>");

					return new StringVar(manager, manager.generateInternalName(), parameters[0].toString());
				}
				case "toBoolean": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Convert@toBoolean <variable>");

					if (parameters[0].getType() == VarType.BOOLEAN) {
						return new BooleanVar(manager, manager.generateInternalName(), (boolean) parameters[0].get());
					}

					if (parameters[0].getType() == VarType.INT) {
						return new BooleanVar(manager, manager.generateInternalName(),
								((IntVar) parameters[0]).getValue() > 0);
					}

					if (parameters[0].getType() == VarType.DOUBLE) {
						return new BooleanVar(manager, manager.generateInternalName(),
								((DoubleVar) parameters[0]).getValue() > 0);
					}

					if (parameters[0].getType() == VarType.STRING) {
						String s = ((StringVar) parameters[0]).getValue();
						return new BooleanVar(manager, manager.generateInternalName(),
								s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1"));
					}

					return new BooleanVar(manager, manager.generateInternalName(),
							parameters[0].getType() == VarType.NULL);
				}
				case "fromString": {

					Validate.isTrue(parameters.length == 1,
							"Invalid parameters! @Convert@fromString <string variable>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					return manager.craftVar(manager.generateInternalName(), ((StringVar) parameters[0]).rawToString());
				}
				default:
					throw new IllegalArgumentException("StaticAction '" + action + "' does not exist");
			}
		});

		map.put("Debug", (manager, action, parameters) -> {
			switch (action) {
				case "debug": {

					StringJoiner d = new StringJoiner(" ");

					for (Variable v : parameters) {
						d.add(v.toString());
					}

					System.out.println(d.toString());

					return new StringVar(manager, manager.generateInternalName(), d.toString());
				}
				case "info": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Debug@info <string info>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					Bukkit.getLogger().info(s);

					return (StringVar) parameters[0];
				}
				case "warn": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Debug@warn <string warnString>");
					Validate.isTrue(parameters[0].getType() == VarType.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					Bukkit.getLogger().warning(s);

					return (StringVar) parameters[0];
				}
				case "printVars": {
					Validate.isTrue(parameters.length == 0, "Invalid parameters! No required parameters");

					manager.print();
					return new NullVar(manager, manager.generateInternalName());
				}
				default:
					throw new IllegalArgumentException("StaticAction '" + action + "' does not exist");
			}
		});
	}

	public static void register(String key, StaticAction value) {
		if (key.equals("Bukkit") || key.equals("Utils"))
			throw new IllegalArgumentException("Illegal main name: " + key);
		if (map.containsKey(key)) {
			throw new IllegalArgumentException("A '" + key + "' StaticVar has already been registered");
		}
		map.put(key, value);
	}

	public Variable getVariable(String... args) {

		String s = args[0];

		if (s.length() < 4) {
			throw new IllegalArgumentException("Invalid '@' statement");
		}
		if (!s.startsWith("@") || !s.contains("#")) {
			throw new IllegalArgumentException("Invalid '@' statement: @main#staticAction");
		}

		s = s.substring(1);
		args[0] = s;

		Validate.isTrue(Arrays.stream(args).noneMatch(p -> p.contains("@")), "Invalid '@' statement, too much '@'");

		String[] st = s.split("#");

		Validate.isTrue(st.length == 2, "Invalid '@' statement, it must contains one '#'");

		String main = st[0];
		String action = st[1];

		Validate.isTrue(map.containsKey(main),
				"Main " + main + " is not registered. Make sure to have all dependencies installed");

		Variable[] arr = new Variable[args.length - 1];

		for (int i = 0; i < arr.length; i++) {

			arr[i] = manager.craftVar(manager.generateInternalName(), args[i + 1]);

		}

		return map.get(main).getVar(manager, action, arr);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((manager == null) ? 0 : manager.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StaticActions))
			return false;
		StaticActions other = (StaticActions) obj;
		if (manager == null) {
			if (other.manager != null)
				return false;
		} else if (!manager.equals(other.manager))
			return false;
		return true;
	}

}
