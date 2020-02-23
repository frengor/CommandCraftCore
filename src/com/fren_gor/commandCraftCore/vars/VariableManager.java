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

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import com.fren_gor.commandCraftCore.reader.conditions.CommandCondition;
import com.fren_gor.commandCraftCore.utils.VariableMap;
import com.fren_gor.commandCraftCore.vars.exceptions.VariableException;

import lombok.Getter;

/**
 * VariableManager Class
 * 
 * @author fren_gor
 *
 */
public final class VariableManager implements Cloneable {

	private int internalVars = 0;

	@Getter
	VariableMap vars = new VariableMap();

	@Getter
	public final StaticActions staticVars;

	public final String generateInternalName() {
		return "$internal_" + internalVars++;
	}

	private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
	// private static final Pattern ALLOWED_CHARS_PATTERN =
	// Pattern.compile("^[${1}](?!internal_)[A-Za-z_]\\w*$");

	public final Variable changeName(Variable var, String newName) {

		if (!newName.startsWith("$"))
			newName = "$" + newName;

		Variable newVar = ((Variable) var.clone());
		vars.remove(newVar.name);
		vars.put(newName, newVar);
		newVar.name = newName;
		return newVar;

	}

	/**
	 * It verifies if the name of the variable is correct
	 * 
	 * @param name
	 *            The variable name
	 * @return if the variable name is correct
	 * @throws IllegalArgumentException
	 *             If the name starts with 'internal_' or if the name doesn't
	 *             start with '$'
	 */
	public static boolean verifyName(String name) throws IllegalArgumentException {
		if (!name.startsWith("$")) {
			throw new IllegalArgumentException("Variables must start with '$'");
		}
		if (name.substring(1).contains("$")) {
			throw new IllegalArgumentException("Variables must have only one '$' in front of the name");
		}
		if (name.startsWith("$internal_")) {
			throw new IllegalArgumentException("Variables name cannot start with 'internal_'");
		}
		for (char c : name.substring(1).toCharArray()) {
			if (!ALLOWED_CHARS.contains(String.valueOf(c))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Just the constructor
	 */
	public VariableManager() {
		staticVars = new StaticActions(this);
	}

	public void print() {

		for (Entry<String, Variable> s : vars.entrySet()) {
			System.out.println(s.getKey() + " -> " + s.getValue().toString());
		}

	}

	/**
	 * Get the variable with name 'name'
	 * 
	 * @param name
	 *            The variable name
	 * @return The variable if it exists, else a new NullVar
	 */
	public Variable getVar(String name) {

		name = name.startsWith("$") ? name.trim() : "$" + name.trim();

		Validate.isTrue(verifyName(name), "Illegal Name " + name);

		Variable var = vars.get(name);

		if (var == null) {
			return new NullVar(this, name);
		}
		return var;

	}

	/**
	 * Create a variable if it does not exist
	 * 
	 * @param name
	 *            The variable name
	 * @param value
	 *            The initial value of this variable
	 * @return The variable with the correct value
	 */
	public Variable craftVar(String name, String value) {

		name = name.startsWith("$") ? name.trim() : "$" + name.trim();

		// Validate.isTrue(verifyName(name), "Illegal Name " + name);

		if (vars.containsKey(name))
			return vars.get(name);

		value = value.trim();

		if (value.isEmpty()) {
			return new NullVar(this, name);
		}

		if (value.startsWith("@")) {
			return staticVars.getVariable(split(value));
		}

		if (value.startsWith("$")) {
			for (Entry<String, Variable> e : vars.entrySet()) {
				if (value.equals(e.getKey()))
					return craftVariable(generateInternalName(), e.getValue().get(), e.getValue().getType());
			}
			return new NullVar(this, name);
		}

		if (value.startsWith("/") || value.startsWith("\\") || value.startsWith("(")) {

			return new BooleanVar(this, generateInternalName(), new CommandCondition(value).execute(this));

		}

		if (value.equalsIgnoreCase("true")) {
			return new BooleanVar(this, name, true);
		}
		if (value.equalsIgnoreCase("false")) {
			return new BooleanVar(this, name, false);
		}
		if (value.equalsIgnoreCase("null")) {
			return new NullVar(this, name);
		}
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return new StringVar(this, name, value.substring(1, value.length() - 1));
		}
		if (value.startsWith("{") && value.endsWith("}")) {
			/*
			 * if (containsOnly(value.substring(1, value.length() - 1), ' ')) {
			 * return new ListVar(this, name, new LinkedList<>()); }
			 */
			return new StructVar(this, name, splitStruct(value.substring(1, value.length() - 1)));
		}
		if (value.startsWith("[") && value.endsWith("]")) {
			/*
			 * if (containsOnly(value.substring(1, value.length() - 1), ' ')) {
			 * return new ListVar(this, name, new LinkedList<>()); }
			 */
			return new ListVar(this, name, splitList(value.substring(1, value.length() - 1)));
		}

		VarType t = VarType.INT;

		boolean min = false;
		if (value.startsWith("-")) {
			min = true;
			value = value.substring(1);
		}

		for (char c : value.toCharArray()) {

			if (c == '.') {
				t = VarType.DOUBLE;
				continue;
			}

			if (!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7'
					|| c == '8' || c == '9')) {

				throw new IllegalArgumentException("Illegal var value '" + value + "'");

			}

		}

		if (t == VarType.INT) {
			return new IntVar(this, name, (min ? -1 : 1) * Integer.parseInt(value));
		}

		if (t == VarType.DOUBLE) {
			return new DoubleVar(this, name, (min ? -1 : 1) * Double.parseDouble(value));
		}
		return new NullVar(this, name);
	}

	private List<Variable> splitStruct(String o) {

		if (o.replace(" ", "").replace("\t", "").isEmpty()) {
			return new LinkedList<>();
		}

		boolean inString = false;
		boolean next = false;

		List<Variable> split = new LinkedList<>();

		StringBuilder a = new StringBuilder();

		for (int i = 0; i < o.length(); i++) {
			char c = o.charAt(i);
			if (next) {
				a.append(c);
				next = false;
				continue;
			}

			if (c == '\\' && inString) {
				next = true;
				continue;
			}
			if (c == '"') {
				a.append("\"");
				inString = !inString;
				continue;
			}

			if (inString) {
				a.append(c);
				continue;
			}

			if (c == '{') {
				a = new StringBuilder();
				int opens = 1;

				StringBuilder b = new StringBuilder("{");
				i++;
				for (; i < o.length(); i++) {
					char cc = o.charAt(i);
					if (cc == '{') {
						opens++;
					} else if (cc == '}') {
						opens--;
					}
					b.append(cc);
					if (opens == 0)
						break;
				}

				if (opens > 0) {
					throw new IllegalArgumentException("Unbalanced { }");
				}

				// if (b.toString().trim().length() != 0){System.out.println("1
				// true: " + b.toString());
				split.add(craftVar(generateInternalName(), b.toString()));// }else{System.out.println("1
																			// false");}
				continue;
			}

			if (c == '[') {
				a = new StringBuilder();
				int opens = 1;

				StringBuilder b = new StringBuilder("[");
				i++;
				for (; i < o.length(); i++) {
					char cc = o.charAt(i);
					if (cc == '[') {
						opens++;
					} else if (cc == ']') {
						opens--;
					}
					b.append(cc);
					if (opens == 0)
						break;
				}

				if (opens > 0) {
					throw new IllegalArgumentException("Unbalanced [ ]");
				}

				// if (b.toString().trim().length() != 0){System.out.println("2
				// true: " + b.toString());
				split.add(craftVar(generateInternalName(), b.toString()));// }else{System.out.println("2
																			// false");}
				continue;

			}

			if (c == ',') {
				if (a.toString().trim().length() != 0)// {System.out.println("3
														// true: " +
														// a.toString());
					split.add(craftVar(generateInternalName(), a.toString()));// }else{System.out.println("3
																				// false");}
				a = new StringBuilder();
				continue;

			}

			a.append(c);

		}

		if (inString)
			throw new IllegalArgumentException(
					"Illegal var value '" + split.get(split.size() - 1) + "': unbalanced \" \"");

		if (a.toString().trim().length() != 0)// {System.out.println("4 true: "
												// + a.toString());
			split.add(craftVar(generateInternalName(), a.toString()));// }else{System.out.println("4
																		// false");}

		return split;

	}

	private List<Variable> splitList(String o) {

		if (o.replace(" ", "").replace("\t", "").isEmpty()) {
			return new LinkedList<>();
		}

		boolean inString = false;
		boolean next = false;

		List<Variable> split = new LinkedList<>();

		StringBuilder a = new StringBuilder();

		for (int i = 0; i < o.length(); i++) {

			char c = o.charAt(i);

			if (next) {
				a.append(c);
				next = false;
				continue;
			}
			if (c == '\\' && inString) {
				next = true;
				continue;
			}
			if (c == '"') {
				a.append("\"");
				inString = !inString;
				continue;
			}

			if (inString) {
				a.append(c);
				continue;
			}

			if (c == '{') {
				a = new StringBuilder();
				int opens = 1;

				StringBuilder b = new StringBuilder("{");
				i++;
				for (; i < o.length(); i++) {
					char cc = o.charAt(i);
					if (cc == '{') {
						opens++;
					} else if (cc == '}') {
						opens--;
					}
					b.append(cc);
					if (opens == 0)
						break;
				}

				if (opens > 0) {
					throw new IllegalArgumentException("Unbalanced { }");
				}

				// if (b.toString().trim().length() != 0){System.out.println("5
				// true: " + b.toString());
				split.add(craftVar(generateInternalName(), b.toString()));// }else{System.out.println("5
																			// false");}
				continue;
			}

			if (c == ',') {
				if (a.toString().trim().length() != 0)// {System.out.println("6
														// true: " +
														// a.toString());
					split.add(craftVar(generateInternalName(), a.toString()));// }else{System.out.println("6
																				// false");}
				a = new StringBuilder();
				continue;

			}

			a.append(c);

		}

		if (inString)
			throw new IllegalArgumentException(
					"Illegal var value '" + split.get(split.size() - 1) + "': unbalanced \" \"");

		if (a.toString().trim().length() != 0)// {System.out.println("7 true: "
												// + a.toString());
			split.add(craftVar(generateInternalName(), a.toString()));// }else{System.out.println("7
																		// false");}

		VarType t = null;

		for (Variable s : split) {
			if (t == null) {
				t = s.getType();
			} else if (s.getType() != t) {
				throw new IllegalArgumentException("Illegal list type '" + s.getType() + "'! It should be '" + t + "'");
			}

		}

		return split;

	}

	/**
	 * Execute a statement
	 * 
	 * @param o
	 *            The {@link String} statement
	 * @return The resulting variable
	 */
	public Variable execute(String o) {

		StringBuilder b = new StringBuilder(o);

		if (o.startsWith("!var ")) {
			b.delete(0, 5);
		}

		String[] s = split(b.toString().trim());

		return subExecute(o, s);

	}

	private Variable subExecute(String o, String[] s) {
		Validate.isTrue(s.length > 0, "Invalid line '" + o + "'");

		if (s[0].startsWith("@")) {

			return staticVars.getVariable(s);

		}

		Variable var = null;

		if (s[0].startsWith("$")) {
			var = getVar(s[0]);
		} else {
			try {
				var = craftVar(generateInternalName(), s[0]);
			} catch (IllegalArgumentException e) {
				if (e.getMessage() == null || e.getMessage().isEmpty()) {
					throw new IllegalArgumentException("'" + s[0] + "' is not a variable", e);
				} else {
					throw e;
				}
			}
		}

		if (s.length == 1) {
			return var;
		} else if (s.length == 2) {
			return var.invoke(s[1], null);
		}

		if (s[1].equals("instanceOf")) {

			if (s.length != 3)
				throw new IllegalArgumentException("The method 'instanceOf' must have as parameter a variable type");

			return new BooleanVar(this, generateInternalName(), var.getType().toString().equals(s[2].toUpperCase()));

		}
		if (!var.getMethods().contains(s[1])) {
			throw new IllegalArgumentException("Variable '" + s[0] + "' (type => "
					+ var.getType().toString().toLowerCase() + ")" + " doesn't have a method called '" + s[1] + "'");
		} else {
			if (s.length > 3) {

				String[] s1 = new String[s.length - 2];
				System.arraycopy(s, 2, s1, 0, s1.length);

				return var.invoke(s[1], subExecute(o, s1));

			} else
				return var.invoke(s[1], craftVar(generateInternalName(), s[2]));
		}
	}

	private String[] split(String s) {

		boolean inString = false;
		boolean next = false;
		boolean inSquare = false;
		boolean inStatic = false;
		boolean addAll = false;

		List<String> split = new LinkedList<>();

		StringBuilder a = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (addAll) {
				a.append(c);
				continue;
			}

			if (inStatic) {
				if (c == ' ') {
					inStatic = false;
					split.add(a.toString());
					a = new StringBuilder();
					continue;
				}
				a.append(c);
				continue;
			}

			if (next) {
				a.append(c);
				next = false;
				continue;
			}

			if (c == '\\') {
				if (inString) {
					next = true;
					continue;
				}
				if (a.length() == 0) {
					addAll = true;
					a.append(c);
					continue;
				}
			}

			if (c == '"') {
				a.append("\"");
				inString = !inString;
				continue;
			}

			if (inString) {
				a.append(c);
				continue;
			}

			if ((c == '(' || c == '/') && a.length() == 0) {
				addAll = true;
				a.append(c);
				continue;
			}

			if (inSquare) {
				if (c == '@') {
					throw new IllegalArgumentException("Cannot instantiate static methods into lists");
				}
				if (c == '[') {
					throw new IllegalArgumentException("Duplicate char '['");
				}
				if (c == ']') {
					a.append(c);
					split.add(a.toString());
					a = new StringBuilder();
					inSquare = false;
					continue;
				}
				a.append(c);
				continue;
			}

			if (c == '@') {
				a.append(c);
				inStatic = true;
				continue;
			}

			if (c == '[') {

				inSquare = true;
				a.append(c);
				continue;
			}
			if (c == ']') {
				throw new IllegalArgumentException("Invalid char ']', there isn't a '['");
			}

			if (c == '{') {
				int opens = 0;

				StringBuilder b = new StringBuilder();

				for (; i < s.length(); i++) {
					char cc = s.charAt(i);
					if (cc == '{') {
						opens++;
					} else if (cc == '}') {
						opens--;
					}
					b.append(cc);
					if (opens == 0)
						break;
				}

				if (opens > 0) {
					throw new IllegalArgumentException("Missing one or more '}'");
				}

				split.add(b.toString());
				continue;
			}

			if (c == ' ') {
				if (a.length() == 0) {
					continue;
				}

				split.add(a.toString());
				a = new StringBuilder();
				continue;

			}

			a.append(c);
		}

		if (inString)
			throw new IllegalArgumentException("Unbalanced \" \"");
		if (inSquare)
			throw new IllegalArgumentException("Unbalanced [ ]");

		if (a.length() > 0)
			split.add(a.toString());

		return split.toArray(new String[split.size()]);

	}

	@SuppressWarnings("unchecked")
	Variable craftVariable(String name, Object value, VarType type) {
		if (value instanceof Variable) {
			value = ((Variable) value).get();
		}
		switch (type.toString()) {
			case "BOOLEAN":
				return new BooleanVar(this, name, (boolean) value);
			case "INT":
				return new IntVar(this, name, (int) value);
			case "LIST":
				return new ListVar(this, name, (LinkedList<Variable>) value);
			case "DOUBLE":
				return new DoubleVar(this, name, (double) value);
			case "STRING":
				return new StringVar(this, name, (String) value);
			case "PLAYER":
				return new PlayerVar(this, name, (Player) value);
			case "STRUCT":
				return new StructVar(this, name, (List<Variable>) value);
			default:
				// TODO add constructors for new variable types
				return new NullVar(this, name);
		}

	}

	public String applyVars(String s) {
		for (Entry<String, Variable> e : vars.entrySet()) {
			s = s.replace(e.getKey(),
					e.getValue().getType() == VarType.STRING ? ((StringVar) e.getValue()).rawToString()
							: e.getValue().toString());
		}
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + internalVars;
		result = prime * result + ((staticVars == null) ? 0 : staticVars.hashCode());
		result = prime * result + ((vars == null) ? 0 : vars.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariableManager))
			return false;
		VariableManager other = (VariableManager) obj;
		if (internalVars != other.internalVars)
			return false;
		if (staticVars == null) {
			if (other.staticVars != null)
				return false;
		} else if (!staticVars.equals(other.staticVars))
			return false;
		if (vars == null) {
			if (other.vars != null)
				return false;
		} else if (!vars.equals(other.vars))
			return false;
		return true;
	}

}
