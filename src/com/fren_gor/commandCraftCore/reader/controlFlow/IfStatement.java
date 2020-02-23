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

package com.fren_gor.commandCraftCore.reader.controlFlow;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fren_gor.commandCraftCore.reader.lines.GotoLine;
import com.fren_gor.commandCraftCore.reader.lines.IfLine;

import lombok.Getter;

public class IfStatement implements ControlFlowStatement {

	@Getter
	private final IfLine line;
	@Getter
	private final List<GotoLine> gotoLines;

	public IfStatement(IfLine line, GotoLine newGotoLine, GotoLine... oldGotoLine) {
		this.line = line;
		this.gotoLines = new LinkedList<>();
		Collections.addAll(this.gotoLines, oldGotoLine);
		this.gotoLines.add(newGotoLine);
	}

	public IfStatement(IfLine line, GotoLine newGotoLine, List<GotoLine> oldGotoLine) {
		this.line = line;
		this.gotoLines = oldGotoLine;
		this.gotoLines.add(newGotoLine);
	}

	public IfStatement(IfLine line, GotoLine newGotoLine) {
		this.line = line;
		this.gotoLines = new LinkedList<>();
		this.gotoLines.add(newGotoLine);
	}

	public IfStatement(IfLine line, GotoLine... gotoLine) {
		this.line = line;
		this.gotoLines = new LinkedList<>();
		Collections.addAll(this.gotoLines, gotoLine);
	}

	public IfStatement(IfLine line, List<GotoLine> gotoLine) {
		this.line = line;
		this.gotoLines = gotoLine;
	}

	public void setElseLine(int elseLine) {
		line.setElseLine(elseLine);
	}

	@Override
	public ControlFlowType getType() {
		return ControlFlowType.IF;
	}

}
