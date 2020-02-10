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

package com.fren_gor.commandCraftCore.vars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import lombok.Getter;

public class ByteArrayDataInputVar extends Variable {

	@Getter
	private ByteArrayDataInput value;
	private byte[] bytes;

	public byte[] getBytes() {

		byte[] b = new byte[bytes.length];
		System.arraycopy(bytes, 0, b, 0, bytes.length);
		return b;

	}

	@Getter
	private static Set<String> list = Collections.unmodifiableSet(Sets.newHashSet("toString", "==", "equals", "=", "!=",
			"type", "readBoolean", "readByte", "readChar", "readDouble", "readFloat", "readFully", "readInt",
			"readLine", "readLong", "readShort", "readUnsignedByte", "readUnsignedShort", "readUTF", "skipBytes"));

	private ByteArrayDataInputVar(VariableManager m, String name, ByteArrayDataInput value, byte[] bytes) {
		super(m, name);
		this.value = value;
		this.bytes = bytes;
	}

	public ByteArrayDataInputVar(VariableManager m, String name, byte[] bytes) {
		super(m, name);
		this.value = ByteStreams.newDataInput(bytes);
		this.bytes = bytes;
	}

	@Override
	public VarType getType() {
		return VarType.BYTE_ARRAY_DATA_INPUT;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("0x%02X ", b));
		}
		return sb.toString();
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public Set<String> getMethods() {
		return list;
	}

	@Override
	public Variable invoke(String method, Variable parameter) {
		switch (method) {
			case "type":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), "BYTE_ARRAY_DATA_INPUT");
			case "toString":
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), toString());

			case "!=": {
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != VarType.BYTE_ARRAY_DATA_INPUT
						|| ((ByteArrayDataInputVar) parameter).bytes.length != bytes.length)
					return new BooleanVar(manager, manager.generateInternalName(), true);
				byte[] b = ((ByteArrayDataInputVar) parameter).bytes;
				for (int i = 0; i < bytes.length; i++) {
					if (b[i] != bytes[i])
						return new BooleanVar(manager, manager.generateInternalName(), true);
				}
				return new BooleanVar(manager, manager.generateInternalName(), false);
			}
			case "=": {
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (isFinal()) {
					throw new RuntimeException("Cannot modify a final variable");
				}
				if (isConst() && VarType.BYTE_ARRAY_DATA_INPUT != parameter.getType())
					throw new IllegalArgumentException("Cannot change " + name + "'s variable type");
				if (parameter.getType() == VarType.BYTE_ARRAY_DATA_INPUT) {
					bytes = ((ByteArrayDataInputVar) parameter).bytes;
					value = ((ByteArrayDataInputVar) parameter).value;
					return this;
				}
				manager.vars.remove(getName());
				return manager.craftVariable(name, parameter.get(), parameter.getType());
			}
			case "==":
			case "equals": {
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != VarType.BYTE_ARRAY_DATA_INPUT
						|| ((ByteArrayDataInputVar) parameter).bytes.length != bytes.length)
					return new BooleanVar(manager, manager.generateInternalName(), false);
				byte[] b = ((ByteArrayDataInputVar) parameter).bytes;
				for (int i = 0; i < bytes.length; i++) {
					if (b[i] != bytes[i])
						return new BooleanVar(manager, manager.generateInternalName(), false);
				}
				return new BooleanVar(manager, manager.generateInternalName(), true);
			}
			case "readBoolean": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new BooleanVar(manager, manager.generateInternalName(), value.readBoolean());
			}
			case "readByte": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.readByte());
			}
			case "readChar": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), value.readUTF());
			}
			case "readDouble": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), value.readDouble());
			}
			case "readFloat": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), value.readFloat());
			}
			case "readFully": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				ListVar l = new ListVar(manager, manager.generateInternalName(), new ArrayList<Variable>(bytes.length));
				for (byte b : bytes)
					l.add(new IntVar(manager, manager.generateInternalName(), b));
				l.setFinal();
				return l;
			}
			case "readInt": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.readInt());
			}
			case "readLine": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), value.readLine());
			}
			case "readLong": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new DoubleVar(manager, manager.generateInternalName(), value.readLong());
			}
			case "readShort": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.readShort());
			}
			case "readUnsignedByte": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.readUnsignedByte());
			}
			case "readUnsignedShort": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new IntVar(manager, manager.generateInternalName(), value.readUnsignedShort());
			}
			case "readUTF": {
				if (parameter != null)
					throw new IllegalArgumentException("The method ' " + method + " ' cannot have any parameters");
				return new StringVar(manager, manager.generateInternalName(), value.readUTF());
			}
			case "skipBytes": {
				if (parameter == null)
					throw new IllegalArgumentException("The method ' " + method + " ' must have a parameter");
				if (parameter.getType() != VarType.INT)
					throw new IllegalArgumentException(parameter.getName() + " is not an integer variable");
				value.skipBytes((int) parameter.get());
			}
			default:
				throw new IllegalArgumentException("Method '" + method + "' not exists");
		}
	}

	@Override
	public ByteArrayDataInputVar clone() {
		return new ByteArrayDataInputVar(manager, manager.generateInternalName(), value, bytes);
	}

}
