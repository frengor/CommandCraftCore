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

package com.fren_gor.commandCraftCore.events;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.ScriptType;
import com.fren_gor.commandCraftCore.executor.Executor;
import com.fren_gor.commandCraftCore.utils.saveUtils.TripleConsumer;
import com.fren_gor.commandCraftCore.vars.BooleanVar;
import com.fren_gor.commandCraftCore.vars.VariableManager;

public class NewEvent implements EventExecutor {

	private final Reader r;
	private final String name;
	private Listener listener = new Listener() {
	};

	public Listener getListener() {
		return listener;
	}

	private final Class<? extends Event> clazz;

	public Reader getReader() {
		return r;
	}

	public NewEvent(Class<? extends Event> clazz, Reader r) {

		if (r.getType() != ScriptType.EVENT)
			throw new IllegalArgumentException("Invalid type :" + r.getType() + ". Only EVENT is admitted!");

		this.clazz = clazz;
		this.r = r;
		this.name = r.getScriptName();

	}

	public Class<? extends Event> getEventClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	@Override
	public void execute(Listener l, Event e) throws EventException {

		Executor ex = new Executor(r);

		new BooleanVar(ex.getManager(), "$cancelled",
				e instanceof Cancellable ? ((Cancellable) e).isCancelled() : false);

		if (CommandCraftCore.getEventManager().eventVars.containsKey(clazz)
				&& !CommandCraftCore.getEventManager().eventVars.get(clazz).isEmpty()) {
			for (BiConsumer<Event, VariableManager> c : CommandCraftCore.getEventManager().eventVars.get(clazz)
					.listValue3()) {
				c.accept(e, ex.getManager());
			}
		}
		boolean canc = ex.execute();
		if (canc && e instanceof Cancellable)
			((Cancellable) e).setCancelled(true);

		if (CommandCraftCore.getEventManager().getEventTasks().containsKey(clazz)
				&& !CommandCraftCore.getEventManager().getEventTasks().getValue2(clazz).isEmpty()) {
			for (TripleConsumer<Event, VariableManager, Boolean> t : CommandCraftCore.getEventManager().getEventTasks()
					.getValue2(clazz)) {

				t.accept(e, ex.getManager(), canc);

			}
		}

	}

	public void unregister() {
		unregister(true);
	}

	public void unregister(boolean remove) {

		if (!CommandCraftCore.getEventManager().getEvents().containsKey(name))
			return;

		try {
			HandlerList h = (HandlerList) clazz.getMethod("getHandlerList", new Class<?>[0]).invoke(null);
			h.unregister(listener);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}

		if (remove)
			CommandCraftCore.getEventManager().getEvents().remove(name);
		CommandCraftCore.getEventManager().getEventVars().remove(getEventClass());
		CommandCraftCore.getEventManager().getEventTasks().remove(getEventClass());

	}

}
