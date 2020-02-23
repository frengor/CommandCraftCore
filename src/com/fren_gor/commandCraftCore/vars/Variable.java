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

import java.util.Set;

import com.fren_gor.commandCraftCore.vars.exceptions.VariableException;

import lombok.Getter;

public abstract class Variable implements Cloneable {

	// Var cannot change at all
	@Getter
	private boolean Final = false;

	// Var cannot change the value
	@Getter
	private boolean Const = false;

	public abstract Object get();

	@Override
	public abstract Object clone();

	public abstract Set<String> getMethods();

	public abstract Variable invoke(String method, Variable parameter);

	public abstract VarType getType();

	@Getter
	protected String name;

	@Getter
	protected final VariableManager manager;

	protected Variable(VariableManager m, String name) {

		/*
		 * if (getType() == null) throw new
		 * VariableException("A variable must belong to a VarType");
		 */

		name = name.trim();

		if (!name.startsWith("$"))
			name = "$" + name;

		if (m.vars.containsKey(name)) {
			throw new VariableException("A variable with name '" + name + "' already exist");
		}
		m.vars.put(name, this);
		this.name = name;
		this.manager = m;
	}

	public void setFinal() {
		Final = true;
	}

	public void setConst() {
		Const = true;
	}

	@Override
	public abstract String toString();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (Const ? 1231 : 1237);
		result = prime * result + (Final ? 1231 : 1237);
		result = prime * result + ((manager == null) ? 0 : manager.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Variable))
			return false;
		Variable other = (Variable) obj;
		if (Const != other.Const)
			return false;
		if (Final != other.Final)
			return false;
		if (manager == null) {
			if (other.manager != null)
				return false;
		} else if (!manager.equals(other.manager))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
