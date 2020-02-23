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

import com.fren_gor.commandCraftCore.reader.Reader;
import com.fren_gor.commandCraftCore.vars.VariableManager;

import lombok.Getter;

public class VarLine extends Line {

	@Getter
	private String var;

	public VarLine(Reader reader, int line, String var) {
		super(reader, line);
		this.var = var.trim();
		if (!(this.var.startsWith("!var") || this.var.startsWith("$") || this.var.startsWith("@"))) {
			reader.throwError("Invalid '!var' statement '" + var + "'");
			return;
		}
	}

	public void execute(VariableManager manager) throws IllegalArgumentException {
		manager.execute(var);
	}

	@Override
	public LineType getType() {
		return LineType.VAR;
	}

	@Override
	public String toString() {
		return var;
	}

}
