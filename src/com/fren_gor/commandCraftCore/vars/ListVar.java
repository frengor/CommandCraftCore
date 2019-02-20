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
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang.Validate;

import lombok.Getter;

public class ListVar extends Variable {

	@Getter
	private static List<String> list = Collections.unmodifiableList(Arrays.asList("=", "==", "equals", "add", "remove",
			"get", "size", "clone", "indexOf", "contains", "toString", "!=", "sort", "invert", "type", "listType"));

	@Getter
	private List<Variable> value;

	@Getter
	private Type listType;

	// ConstructorUse
	private String illegal;

	@Override
	public String toString() {
		StringJoiner s = new StringJoiner(", ");
		value.stream().forEach(v -> s.add(v.toString()));
		return "[" + s.toString() + "]";
	}

	public ListVar(VariableManager m, String name, List<Variable> value) {
		super(m, name);
		if (value.size() > 0)
			if (value.stream().anyMatch(v -> {
				if (listType == null) {
					listType = v.getType();
					return false;
				}
				if (v.getType() == Type.LIST || v.getType() != listType) {
					illegal = v.getType().toString().toLowerCase();
					return true;
				}
				return false;
			})) {
				throw new IllegalArgumentException(
						"Illegal list type " + illegal + "! It should be " + listType.toString().toLowerCase());
			}
		this.value = value;
	}

	public ListVar(VariableManager m, String name, char[] strings) {

		super(m, name);

		listType = Type.STRING;

		value = new LinkedList<>();

		for (char s : strings)
			value.add(new StringVar(m, m.generateInternalName(), String.valueOf(s)));

	}

	public ListVar(VariableManager m, String name, String[] strings) {

		super(m, name);

		listType = Type.STRING;

		value = new LinkedList<>();

		for (String s : strings)
			value.add(new StringVar(m, m.generateInternalName(), s));

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
		return Type.LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "LIST");
			case "listType":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), listType.toString());
			case "invert":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				List<Variable> l = new LinkedList<>();
				for (int i = value.size() - 1; i >= 0; i--) {
					l.add(value.get(i));
				}
				this.value = l;
				return this;
			case "sort":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				value.sort((s1, s2) -> s1.toString().compareTo(s2.toString()));
				return this;
			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.LIST && ((ListVar) parameter).getListType() == listType) {
					return new BooleanVar(manager, manager.generateInternalName(), value.equals(parameter.get()));
				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() == Type.LIST && ((ListVar) parameter).getListType() == listType) {
					return new BooleanVar(manager, manager.generateInternalName(), !value.equals(parameter.get()));
				}
				return new BooleanVar(manager, manager.generateInternalName(), true);
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst())
					if (Type.LIST != parameter.getType())
						throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
					else if (listType != ((ListVar) parameter).getListType())
						throw new IllegalArgumentException("Cannot change " + name + "'s list variable type");
				if (parameter.getType() == Type.LIST) {
					value = new LinkedList<>((List<Variable>) parameter.get());
					listType = ((ListVar) parameter).getListType();
					return this;
				}

				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());

			case "add":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (parameter.getType() == Type.LIST) {
					ListVar p = (ListVar) parameter;
					if (p.listType == null)
						return this;
					if (listType == null) {
						value = new LinkedList<>(p.value);
						listType = ((ListVar) parameter).getListType();
						return this;
					}
					if (listType != p.listType)
						throw new IllegalArgumentException("Illegal list type " + p.listType.toString().toLowerCase()
								+ "! It should be " + listType.toString().toLowerCase());
					value.addAll(p.value);
					return this;
				}
				if (listType == null)
					listType = parameter.getType();
				else
					Validate.isTrue(parameter.getType() == listType,
							"Illegal list type " + parameter.getType().toString().toLowerCase() + "! It should be "
									+ listType.toString().toLowerCase());
				value.add(parameter);
				return this;

			case "remove":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (parameter.getType() != Type.INT) {
					throw new IllegalArgumentException("Parameter of method 'get' must be an integer");
				}
				return new BooleanVar(manager, manager.generateInternalName(), value.remove(parameter.get()));
			case "get":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != Type.INT) {
					throw new IllegalArgumentException("Parameter of method 'get' must be an integer");
				}
				return manager.craftVariable(manager.generateInternalName(), value.get((int) parameter.get()),
						listType);
			case "clone":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return clone();
			case "contains":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(),
						parameter.getType() != listType ? false : value.contains(parameter));
			case "size":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.size());
			case "indexOf":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new IntVar(manager, manager.generateInternalName(), value.indexOf(parameter));
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());

			default:
				throw new IllegalArgumentException("Method '" + method + "' is not implemented");
		}
	}

	@Override
	public ListVar clone() {
		LinkedList<Variable> l = new LinkedList<>();
		for (Variable v : value) {
			l.add((Variable) v.clone());
		}
		return new ListVar(manager, manager.generateInternalName(), l);
	}

	@Override
	public void setFinal() {
		super.setFinal();
		list = Collections.unmodifiableList(list);
	}

}
