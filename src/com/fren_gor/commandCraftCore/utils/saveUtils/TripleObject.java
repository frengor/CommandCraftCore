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

public class TripleObject<K, V1, V2> implements Serializable {

	private static final long serialVersionUID = 1L;
	private K o1;
	private V1 o2;
	private V2 o3;

	public TripleObject() {
	}

	public TripleObject(K key, V1 value1, V2 value2) {
		put(key, value1, value2);
	}

	public void put(K key, V1 value1, V2 value2) {
		this.o1 = key;
		this.o2 = value1;
		this.o3 = value2;
	}

	public void clear() {
		o1 = null;
		o2 = null;
		o3 = null;
	}

	public K setKey(K key) {
		K o = o1;
		this.o1 = key;
		return o;
	}

	public V1 setValue1(V1 value) {
		V1 o = o2;
		this.o2 = value;
		return o;
	}

	public V2 setValue2(V2 value) {
		V2 o = o3;
		this.o3 = value;
		return o;
	}

	public K getKey() {
		return o1;
	}

	public V1 getValue1() {
		return o2;
	}

	public V2 getValue2() {
		return o3;
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
		TripleObject<K, V1, V2> o1;
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			o1 = (TripleObject<K, V1, V2>) ois.readObject();

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
		this.o3 = o1.o3;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(">");
		b.append(o1.toString());
		b.append(">, <");
		b.append(o2.toString());
		b.append(">, <");
		b.append(o3.toString());
		b.append("<");
		return b.toString();
	}
}
