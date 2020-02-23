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

import lombok.Getter;

public class ElseStatement implements ControlFlowStatement {

	@Getter
	private final List<GotoLine> gotoLines;

	public ElseStatement(GotoLine newGotoLine, GotoLine... oldGotoLine) {
		this.gotoLines = new LinkedList<>();
		Collections.addAll(this.gotoLines, oldGotoLine);
		this.gotoLines.add(newGotoLine);
	}

	public ElseStatement(GotoLine newGotoLine, List<GotoLine> oldGotoLine) {
		this.gotoLines = oldGotoLine;
		this.gotoLines.add(newGotoLine);
	}

	public ElseStatement(GotoLine newGotoLine) {
		this.gotoLines = new LinkedList<>();
		this.gotoLines.add(newGotoLine);
	}

	public ElseStatement(GotoLine... gotoLine) {
		this.gotoLines = new LinkedList<>();
		Collections.addAll(this.gotoLines, gotoLine);
	}

	public ElseStatement(List<GotoLine> gotoLine) {
		this.gotoLines = gotoLine;
	}

	@Override
	public ControlFlowType getType() {
		return ControlFlowType.ELSE;
	}

}
