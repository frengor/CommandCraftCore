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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import com.fren_gor.commandCraftCore.Reader;

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
	public final StaticMethods staticVars;

	public String generateInternalName() {
		return "$internal_" + internalVars++;
	}

	private static String allowedChars = "abcdefghijklmnopqrstuvwxyzèòàùéçì";

	static {
		allowedChars = allowedChars + allowedChars.toUpperCase() + "0123456789_";
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
		if (name.startsWith("internal_"))
			throw new IllegalArgumentException("Variables name cannot start with 'internal_'");
		if (!name.startsWith("$")) {
			throw new IllegalArgumentException("Variables must start with '$'");
		}
		if (name.substring(1).contains("$")) {
			throw new IllegalArgumentException("Variables must have only one '$' in front of the name");
		}
		for (char c : name.substring(1).toCharArray()) {
			if (!allowedChars.contains(String.valueOf(c))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Just the constructor
	 */
	public VariableManager() {
		staticVars = new StaticMethods(this);
	}

	@Getter
	Map<String, Variable> vars = new LinkedHashMap<>();

	public void sort() {

		List<Entry<String, Variable>> entries = new ArrayList<>(vars.entrySet());
		Collections.sort(entries, (s1, s2) -> {
			if (s1.getKey().length() == s2.getKey().length())
				return 0;
			if (s1.getKey().length() > s2.getKey().length())
				return -1;
			return 1;
		});

		vars = new LinkedHashMap<>();
		for (Map.Entry<String, Variable> entry : entries) {
			vars.put(entry.getKey(), entry.getValue());
		}

	}

	public void print() {

		for (Entry<String, Variable> s : vars.entrySet()) {

			System.out.println(s.getKey() + " -> " + s.getValue().toString());
		}

	}

	public void clear() {
		Iterator<Entry<String, Variable>> it = vars.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Variable> e = it.next();

			if (!e.getValue().isFinal()) {
				it.remove();
			}

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

		name = name.startsWith("$") ? Reader.trim(name) : "$" + Reader.trim(name);

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

		name = name.startsWith("$") ? Reader.trim(name) : "$" + Reader.trim(name);

		// Validate.isTrue(verifyName(name), "Illegal Name " + name);

		if (vars.containsKey(name))
			return vars.get(name);

		value = Reader.trim(value);

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

		if (value.isEmpty()) {
			return new NullVar(this, name);
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
		if (value.startsWith("[") && value.endsWith("]")) {
			/*
			 * if (containsOnly(value.substring(1, value.length() - 1), ' ')) {
			 * return new ListVar(this, name, new LinkedList<>()); }
			 */
			return new ListVar(this, name, splitList(value.substring(1, value.length() - 1)));
		}

		Type t = Type.INT;

		boolean min = false;
		if (value.startsWith("-")) {
			min = true;
			value = value.substring(1);
		}

		for (char c : value.toCharArray()) {

			if (c == '.') {
				t = Type.DOUBLE;
				continue;
			}

			if (!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7'
					|| c == '8' || c == '9')) {

				throw new IllegalArgumentException("Illegal var value '" + value + "'");

			}

		}

		if (t == Type.INT) {
			return new IntVar(this, name, (min ? -1 : 1) * Integer.parseInt(value));
		}

		if (t == Type.DOUBLE) {
			return new DoubleVar(this, name, (min ? -1 : 1) * Double.parseDouble(value));
		}
		return new NullVar(this, name);
	}

	private List<Variable> splitList(String o) {

		if (o.trim().isEmpty()) {
			return new LinkedList<>();
		}

		boolean inString = false;
		boolean next = false;

		List<String> split = new LinkedList<>();

		StringBuilder a = new StringBuilder();

		for (char c : o.toCharArray()) {

			if (next) {
				a.append(c);
				next = false;
				continue;
			}
			if (c == '\\') {
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

			if (c == ',') {

				split.add(a.toString());
				a = new StringBuilder();
				continue;

			}

			a.append(c);

		}

		split.add(a.toString());

		if (inString)
			throw new IllegalArgumentException(
					"Illegal var value '" + split.get(split.size() - 1) + "': unbalanced \" \"");

		Type t = null;

		List<Variable> l = new LinkedList<>();

		for (String s : split) {
			Variable v = craftVar(generateInternalName(), s);
			l.add(v);

			if (t == null) {

				t = v.getType();

			} else if (v.getType() != t) {
				throw new IllegalArgumentException("Illegal list type '" + v.getType() + "'! It should be '" + t + "'");
			}

		}

		return l;

	}

	/**
	 * Replace the variables in {@link String} s with the variables values
	 * 
	 * @param s
	 *            The {@link String} to translate
	 * @return The {@link String} with the variables values
	 */
	// TODO to check
	public String applyVars1(String s) {
		for (Entry<String, Variable> e : vars.entrySet()) {
			s = s.replace(e.getKey(),
					/*
					 * e.getValue().getType() == Type.STRING ? ((StringVar)
					 * e.getValue()).toString1() :
					 */ e.getValue().toString());
		}
		return s;
	}

	/**
	 * Execute a statement
	 * 
	 * @param o
	 *            The statement's {@link String}
	 * @return The resulting variable
	 */
	public Variable execute(String o) {

		StringBuilder b = new StringBuilder(o);

		if (o.startsWith("!var ")) {
			b.delete(0, 5);
		}

		String[] s = split(Reader.trim(b));

		return subExecute(o, s);

	}

	/*
	 * private static String join(String[] s) { StringJoiner j = new
	 * StringJoiner(" "); for (String ss : s) j.add(ss); return j.toString(); }
	 */

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
			// try {
			return var.invoke(s[1], null);
			// } catch (java.lang.NullPointerException e) {
			// throw new NullPointerException("Method ' " + s[1] + " ' require
			// an argument", e);
			// }
		}

		// Validate.isTrue((s.length - 1) % 2 == 0, "Invalid line '" + o + "'");

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
				return var.invoke(s[1], subExecute(o,
						/* b.delete(0, s[0].length() + s[1].length() + 2), */ s1));
			} else
				// try {
				return var.invoke(s[1], craftVar(generateInternalName(), s[2]));
			// } catch (java.lang.NullPointerException e) {
			// throw new NullPointerException("Method ' " + s[1] + " ' require
			// an argument", e);
			// }
		}
	}

	/*
	 * private Variable getVar(char[] a, int i, boolean allowMethods) {
	 * 
	 * StringBuilder b = new StringBuilder();
	 * 
	 * do { i++; if (i == a.length) return Variable.getVar(b.toString());
	 * b.append(a[i]); } while (a[i] != ' ' && a[i] != '#');
	 * 
	 * Variable v = Variable.getVar(b.toString());
	 * 
	 * if (a[i + 1] != '#') { return v; }
	 * 
	 * if (!allowMethods) throw new
	 * IllegalArgumentException("Could not invoke methods on variable " +
	 * b.toString());
	 * 
	 * StringBuilder method = new StringBuilder(); StringBuilder par = new
	 * StringBuilder(); List<String> pars = new LinkedList<>();
	 * 
	 * do { i++; if (i == a.length) throw new
	 * IllegalArgumentException("Unbalanced round brackets");
	 * 
	 * method.append(a[i]);
	 * 
	 * } while (a[i] != '(');
	 * 
	 * do {
	 * 
	 * i++; if (a[i] == ' ') continue; if (i == a.length) throw new
	 * IllegalArgumentException("Unbalanced round brackets"); if (a[i] == ',') {
	 * pars.add(par.toString()); par = new StringBuilder(); } else
	 * par.append(a[i]);
	 * 
	 * } while (a[i] != ')');
	 * 
	 * pars.add(par.toString());
	 * 
	 * Variable[] vars = new Variable[pars.size()];
	 * 
	 * for (int ii = 0; ii < pars.size(); ii++) { vars[ii] =
	 * Variable.getVar(pars.get(ii)); if (vars[ii].getType() == Type.NULL) {
	 * vars[ii] = Variable.deserializeVar(Variable.generateInternalName(),
	 * pars.get(ii)); } }
	 * 
	 * v.invoke(method.toString(), vars);
	 * 
	 * return v;
	 * 
	 * }
	 */

	private String[] split(String s) {

		boolean inString = false;
		boolean next = false;
		boolean inSquare = false;
		boolean inStatic = false;

		List<String> split = new LinkedList<>();

		StringBuilder a = new StringBuilder();

		for (char c : s.toCharArray()) {

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
	Variable craftVariable(String name, Object value, Type type) {
		switch (type) {
			case BOOLEAN:
				return new BooleanVar(this, name, (boolean) value);
			case INT:
				return new IntVar(this, name, (int) value);
			case LIST:
				return new ListVar(this, name, (LinkedList<Variable>) value);
			case DOUBLE:
				return new DoubleVar(this, name, (double) value);
			case STRING:
				return new StringVar(this, name, (String) value);
			case PLAYER:
				return new PlayerVar(this, name, (Player) value);
			default:
				return new NullVar(this, name);
		}

	}

	public String applyVars(String s) {
		for (Entry<String, Variable> e : vars.entrySet()) {
			s = s.replace(e.getKey(), e.getValue().getType() == Type.STRING ? ((StringVar) e.getValue()).toString1()
					: e.getValue().toString());
		}
		return s;
	}

}
