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
import lombok.Setter;

public class BooleanVar extends Variable {

	@Getter
	private static List<String> list = Collections.unmodifiableList(
			Arrays.asList("=!", "||", "&&", "^^", "=", "==", "!=", "equals", "toString", "clone", "type"));

	@Setter
	private boolean value;

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public BooleanVar(VariableManager m, String name, boolean value) {
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
		return Type.BOOLEAN;
	}

	@Override
	public BooleanVar clone() {
		return new BooleanVar(manager, manager.generateInternalName(), value);
	}

	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "BOOLEAN");
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && Type.NULL != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() == Type.BOOLEAN) {
					value = (boolean) parameter.get();
					return this;
				}
				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());
			case "clone":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return clone();
			case "=!":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.BOOLEAN) {

					value = !((boolean) parameter.get());

					return this;
				} else
					throw new IllegalArgumentException("The method " + method + " is defined only for boolean vars");
			case "||":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.BOOLEAN)
					return new BooleanVar(manager, manager.generateInternalName(),
							value || ((boolean) parameter.get()));
				else
					throw new IllegalArgumentException("The method " + method + " is defined only for boolean vars");
			case "&&":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.BOOLEAN)
					return new BooleanVar(manager, manager.generateInternalName(),
							value && ((boolean) parameter.get()));
				else
					throw new IllegalArgumentException("The method " + method + " is defined only for boolean vars");
			case "^^":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.BOOLEAN)
					return new BooleanVar(manager, manager.generateInternalName(),
							(value) ^ ((boolean) parameter.get()));
				else
					throw new IllegalArgumentException("The method " + method + " is defined only for boolean vars");
			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.BOOLEAN)
					return new BooleanVar(manager, manager.generateInternalName(), value != (boolean) parameter.get());
				else
					return new BooleanVar(manager, manager.generateInternalName(), true);
			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.BOOLEAN)
					return new BooleanVar(manager, manager.generateInternalName(), value == (boolean) parameter.get());
				else
					return new BooleanVar(manager, manager.generateInternalName(), false);
			case "!":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				value = !value;
				return this;
			default:
				throw new IllegalArgumentException("Method '" + method + "' is not implemented");
		}
	}

}
