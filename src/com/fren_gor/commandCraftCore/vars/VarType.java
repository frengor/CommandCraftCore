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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.commons.lang.Validate;

import com.fren_gor.commandCraftCore.vars.exceptions.VariableException;

import lombok.Getter;

public final class VarType implements Serializable {

	private static final long serialVersionUID = -7540806303326960657L;

	public transient static final VarType INT, DOUBLE, STRING, BOOLEAN, LIST, PLAYER, NULL, STRUCT,
			BYTE_ARRAY_DATA_INPUT;
	private static final String INVALID_CHARS = ";: \t\n\r\b\f\0";

	private transient static Map<String, VarType> map = new ConcurrentHashMap<>();

	/**
	 * 
	 * @param varTypeName
	 *            The {@link VarType} name
	 * @param getMethodNames
	 *            A {@link Supplier} that return a full list of the available
	 *            methods for that {@link VarType}
	 * @throws IllegalArgumentException
	 *             If that {@link VarType} has already been registered
	 */
	public static VarType registerNewVariableType(String varTypeName, Supplier<Set<String>> getMethodNames) {

		varTypeName = varTypeName.toUpperCase();

		Validate.notNull(map.containsKey(varTypeName),
				"Variable type '" + varTypeName + "' has already been registered");

		if (!varTypeName.equals("NULL") && !getMethodNames.get().containsAll(NullVar.getList()))
			throw new VariableException("VarType '" + varTypeName
					+ "' doesn't implement all the mandatory methods 'toString', '==', 'equals', '=', '!=', 'type'");

		for (char c : varTypeName.toCharArray()) {
			if (INVALID_CHARS.contains(String.valueOf(c))) {
				throw new VariableException("Invalid VarType name '" + varTypeName);
			}
		}

		for (String s : getMethodNames.get())
			for (char c : s.toCharArray()) {
				if (INVALID_CHARS.contains(String.valueOf(c))) {
					throw new VariableException("Invalid method name '" + varTypeName);
				}
			}

		return new VarType(varTypeName, getMethodNames);

	}

	/**
	 * Get a var type through its name
	 * 
	 * @param varTypeName
	 *            The {@link VarType} name
	 * @return The corresponding {@link VarType}
	 * @throws IllegalArgumentException
	 *             If that type has already been registered
	 */
	public static VarType getType(String varTypeName) {
		varTypeName = varTypeName.toUpperCase();
		VarType t = map.get(varTypeName);

		if (t != null)
			return t;
		throw new IllegalArgumentException("Variable type '" + varTypeName + "' hasn't yet been registered");

	}

	/**
	 * Get all the {@link VarType} actually registered
	 * 
	 * @return A collection containing all the {@link VarType} registered
	 */
	public static Collection<VarType> getRegisteredTypes() {

		return Collections.unmodifiableCollection(map.values());

	}

	static {
		INT = registerNewVariableType("INT", () -> IntVar.getList());
		DOUBLE = registerNewVariableType("DOUBLE", () -> DoubleVar.getList());
		STRING = registerNewVariableType("STRING", () -> StringVar.getList());
		BOOLEAN = registerNewVariableType("BOOLEAN", () -> BooleanVar.getList());
		LIST = registerNewVariableType("LIST", () -> ListVar.getList());
		NULL = registerNewVariableType("NULL", () -> NullVar.getList());
		STRUCT = registerNewVariableType("STRUCT", () -> StructVar.getList());
		PLAYER = registerNewVariableType("PLAYER", () -> PlayerVar.getList());
		BYTE_ARRAY_DATA_INPUT = registerNewVariableType("BYTE_ARRAY_DATA_INPUT", () -> StructVar.getList());
	}

	private transient Supplier<Set<String>> f;
	@Getter
	private String name;

	private VarType(String name, Supplier<Set<String>> f) {
		this.f = f;
		this.name = name.toUpperCase();
		map.put(this.name, this);
	}

	public Set<String> getMethods() {
		return f.get();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VarType))
			return false;
		VarType other = (VarType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name.toUpperCase()))
			return false;
		return true;
	}

}