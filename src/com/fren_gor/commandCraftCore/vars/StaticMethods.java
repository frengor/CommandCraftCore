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
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class StaticMethods {

	private VariableManager manager;
	public static Map<String, String> savedVariables = new HashMap<>();

	public StaticMethods(VariableManager m) {
		manager = m;
	}

	private static Map<String, StaticMethod> map = new HashMap<>();

	static {
		map.put("Bukkit", (manager, method, parameters) -> {
			switch (method) {
				case "getPlayer":

					Validate.isTrue(parameters.length == 1, "Invalid parameter! @Bukkit@getPlayer name/UUID");
					Validate.isTrue(parameters[0].getType() == com.fren_gor.commandCraftCore.vars.Type.STRING,
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
							parameters[0].getType() == com.fren_gor.commandCraftCore.vars.Type.STRING
									&& parameters[1].getType() == com.fren_gor.commandCraftCore.vars.Type.INT
									&& parameters[2].getType() == com.fren_gor.commandCraftCore.vars.Type.INT
									&& parameters[3].getType() == com.fren_gor.commandCraftCore.vars.Type.INT,
							"Invalid parameters! @Bukkit@getBlockTypeAt <string worldName> <int x> <int y> <int z>");

					World w = Bukkit.getWorld(((StringVar) parameters[0]).getValue());

					return new StringVar(manager,
							manager.generateInternalName(), w
									.getBlockAt(((IntVar) parameters[1]).getValue(),
											((IntVar) parameters[2]).getValue(), ((IntVar) parameters[3]).getValue())
									.getType().toString().toLowerCase());

				default:
					throw new IllegalArgumentException("Method '" + method + "' does not exist");
			}
		});

		map.put("Utils", (manager, method, parameters) -> {
			switch (method) {
				case "saveVar": {

					Validate.isTrue(parameters.length == 2,
							"Invalid parameters! @Utils@saveVar <string variableName> <variableToSave>");
					Validate.isTrue(parameters[0].getType() == com.fren_gor.commandCraftCore.vars.Type.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					String ret = savedVariables.put(s, parameters[1].toString());
					Variable v = ret != null && !ret.isEmpty() ? manager.craftVar(manager.generateInternalName(), ret)
							: new NullVar(manager, manager.generateInternalName());
					return v;
				}
				case "loadVar": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Utils@getVar <string variableName>");
					Validate.isTrue(parameters[0].getType() == com.fren_gor.commandCraftCore.vars.Type.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					String ret = savedVariables.get(s);
					Variable v = ret != null && !ret.isEmpty() ? manager.craftVar(manager.generateInternalName(), ret)
							: new NullVar(manager, manager.generateInternalName());
					return v;
				}
				default:
					throw new IllegalArgumentException("Method '" + method + "' does not exist");
			}
		});
		map.put("Debug", (manager, method, parameters) -> {
			switch (method) {
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
					Validate.isTrue(parameters[0].getType() == com.fren_gor.commandCraftCore.vars.Type.STRING,
							"'" + parameters[0] + "' must be a String");

					String s = ((StringVar) parameters[0]).getValue();

					Bukkit.getLogger().info(s);

					return (StringVar) parameters[0];
				}
				case "warn": {

					Validate.isTrue(parameters.length == 1, "Invalid parameters! @Debug@warn <string warnString>");
					Validate.isTrue(parameters[0].getType() == com.fren_gor.commandCraftCore.vars.Type.STRING,
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
					throw new IllegalArgumentException("Method '" + method + "' does not exist");
			}
		});
	}

	public static void register(String key, StaticMethod value) {
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
			throw new IllegalArgumentException("Invalid '@' statement: @main#method");
		}

		s = s.substring(1);
		args[0] = s;

		Validate.isTrue(Arrays.stream(args).noneMatch(p -> p.contains("@")), "Invalid '@' statement, too much '@'");

		String[] st = s.split("#");

		Validate.isTrue(st.length == 2, "Invalid '@' statement, it must contains one '#'");

		String main = st[0];
		String method = st[1];

		Validate.isTrue(map.containsKey(main),
				"Main " + main + " is not registered. Make sure to have all dependencies installed");

		Variable[] arr = new Variable[args.length - 1];

		for (int i = 0; i < arr.length; i++) {

			arr[i] = manager.craftVar(manager.generateInternalName(), args[i + 1]);

		}

		return map.get(main).getVar(manager, method, arr);
	}

}
