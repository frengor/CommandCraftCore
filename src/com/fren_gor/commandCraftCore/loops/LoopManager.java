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

package com.fren_gor.commandCraftCore.loops;

import java.io.File;
import java.io.IOException;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.Reader.Type;
import com.fren_gor.commandCraftCore.commands.CommandManager;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleMultiHashMap;

public class LoopManager {

	private DoubleMultiHashMap<String, NewLoop, File> loops;

	private File datafolder;

	public LoopManager() {

		loops = new DoubleMultiHashMap<>();
		datafolder = new File(CommandCraftCore.getInstance().getDataFolder(), "loops");

	}

	public void registerLoop(NewLoop loop, File f) {

		if (loop.getReader().getType() != Type.LOOP)
			return;

		loops.put(loop.getName(), loop, f);

	}

	public DoubleMultiHashMap<String, NewLoop, File> getLoops() {
		return loops;
	}

	public NewLoop buildLoop(File f) {
		try {
			return new NewLoop(new Reader(CommandManager.readFile(f), f));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
