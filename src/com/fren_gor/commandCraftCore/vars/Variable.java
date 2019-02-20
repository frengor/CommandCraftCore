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

import java.util.List;

import com.fren_gor.commandCraftCore.Reader;

import lombok.Getter;

public abstract class Variable implements Cloneable {

	@Getter
	private boolean Final = false;
	@Getter
	private boolean Const = false;

	public abstract Object get();

	@Override
	public abstract Object clone();

	public abstract List<String> getMethods();

	public abstract Variable invoke(String method, Variable parameter);

	public abstract Type getType();

	@Getter
	protected final String name;

	@Getter
	protected final VariableManager manager;

	protected Variable(VariableManager m, String name) {

		name = Reader.trim(name);

		if (!name.startsWith("$"))
			name = "$" + name;

		if (m.vars.containsKey(name)) {
			throw new IllegalArgumentException("Variable with name '" + name + "' already exist");
		}
		m.vars.put(name, this);
		this.name = name;
		this.manager = m;
		if (!name.startsWith("internal_"))
			m.sort();
	}

	public void setFinal() {
		Final = true;
	}

	public void setConst() {
		Const = true;
	}

}
