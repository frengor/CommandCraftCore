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
import java.util.Map.Entry;

public class DoubleObject<K, V> implements Serializable, Entry<K, V> {

	private static final long serialVersionUID = 1L;
	private K o1;
	private V o2;

	public DoubleObject() {
	}

	public DoubleObject(K key, V value) {
		put(key, value);
	}

	public void put(K key, V value) {
		this.o1 = key;
		this.o2 = value;
	}

	public void clear() {
		o1 = null;
		o2 = null;
	}

	public K setKey(K key) {
		K o = o1;
		this.o1 = key;
		return o;
	}

	@Override
	public V setValue(V value) {
		V o = o2;
		this.o2 = value;
		return o;
	}

	@Override
	public K getKey() {
		return o1;
	}

	@Override
	public V getValue() {
		return o2;
	}

	public boolean save(File file) {

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {

			fout = new FileOutputStream(file);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(this);

		} catch (Exception ex) {

			ex.printStackTrace();
			return false;
		} finally {

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {

					e.printStackTrace();
					return false;
				}
			}

			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean load(File file) {

		if (!file.exists()) {
			this.o1 = null;
			this.o2 = null;
			return false;
		}
		DoubleObject<K, V> o1;
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			o1 = (DoubleObject<K, V>) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {

			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

		}

		this.o1 = o1.o1;
		this.o2 = o1.o2;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("<");
		b.append(o1.toString());
		b.append(">, <");
		b.append(o2.toString());
		b.append(">");
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((o1 == null) ? 0 : o1.hashCode());
		result = prime * result + ((o2 == null) ? 0 : o2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DoubleObject))
			return false;
		DoubleObject<?, ?> other = (DoubleObject<?, ?>) obj;
		if (o1 == null) {
			if (other.o1 != null)
				return false;
		} else if (!o1.equals(other.o1))
			return false;
		if (o2 == null) {
			if (other.o2 != null)
				return false;
		} else if (!o2.equals(other.o2))
			return false;
		return true;
	}

}
