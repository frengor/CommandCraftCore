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

package com.fren_gor.commandCraftCore.utils.saveUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class DoubleMultiHashMap<K, V1, V2> implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<K, DoubleObject<V1, V2>> map = new LinkedHashMap<>();

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public void clear() {
		map.clear();
	}

	public DoubleObject<V1, V2> replace(K key, V1 value1, V2 value2) {
		return map.replace(key, new DoubleObject<>(value1, value2));
	}

	public V1 replaceValue1(K key, V1 value) {
		if (map.containsKey(key)) {
			return map.get(key).setKey(value);
		}
		return null;
	}

	public V2 replaceValue2(K key, V2 value) {
		if (map.containsKey(key)) {
			return map.get(key).setValue(value);
		}
		return null;
	}

	public int size() {
		return map.size();
	}

	public HashMap<K, DoubleObject<V1, V2>> getHashMap() {
		return map;
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public boolean containsValue1(V1 value) {
		for (DoubleObject<V1, V2> d : map.values()) {
			if (d.getKey().equals(value))
				return true;
		}
		return false;
	}

	public boolean containsValue2(V2 value) {
		for (DoubleObject<V1, V2> d : map.values()) {
			if (d.getValue().equals(value))
				return true;
		}
		return false;
	}

	public DoubleObject<V1, V2> remove(K key) {
		return map.remove(key);
	}

	public DoubleObject<V1, V2> put(K key, V1 value1, V2 value2) {
		return map.put(key, new DoubleObject<>(value1, value2));
	}

	/**
	 * 
	 * @param key
	 * @param v1
	 * @param v2
	 * @return true if the swap took place, false if the key was already
	 *         contains
	 */
	public DoubleObject<V1, V2> putIfAbsent(K key, V1 value1, V2 value2) {
		return map.putIfAbsent(key, new DoubleObject<>(value1, value2));
	}

	public List<K> keySet() {
		List<K> ol = new ArrayList<>();
		for (K o : map.keySet()) {
			ol.add(o);
		}
		return ol;
	}

	public Iterator<K> getKeysIterator() {
		return map.keySet().iterator();
	}

	public Iterator<DoubleObject<V1, V2>> getValuesIterator() {
		return map.values().iterator();
	}

	public Iterator<Entry<K, DoubleObject<V1, V2>>> getEntryIterator() {
		return map.entrySet().iterator();
	}

	public List<V1> listValue1() {
		Collection<DoubleObject<V1, V2>> c = map.values();
		List<V1> list = new ArrayList<>();
		for (DoubleObject<V1, V2> o : c) {
			list.add(o.getKey());
		}
		return list;
	}

	public List<V2> listValue2() {
		Collection<DoubleObject<V1, V2>> c = map.values();
		List<V2> list = new ArrayList<>();
		for (DoubleObject<V1, V2> o : c) {
			list.add(o.getValue());
		}
		return list;
	}

	public V1 getValue1(K key) {
		if (map.containsKey(key)) {
			return map.get(key).getKey();
		}
		return null;
	}

	public V2 getValue2(K key) {
		if (map.containsKey(key)) {
			return map.get(key).getValue();
		}
		return null;
	}

	public void saveToFile(File file) {

		if (file.exists()) {
			file.delete();
		}

		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {
			fout = new FileOutputStream(file);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(this);

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@SuppressWarnings("unchecked")
	public void loadFromFile(File file) {

		if (!file.exists()) {
			this.map = new HashMap<>();
			return;
		}

		DoubleMultiHashMap<K, V1, V2> o = null;

		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			o = (DoubleMultiHashMap<K, V1, V2>) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		this.map = o.getHashMap();

	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append("{\n");
		for (Entry<K, DoubleObject<V1, V2>> e : map.entrySet()) {

			b.append(e.getKey().toString());
			b.append(" -> ");
			b.append(e.getValue().toString());
			b.append("\n");

		}
		b.append("}");
		return b.toString();

	}

}
