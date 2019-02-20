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

import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.Executor;
import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.Reader.Type;

public class NewLoop extends BukkitRunnable {

	private final String name;
	private final Reader r;

	public String getName() {
		return name;
	}

	public Reader getReader() {
		return r;
	}

	public NewLoop(Reader r) {

		this.r = r;

		if (r.getType() != Type.LOOP)
			throw new IllegalArgumentException("Invalid type :" + r.getType() + ". Only LOOP is admitted!");

		this.name = r.getName();

		if (r.getLoopInfo().getValue() == Integer.MIN_VALUE) {

			runTaskLater(CommandCraftCore.getInstance(), r.getLoopInfo().getKey());

		} else {

			runTaskTimer(CommandCraftCore.getInstance(), r.getLoopInfo().getKey(), r.getLoopInfo().getValue());

		}

	}

	@Override
	public void run() {

		new Executor().execute(r);

	}

	public synchronized void unregister() {

		if (CommandCraftCore.getLoopManager().getLoops().containsKey(name)) {
			CommandCraftCore.getLoopManager().getLoops().remove(name);
		}

		cancel();

	}

}
