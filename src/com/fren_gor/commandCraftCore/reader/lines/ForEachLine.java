//  MIT License
//  
//  Copyright (c) 2020 fren_gor
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

package com.fren_gor.commandCraftCore.reader.lines;

import java.util.Iterator;

import com.fren_gor.commandCraftCore.reader.Reader;
import com.fren_gor.commandCraftCore.vars.Variable;
import com.fren_gor.commandCraftCore.vars.VariableManager;

import lombok.Getter;
import lombok.Setter;

public class ForEachLine extends Line {

	@Getter
	private VarLine var;
	@Getter
	private VarLine list;
	@Getter
	@Setter
	private int gotoLine;
	@Setter
	@Getter
	private Iterator<Variable> iterator;

	public ForEachLine(Reader reader, int line, String var, String list) {
		super(reader, line);
		System.out.println(var);
		var = var.trim();
		if (var.startsWith("!var "))
			var = var.substring(5).trim();
		try {
			if (!VariableManager.verifyName(var)) {
				reader.throwError("Invalid foreach statement. '" + var + "' isn't a valid variable");
				return;
			}
		} catch (IllegalArgumentException e) {
			reader.throwError("Invalid foreach statement. '" + var + "' isn't a valid variable");
			return;
		}
		this.var = new VarLine(reader, line, var);
		this.list = new VarLine(reader, line, list);
	}

	@Override
	public LineType getType() {
		return LineType.FOREACH;
	}

	@Override
	public String toString() {
		return var + " : " + line;
	}

}
