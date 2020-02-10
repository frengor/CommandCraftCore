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

import com.google.common.collect.Sets;

import lombok.Getter;

public class NullVar extends Variable {

	@Getter
	private static Set<String> list = Collections
			.unmodifiableSet(Sets.newHashSet("toString", "==", "equals", "=", "!=", "type"));

	public NullVar(VariableManager m, String name) {
		super(m, name);
	}

	@Override
	public VarType getType() {
		return VarType.NULL;
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public Object get() {
		return null;
	}

	@Override
	public Set<String> getMethods() {
		return list;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "NULL");
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "null");

			case "!=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(), parameter.getType() != VarType.NULL);
			case "=":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && VarType.NULL != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() != VarType.NULL) {
					manager.vars.remove(name);
					return manager.craftVariable(name, parameter.get(), parameter.getType());
				}
				return this;

			case "==":
			case "equals":
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				return new BooleanVar(manager, manager.generateInternalName(), parameter.getType() == VarType.NULL);

			default:
				throw new RuntimeException("Cannot invoke any method on a null variable!");
		}
	}

	@Override
	public NullVar clone() {
		return new NullVar(manager, manager.generateInternalName());
	}

}
