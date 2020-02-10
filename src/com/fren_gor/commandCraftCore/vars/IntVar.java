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
import java.util.Set;

import com.fren_gor.commandCraftCore.utils.saveUtils.TripleObject;
import com.google.common.collect.Sets;

import lombok.Getter;

public class IntVar extends Variable {

	@Getter
	private static Set<String> list = Collections.unmodifiableSet(Sets.newHashSet("=", "+", "-", "*", "/", "√", "%",
			"!=", "^", "toString", "clone", "==", "equals", ">", "v", "<", ">=", "<=", "toDouble", "type"));

	@Getter
	private int value;

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public IntVar(VariableManager m, String name, int value) {
		super(m, name);
		this.value = value;
	}

	@Override
	public Set<String> getMethods() {
		return list;
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public VarType getType() {
		return VarType.INT;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {

		TripleObject<Integer, Double, Boolean> d = parameter == null ? new TripleObject<>(null, null, false)
				: transorm(parameter);

		switch (method) {
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "INT");
			case "toDouble":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), value);
			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2())
					return new BooleanVar(manager, manager.generateInternalName(), value != d.getValue1());
				return new BooleanVar(manager, manager.generateInternalName(), true);
			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					return new BooleanVar(manager, manager.generateInternalName(), value == d.getValue1());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case ">":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					return new BooleanVar(manager, manager.generateInternalName(), value > d.getValue1());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case "<":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					return new BooleanVar(manager, manager.generateInternalName(), value < d.getValue1());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case ">=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					return new BooleanVar(manager, manager.generateInternalName(), value >= d.getValue1());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case "<=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					return new BooleanVar(manager, manager.generateInternalName(), value <= d.getValue1());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case "clone":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return clone();
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && VarType.INT != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() == VarType.INT) {
					value = d.getKey();
					return this;
				}
				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());
			case "+":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					if (parameter.getType() == VarType.DOUBLE)
						return new DoubleVar(manager, manager.generateInternalName(), d.getValue1() + value);
					return new IntVar(manager, manager.generateInternalName(), d.getKey() + value);

				} else {
					throw new IllegalArgumentException(
							"Cannot add a " + parameter.getType().toString().toLowerCase() + " to an integer");
				}
			case "-":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					if (parameter.getType() == VarType.DOUBLE)
						return new DoubleVar(manager, manager.generateInternalName(), value - d.getValue1());
					return new IntVar(manager, manager.generateInternalName(), value - d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot subtract a " + parameter.getType().toString().toLowerCase() + " to an integer");
				}
			case "*":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					if (parameter.getType() == VarType.DOUBLE)
						return new DoubleVar(manager, manager.generateInternalName(), value * d.getValue1());
					return new IntVar(manager, manager.generateInternalName(), value * d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot multiply an integer by a " + parameter.getType().toString().toLowerCase());
				}
			case "/":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					if (parameter.getType() == VarType.DOUBLE)
						return new DoubleVar(manager, manager.generateInternalName(), value / d.getValue1());
					return new IntVar(manager, manager.generateInternalName(), value / d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot divide an integer by a " + parameter.getType().toString().toLowerCase());
				}
				// root
			case "v":
				// \u221A
			case "√":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != VarType.INT) {
					throw new IllegalArgumentException(parameter.getName() + " is not an integer variable");
				}

				int v = (int) Math.pow(Math.E, Math.log(value) / d.getValue1());
				return new IntVar(manager, manager.generateInternalName(), v);
			case "%":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {

					if (parameter.getType() == VarType.DOUBLE)
						return new DoubleVar(manager, manager.generateInternalName(), value % d.getValue1());
					return new IntVar(manager, manager.generateInternalName(), value % d.getKey());

				} else {
					throw new IllegalArgumentException("Cannot make the module of an integer for a "
							+ parameter.getType().toString().toLowerCase());
				}
			case "^":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue2()) {
					if (parameter.getType() == VarType.DOUBLE)
						return new DoubleVar(manager, manager.generateInternalName(), Math.pow(value, d.getValue1()));
					return new IntVar(manager, manager.generateInternalName(), (int) Math.pow(value, d.getKey()));

				} else {
					throw new IllegalArgumentException(
							"Cannot rise an integer for a " + parameter.getType().toString().toLowerCase());
				}
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());
			default:
				throw new IllegalArgumentException("Method '" + method + "' not exists");
		}

	}

	private static TripleObject<Integer, Double, Boolean> transorm(Variable v) {

		switch (v.getType().toString()) {
			case "INT":
				return new TripleObject<>((int) v.get(), (double) (int) v.get(), true);
			case "DOUBLE":
				return new TripleObject<>((int) (double) v.get(), (double) v.get(), true);
			default:
				return new TripleObject<>(null, null, false);
		}

	}

	@Override
	public IntVar clone() {
		return new IntVar(manager, manager.generateInternalName(), value);
	}

}
