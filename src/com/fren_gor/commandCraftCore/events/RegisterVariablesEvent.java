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

import java.util.function.BiConsumer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.utils.saveUtils.TripleConsumer;
import com.fren_gor.commandCraftCore.vars.StaticMethod;
import com.fren_gor.commandCraftCore.vars.StaticMethods;
import com.fren_gor.commandCraftCore.vars.Type;
import com.fren_gor.commandCraftCore.vars.Variable;
import com.fren_gor.commandCraftCore.vars.VariableManager;

public final class RegisterVariablesEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Register a {@link Variable} for an {@link Event}
	 * 
	 * @param event
	 *            The class of the event
	 * @param varName
	 *            The var name
	 * @param type
	 *            The var type
	 * @param value
	 *            The Variable
	 */
	public void registerVariable(Class<? extends Event> event, String varName, Type type,
			BiConsumer<Event, VariableManager> value) {
		CommandCraftCore.getEventManager().registerVariable(event, varName, type, value);
	}

	/**
	 * Register a {@link Variable} for an {@link Event}
	 * 
	 * @param event
	 *            The class of the event
	 * @param varName
	 *            The var name
	 * @param type
	 *            The var type
	 * @param varDescription
	 *            The var description
	 * @param value
	 *            The Variable
	 */
	public void registerVariable(Class<? extends Event> event, String varName, Type type, String varDescription,
			BiConsumer<Event, VariableManager> value) {
		CommandCraftCore.getEventManager().registerVariable(event, varName, type, varDescription, value, false);
	}

	/**
	 * Register a {@link Variable} for an {@link Event}
	 * 
	 * @param event
	 *            The class of the event
	 * @param varName
	 *            The var name
	 * @param type
	 *            The var type
	 * @param value
	 *            The Variable
	 * @param replace
	 *            Indicates whether to replace a variable if one already exists
	 *            with the same name
	 */
	public void registerVariable(Class<? extends Event> event, String varName, Type type,
			BiConsumer<Event, VariableManager> value, boolean replace) {
		CommandCraftCore.getEventManager().registerVariable(event, varName, type, value, replace);
	}

	/**
	 * Register a {@link Variable} for an {@link Event}
	 * 
	 * @param event
	 *            The class of the event
	 * @param varName
	 *            The var name
	 * @param type
	 *            The var type
	 * @param varDescription
	 *            The var description
	 * @param value
	 *            The Variable
	 * @param replace
	 *            Indicates whether to replace a variable if one already exists
	 *            with the same name
	 */
	public void registerVariable(Class<? extends Event> event, String varName, Type type, String varDescription,
			BiConsumer<Event, VariableManager> value, boolean replace) {
		CommandCraftCore.getEventManager().registerVariable(event, varName, type, varDescription, value, replace);
	}

	/**
	 * Register a {@link StaticMethod} like "@Bukkit#getPlayer"
	 * 
	 * @param key
	 *            The Main name (in the example: Bukkit)
	 * @param value
	 *            The {@link StaticMethod}
	 */
	public void registerStaticVariable(String key, StaticMethod value) {
		StaticMethods.register(key, value);
	}

	/**
	 * Register an {@link Event}
	 * 
	 * @param event
	 *            The event to register
	 */
	public void registerEvent(Class<? extends Event> event) {
		CommandCraftCore.getEventManager().registerEvent(event);
	}

	/**
	 * Register a task to run after an {@link Event}
	 * 
	 * @param event
	 *            The event to register
	 * @param description
	 *            The description showed when users use /createscript command
	 * @param task
	 *            The action to do
	 */
	public void registerEventTask(Class<? extends Event> event, TripleConsumer<Event, VariableManager, Boolean> task,
			String... description) {
		CommandCraftCore.getEventManager().registerEventTask(event, task, description);
	}

}
