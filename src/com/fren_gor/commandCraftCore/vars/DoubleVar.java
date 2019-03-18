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

import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleObject;

import lombok.Getter;

public class DoubleVar extends Variable {

	@Getter
	private static List<String> list = Collections
			.unmodifiableList(Arrays.asList("=", "+", "-", "*", "/", "%", "^", "clone", "toString", "==", "equals",
					"toInt", "!=", ">", "<", ">=", "<=", "\\/¯", "v", "floor", "ceil", "round", "type"));

	@Getter
	private double value;

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public DoubleVar(VariableManager m, String name, double value) {
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
		return Type.DOUBLE;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {

		DoubleObject<Double, Boolean> d = parameter == null ? new DoubleObject<>(null, false) : transorm(parameter);

		switch (method) {
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "DOUBLE");
			case "round": {
				if (parameter == null) {
					value = Math.round(value);
					return this;
				}
				if (parameter.getType() != Type.INT) {
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not an integer variable");
				}

				long m = (long) Math.pow(10, (int) parameter.get());
				double v = Math.round(value * m) / m;

				return new DoubleVar(manager, manager.generateInternalName(), v);
			}
			case "floor": {
				if (parameter == null) {
					value = Math.floor(value);
					return this;
				}
				if (parameter.getType() != Type.INT) {
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not an integer variable");
				}

				long m = (long) Math.pow(10, (int) parameter.get());
				double v = Math.floor(value * m) / m;

				return new DoubleVar(manager, manager.generateInternalName(), v);
			}
			case "ceil": {
				if (parameter == null) {
					value = Math.ceil(value);
					return this;
				}
				if (parameter.getType() != Type.INT) {
					throw new IllegalArgumentException(
							parameter.getType().toString().toLowerCase() + " is not an integer variable");
				}

				long m = (long) Math.pow(10, (int) parameter.get());
				double v = Math.ceil(value * m) / m;

				return new DoubleVar(manager, manager.generateInternalName(), v);
			}
			case "toInt":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), (int) Math.round(value));
			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue())
					return new BooleanVar(manager, manager.generateInternalName(),
							d.getKey() != ((double) parameter.get()));
				return new BooleanVar(manager, manager.generateInternalName(), true);
			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new BooleanVar(manager, manager.generateInternalName(), value == d.getKey());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case ">":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new BooleanVar(manager, manager.generateInternalName(), value > d.getKey());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case "<":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new BooleanVar(manager, manager.generateInternalName(), value < d.getKey());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case ">=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new BooleanVar(manager, manager.generateInternalName(), value >= d.getKey());

				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			case "<=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new BooleanVar(manager, manager.generateInternalName(), value <= d.getKey());

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
				if (isConst() && Type.DOUBLE != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() == Type.DOUBLE) {
					value = d.getKey();
					return this;
				}
				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());

			case "+":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new DoubleVar(manager, manager.generateInternalName(), value + d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot add a " + parameter.getType().toString().toLowerCase() + " to a double");
				}
			case "-":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new DoubleVar(manager, manager.generateInternalName(), value - d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot subtract a " + parameter.getType().toString().toLowerCase() + " to a double");
				}
				// root
			case "v":
				// Alt + 0175
			case "\\/¯":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					double v = Math.pow(Math.E, Math.log(value) / d.getKey());
					return new DoubleVar(manager, manager.generateInternalName(), v);

				} else {
					throw new IllegalArgumentException(
							"Cannot make the root of a double for a " + parameter.getType().toString().toLowerCase());
				}
			case "*":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new DoubleVar(manager, manager.generateInternalName(), value * d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot multiply a double by a " + parameter.getType().toString().toLowerCase());
				}
			case "/":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new DoubleVar(manager, manager.generateInternalName(), value / d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot divide a double by a " + parameter.getType().toString().toLowerCase());
				}
			case "%":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new DoubleVar(manager, manager.generateInternalName(), value % d.getKey());

				} else {
					throw new IllegalArgumentException(
							"Cannot make the module of a double for a " + parameter.getType().toString().toLowerCase());
				}
			case "^":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (d.getValue()) {

					return new DoubleVar(manager, manager.generateInternalName(), Math.pow(value, d.getKey()));

				} else {
					throw new IllegalArgumentException(
							"Cannot rise a double for a " + parameter.getType().toString().toLowerCase());
				}
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());
			default:
				throw new IllegalArgumentException("Method '" + method + "' is not implemented");
		}

	}

	private static DoubleObject<Double, Boolean> transorm(Variable v) {

		switch (v.getType()) {
			case INT:
				return new DoubleObject<>((double) (int) v.get(), true);
			case DOUBLE:
				return new DoubleObject<>((double) v.get(), true);
			default:
				return new DoubleObject<>(null, false);
		}

	}

	@Override
	public DoubleVar clone() {
		return new DoubleVar(manager, manager.generateInternalName(), value);
	}

}
