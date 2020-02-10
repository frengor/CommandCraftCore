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
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;

import com.fren_gor.commandCraftCore.Reader;
import com.fren_gor.commandCraftCore.exceptions.ReaderException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

	private static Random rand = new Random();

	public static List<String> filterTabCompleteOptions(Collection<String> options, String... args) {
		String lastArg = "";
		if (args.length > 0) {
			lastArg = args[(args.length - 1)].toLowerCase();
		}
		List<String> Options = new ArrayList<>(options);
		for (int i = 0; i < Options.size(); i++) {
			if (!Options.get(i).toLowerCase().startsWith(lastArg)) {
				Options.remove(i--);
			}
		}
		return Options;
	}

	/**
	 * Check if a line starts with a {@link String} and if it is followed by any
	 * character
	 * 
	 * @param line
	 *            Complete line
	 * @param starting
	 *            Stirng to compare
	 * @return If the string matching was successful
	 */
	public static boolean check(String line, String starting) {

		if (line.length() <= starting.length())
			return false;

		char[] sc = starting.toCharArray();
		char[] oc = line.toCharArray();

		int l = starting.length();

		for (int i = 0; i < line.length(); i++) {
			if (i < l) {
				if (sc[i] != oc[i]) {
					return false;
				}
			} else {
				if (oc[i] != ' ' && oc[i] != '\t') {
					return true;
				}
			}
		}

		return false;

	}

	public static void printRidingError(String report, String path, int line) {

		Bukkit.getConsoleSender()
				.sendMessage("[CommandCraftCore] §cError: " + report + "§c! §7File: " + path + " §6Line: §e" + line);
		throw new ReaderException(report);

	}

	public static void printReadingError(String report, Reader reader, int line) {

		Bukkit.getConsoleSender().sendMessage("[CommandCraftCore] §cError: " + report + "§c! §7File: "
				+ reader.getFile().getPath() + " §6Line: §e" + line);
		throw new ReaderException(report);

	}

	/**
	 * By Jonas Klemming https://stackoverflow.com/a/237204
	 */
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return a random integer in a certain range
	 * 
	 * @param Min
	 *            The min number of the range
	 * @param Max
	 *            The max number of the range
	 * @return A number between min and max (included)
	 */
	public static int nextInt(int min, int max) {
		if (min == max) {
			return max;
		}

		return rand.nextInt(max - min + 1) + min;
	}

}
