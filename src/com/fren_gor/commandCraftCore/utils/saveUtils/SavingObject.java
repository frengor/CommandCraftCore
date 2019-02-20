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

public final class SavingObject<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T o;

	public T getObject() {
		return o;
	}

	public SavingObject(T o) {

		this.o = o;

	}

	public SavingObject(File file) {

		load(file);

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
			o = null;
			return false;
		}
		SavingObject<T> o1;
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(file);
			ois = new ObjectInputStream(fin);
			o1 = (SavingObject<T>) ois.readObject();

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
		o = o1.getObject();

		return true;
	}

}
