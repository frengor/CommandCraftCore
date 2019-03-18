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

package com.fren_gor.commandCraftCore.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fren_gor.commandCraftCore.vars.Variable;

public class VariableMap implements Map<String, Variable> {

	// For internal vars
	private Map<String, Variable> map = new LinkedHashMap<>();

	// For external vars
	private Map<String, Variable> cleanMap = new LinkedHashMap<>();

	public void clearInternals() {
		map.clear();
	}

	@Override
	public Set<Entry<String, Variable>> entrySet() {
		return cleanMap.entrySet();
	}

	public Set<Entry<String, Variable>> internalsEntrySet() {
		return map.entrySet();
	}

	@Override
	public void clear() {
		map.clear();
		cleanMap.clear();
	}

	public void clear(boolean ignoreFinals) {
		map.clear();
		if (ignoreFinals) {
			cleanMap.clear();
		} else {
			Iterator<Entry<String, Variable>> it = cleanMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Variable> e = it.next();

				if (!e.getValue().isFinal()) {
					it.remove();
				}
			}
		}
	}

	@Override
	public boolean containsKey(Object s) {
		if (toReturn(s))
			return false;

		if (isInternal((String) s))
			return map.containsKey(s);
		return cleanMap.containsKey(s);
	}

	@Override
	public boolean containsValue(Object s) {
		if (toReturn(s))
			return false;

		if (isInternal((String) s))
			return map.containsValue(s);
		return cleanMap.containsValue(s);
	}

	@Override
	public Variable get(Object s) {
		if (toReturn(s))
			return null;

		if (isInternal((String) s))
			return map.get(s);
		return cleanMap.get(s);
	}

	@Override
	public boolean isEmpty() {
		return cleanMap.isEmpty();
	}

	public boolean isReallyEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return cleanMap.keySet();
	}

	public Set<String> fullKeySet() {
		return map.keySet();
	}

	@Override
	public Variable put(String s, Variable v) {

		if (isInternal(s))
			return map.put(s, v);

		Variable va = cleanMap.put(s, v);
		sort();
		return va;

	}

	public void sort() {

		List<Entry<String, Variable>> entries = new ArrayList<>(cleanMap.entrySet());
		Collections.sort(entries, (s1, s2) -> {
			if (s1.getKey().length() == s2.getKey().length())
				return 0;
			if (s1.getKey().length() > s2.getKey().length())
				return -1;
			return 1;
		});

		cleanMap.clear();
		for (Map.Entry<String, Variable> entry : entries) {
			cleanMap.put(entry.getKey(), entry.getValue());
		}

	}

	@Override
	public void putAll(Map<? extends String, ? extends Variable> var1) {
		for (Entry<? extends String, ? extends Variable> e : var1.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public Variable remove(Object s) {

		if (toReturn(s))
			return null;

		if (isInternal((String) s))
			return map.remove(s);
		return cleanMap.remove(s);

	}

	@Override
	public int size() {
		return cleanMap.size();
	}

	public int internalsSize() {
		return map.size();
	}

	@Override
	public Collection<Variable> values() {
		return cleanMap.values();
	}

	public Collection<Variable> internalsValues() {
		return map.values();
	}

	private boolean toReturn(Object s) {
		return !(s instanceof String);
	}

	private boolean isInternal(String s) {
		return s.startsWith("$internal_");
	}

}
