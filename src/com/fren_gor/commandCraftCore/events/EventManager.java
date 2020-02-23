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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.reflections.Reflections;

import com.fren_gor.commandCraftCore.CommandCraftCore;
import com.fren_gor.commandCraftCore.ScriptType;
import com.fren_gor.commandCraftCore.reader.Reader;
import com.fren_gor.commandCraftCore.utils.saveUtils.DoubleMultiHashMap;
import com.fren_gor.commandCraftCore.utils.saveUtils.TripleConsumer;
import com.fren_gor.commandCraftCore.utils.saveUtils.TripleMultiHashMap;
import com.fren_gor.commandCraftCore.vars.VarType;
import com.fren_gor.commandCraftCore.vars.VariableManager;

public class EventManager {

	private Map<String, Map<File, NewEvent>> events;
	Map<Class<? extends Event>, TripleMultiHashMap<String, VarType, String, BiConsumer<Event, VariableManager>>> eventVars = new HashMap<>();
	DoubleMultiHashMap<Class<? extends Event>, List<String>, List<TripleConsumer<Event, VariableManager, Boolean>>> eventTasks = new DoubleMultiHashMap<>();

	public Map<String, Map<File, NewEvent>> getEvents() {
		return events;
	}

	public Map<File, NewEvent> getEvent(String eventName) {
		return events.get(eventName);
	}

	public DoubleMultiHashMap<Class<? extends Event>, List<String>, List<TripleConsumer<Event, VariableManager, Boolean>>> getEventTasks() {
		return eventTasks;
	}

	public Map<Class<? extends Event>, TripleMultiHashMap<String, VarType, String, BiConsumer<Event, VariableManager>>> getEventVars() {
		return eventVars;
	}

	// private File datafolder;

	public EventManager() {
		events = new HashMap<>();
		// datafolder = new File(CommandCraftCore.getInstance().getDataFolder(),
		// "events");
	}

	public void registerEvent(NewEvent event, File file) {

		Bukkit.getPluginManager().registerEvent(event.getEventClass(), event.getListener(),
				event.getReader().getEventPriority(), event, CommandCraftCore.getInstance());

		if (!events.containsKey(event.getName()))
			events.put(event.getName(), new HashMap<>());

		events.get(event.getName()).put(file, event);

	}

	public NewEvent buildEvent(File f) throws Exception {
		return buildEvent(new Reader(f), f);
	}

	@SuppressWarnings("unchecked")
	public NewEvent buildEvent(Reader r, File f) throws Exception {

		if (r.getType() != ScriptType.EVENT) {
			Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cFile " + f.getPath() + " is not an event file");
			throw new IllegalArgumentException("File " + f.getPath() + " is not an event file");
		}
		Class<? extends Event> clazz = null;

		if (r.getEventName().contains(".")) {
			clazz = (Class<? extends Event>) Class.forName(r.getEventName());
		} else {

			Reflections reflections = new Reflections("org.bukkit.event");

			Set<Class<? extends Event>> allClasses = reflections.getSubTypesOf(Event.class);

			for (Class<? extends Event> c : allClasses) {

				if (c.getSimpleName().equals(r.getEventName())) {

					clazz = c;
					break;

				}

			}

			if (clazz == null) {
				Reflections reflections1 = new Reflections("com.fren_gor.commandCraftCore.events.events");

				Set<Class<? extends Event>> allClasses1 = reflections1.getSubTypesOf(Event.class);

				for (Class<? extends Event> c : allClasses1) {

					if (c.getSimpleName().equals(r.getEventName())) {

						clazz = c;
						break;

					}

				}
			}
		}

		return new NewEvent(clazz, r);

	}

	public void registerEvent(Class<? extends Event> event) {
		if (!eventVars.containsKey(event))
			eventVars.put(event, new TripleMultiHashMap<>());
	}

	public void registerVariable(Class<? extends Event> event, String varName, VarType type,
			BiConsumer<Event, VariableManager> value) {
		registerVariable(event, varName, type, "", value, false);
	}

	public void registerVariable(Class<? extends Event> event, String varName, VarType type, String varDescription,
			BiConsumer<Event, VariableManager> value) {
		registerVariable(event, varName, type, varDescription, value, false);
	}

	public void registerVariable(Class<? extends Event> event, String varName, VarType type,
			BiConsumer<Event, VariableManager> value, boolean replace) {
		registerVariable(event, varName, type, "", value, replace);
	}

	public void registerVariable(Class<? extends Event> event, String varName, VarType type, String varDescription,
			BiConsumer<Event, VariableManager> value, boolean replace) {

		if (!varName.startsWith("$"))
			varName = "$" + varName;

		if (varName.equals("$cancelled")) {
			throw new IllegalArgumentException("Variable name '$cancelled' is reserved");
		}

		if (!eventVars.containsKey(event))
			eventVars.put(event, new TripleMultiHashMap<>());

		if (replace)
			eventVars.get(event).put(varName, type, varDescription, value);
		else
			eventVars.get(event).putIfAbsent(varName, type, varDescription, value);
	}

	public void registerEventTask(Class<? extends Event> event, TripleConsumer<Event, VariableManager, Boolean> task,
			String... description) {

		if (!eventTasks.containsKey(event)) {
			eventTasks.put(event, new LinkedList<>(), new LinkedList<>());
		}

		for (String de : description)
			eventTasks.getValue1(event).add(de);
		eventTasks.getValue2(event).add(task);

	}

	public void registerEventDescription(Class<? extends Event> event, String... description) {

		if (!eventTasks.containsKey(event)) {
			eventTasks.put(event, new LinkedList<>(), new LinkedList<>());
		}

		for (String de : description)
			eventTasks.getValue1(event).add(de);

	}

	public static List<String> readFile(File file) throws IOException {
		List<String> l = new ArrayList<>();
		BufferedReader reader = null;
		String currentLine = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

			while ((currentLine = reader.readLine()) != null) {
				l.add(currentLine);
			}
		} finally {

			reader.close();

		}
		return l;
	}

	public static VarType getType(Object value) {
		VarType t;
		if (value == null) {
			t = VarType.NULL;
		} else if (value instanceof Integer) {
			t = VarType.INT;
		} else if (value instanceof Double) {
			t = VarType.DOUBLE;
		} else if (value instanceof List) {
			t = VarType.LIST;
		} else if (value instanceof String) {
			t = VarType.STRING;
		} else if (value instanceof Boolean) {
			t = VarType.BOOLEAN;
		} else {
			throw new IllegalArgumentException("Illegal value type " + value.getClass().getSimpleName());
		}
		return t;
	}

	public List<String> getRegistredEvents() {
		List<String> l = new LinkedList<>();
		for (Class<? extends Event> d : eventVars.keySet()) {
			l.add(d.getName().startsWith("org.bukkit.event")
					|| d.getName().startsWith("com.fren_gor.commandCraftCore.events.events") ? d.getSimpleName()
							: d.getName());
		}
		return l;
	}
}
