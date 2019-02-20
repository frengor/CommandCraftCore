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

import lombok.Getter;

public class StringVar extends Variable {

	@Getter
	private static List<String> list = Collections.unmodifiableList(Arrays.asList("toString", "+", "=", "==", "equals",
			"equalsIgnoreCase", "clone", "substring", "!substring", "length", "trim", "charAt", "toCharList", "indexOf",
			"startsWith", "isEmpty", "toUpperCase", "toLowerCase", "split", "endsWith", "type"));

	@Getter
	private String value;

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	public String toString1() {
		return value;
	}

	public StringVar(VariableManager m, String name, String value) {
		super(m, name);
		this.value = value;
	}

	@Override
	public List<String> getMethods() {
		return list;
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public Type getType() {
		return Type.STRING;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "split":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.STRING)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not a string variable");
				return new ListVar(manager, manager.generateInternalName(), value.split((String) parameter.get()));
			case "isEmpty":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new BooleanVar(manager, manager.generateInternalName(), value.isEmpty());
			case "toUpperCase":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), value.toUpperCase());
			case "toLowerCase":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), value.toLowerCase());
			case "startsWith":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.STRING)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not a string variable");
				return new BooleanVar(manager, manager.generateInternalName(),
						value.startsWith((String) parameter.get()));
			case "endsWith":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.STRING)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not a string variable");
				return new BooleanVar(manager, manager.generateInternalName(),
						value.endsWith((String) parameter.get()));
			case "indexOf":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.STRING)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not a string variable");
				return new IntVar(manager, manager.generateInternalName(), value.indexOf((String) parameter.get()));
			case "charAt":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.INT)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not an integer variable");
				if ((int) parameter.get() >= value.length()) {
					throw new IllegalArgumentException(parameter.get()
							+ " is too hight, it must be at most equal to the string lenght - 1 (in this case "
							+ (value.length() - 1) + ")");
				}
				if ((int) parameter.get() < 0) {
					throw new IllegalArgumentException(parameter.get() + " is too low, it must be at least 0");
				}
				return new StringVar(manager, manager.generateInternalName(),
						String.valueOf(value.charAt((int) parameter.get())));
			case "toCharList":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new ListVar(manager, manager.generateInternalName(), value.toCharArray());
			case "+":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new StringVar(manager, manager.generateInternalName(), value + parameter.toString());
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && Type.STRING != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() == Type.STRING) {
					value = (String) parameter.get();
					return this;
				}
				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());
			case "length":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.length());
			case "trim":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), value.trim());
			case "substring":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.INT)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not an integer variable");
				if ((int) parameter.get() > value.length()) {
					throw new IllegalArgumentException(parameter.get()
							+ " is too hight, it must be at most equal to the string lenght (in this case "
							+ value.length() + ")");
				}
				if ((int) parameter.get() < 0) {
					throw new IllegalArgumentException(parameter.get() + " is too low, it must be at least 0");
				}
				return new StringVar(manager, manager.generateInternalName(), value.substring((int) parameter.get()));
			case "!substring":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.INT)
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not an integer variable");
				if ((int) parameter.get() < 0) {
					throw new IllegalArgumentException(parameter.get() + " is too low, it must be at least 0");
				}
				if ((int) parameter.get() > value.length()) {
					throw new IllegalArgumentException(parameter.get()
							+ " is too hight, it must be at most equal to the string lenght (in this case "
							+ value.length() + ")");
				}
				return new StringVar(manager, manager.generateInternalName(),
						value.substring(0, (int) parameter.get()));
			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.STRING) {
					return new BooleanVar(manager, manager.generateInternalName(), parameter.get().equals(value));
				} else
					return new BooleanVar(manager, manager.generateInternalName(), false);

			case "equalsIgnoreCase":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.STRING) {
					return new BooleanVar(manager, manager.generateInternalName(),
							((String) parameter.get()).equalsIgnoreCase(value));
				} else {
					return new BooleanVar(manager, manager.generateInternalName(), false);
				}
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "STRING");
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());
			case "clone":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return clone();
			default:
				return this;
		}
	}

	@Override
	public StringVar clone() {
		return new StringVar(manager, manager.generateInternalName(), value);
	}

}
