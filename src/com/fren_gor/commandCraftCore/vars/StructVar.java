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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.Sets;

import lombok.Getter;

public class StructVar extends Variable {

	@Getter
	private static Set<String> list = Collections.unmodifiableSet(Sets.newHashSet("=", "==", "equals", "add", "remove",
			"removeExact", "get", "size", "clone", "indexOf", "contains", "toString", "!=", "type"));

	@Getter
	private List<Variable> value;

	public StructVar(VariableManager m, String name) {
		super(m, name);
		value = new LinkedList<>();
	}

	public StructVar(VariableManager m, String name, List<Variable> objs) {
		super(m, name);
		this.value = objs;
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public StructVar clone() {
		LinkedList<Variable> l = new LinkedList<>();
		for (Variable v : value) {
			l.add((Variable) v.clone());
		}
		return new StructVar(manager, manager.generateInternalName(), l);
	}

	@Override
	public Set<String> getMethods() {
		return list;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && VarType.STRUCT != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() == VarType.STRUCT) {
					value = ((StructVar) parameter).value;
					return this;
				}
				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());
			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");

				return new BooleanVar(manager, manager.generateInternalName(),
						parameter.getType() == VarType.STRUCT ? value.equals(parameter.get()) : false);
			case "add":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}

				value.add(parameter);

				return this;
			case "remove":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				return new BooleanVar(manager, manager.generateInternalName(), value.remove(parameter.get()));
			case "removeExact":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				return value.remove((int) parameter.get());
			case "get":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != VarType.INT)
					throw new IllegalArgumentException("The method ' " + method + " ' require a INT parameter, not a "
							+ parameter.getType().toString());
				return value.get((int) parameter.get());
			case "size":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.size());
			case "clone":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return clone();
			case "indexOf":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new IntVar(manager, manager.generateInternalName(), value.indexOf(parameter));
			case "contains":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(), value.contains(parameter));
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());
			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(),
						parameter.getType() == VarType.STRUCT ? !value.equals(parameter.get()) : true);
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "STRUCT");

			default:
				throw new IllegalArgumentException("Method '" + method + "' not exists");
		}
	}

	@Override
	public VarType getType() {
		return VarType.STRUCT;
	}

	@Override
	public String toString() {

		StringJoiner j = new StringJoiner(", ");

		for (Object o : value)
			j.add(o.toString());

		return "{" + j.toString() + "}";
	}

}
