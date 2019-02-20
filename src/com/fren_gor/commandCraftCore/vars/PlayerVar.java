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
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import lombok.Getter;

public class PlayerVar extends Variable {

	private final Player player;

	@Getter
	private static List<String> list = Collections.unmodifiableList(
			Arrays.asList("toString", "==", "equals", "=", "!=", "type", "getHealth", "getFoodLevel", "getPosX",
					"getPosY", "getPosZ", "getBlockPosX", "getBlockPosY", "getBlockPosZ", "getWorldName", "getUUID"));

	public PlayerVar(VariableManager m, String name, Player player) {
		super(m, name);
		this.player = player;
	}

	@Override
	public Type getType() {
		return Type.PLAYER;
	}

	@Override
	public String toString() {
		return player.getName();
	}

	@Override
	public Player get() {
		return player;
	}

	@Override
	public List<String> getMethods() {
		return list;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "getUUID":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), player.getUniqueId().toString());
			case "getWorldName":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), player.getWorld().getName());
			case "getBlockPosX":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), player.getLocation().getBlockX());
			case "getBlockPosY":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), player.getLocation().getBlockY());
			case "getBlockPosZ":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), player.getLocation().getBlockZ());
			case "getPosX":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), player.getLocation().getX());
			case "getPosY":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), player.getLocation().getY());
			case "getPosZ":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), player.getLocation().getZ());
			case "getHealth":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), player.getHealth());
			case "getFoodLevel":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), player.getFoodLevel());
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "PLAYER");
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), player.getName());

			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(), parameter.getType() != Type.PLAYER
						|| !((Player) parameter.get()).getUniqueId().equals(player.getUniqueId()));
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && Type.PLAYER != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() != Type.PLAYER) {
					manager.vars.remove(name);
					return manager.craftVariable(name, parameter.get(), parameter.getType());
				}
				return this;

			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(), parameter.getType() == Type.PLAYER
						&& ((Player) parameter.get()).getUniqueId().equals(player.getUniqueId()));

			default:
				throw new IllegalArgumentException("Method '" + method + "' is not implemented");
		}
	}

	@Override
	public PlayerVar clone() {
		return new PlayerVar(manager, manager.generateInternalName(), player);
	}

}
