/**
 * $RCSfile: StringUtils.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2007/03/01 00:17:45 $
 *
 * Copyright (C) 2000 CoolServlets.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        CoolServlets.com (http://www.Yasna.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@Yasna.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of CoolServlets.com. For more information
 * on CoolServlets.com, please see <http://www.Yasna.com>.
 */
package com.mooo.mycoz.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class StringUtils {

	/**
	 * Initialization lock for the whole class. Init's only happen once per
	 * class load so this shouldn't be a bottleneck.
	 */
	private static Object initLock = new Object();

	/**
	 * Replaces all instances of oldString with newString in line.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * Replaces all instances of oldString with newString in line with the added
	 * feature that matches of newString in oldString ignore case.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replaceIgnoreCase(String line, String oldString,
			String newString) {
		if (line == null) {
			return null;
		}
		String lcLine = line.toLowerCase();
		String lcOldString = oldString.toLowerCase();
		int i = 0;
		if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * Replaces all instances of oldString with newString in line. The count
	 * Integer is updated with number of replaces.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString, int[] count) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			int counter = 0;
			counter++;
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				counter++;
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			count[0] = counter;
			return buf.toString();
		}
		return line;
	}

	/**
	 * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
	 * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
	 * their HTML escape sequences.
	 * 
	 * @param input
	 *            the text to be converted.
	 * @return the input string with the characters '&lt;' and '&gt;' replaced
	 *         with their HTML escape sequences.
	 */
	public static final String escapeHTMLTags(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		// Use a StringBuffer in lieu of String concatenation -- it is
		// much more efficient this way.
		StringBuffer buf = new StringBuffer(input.length());
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * Used by the hash method.
	 */
	private static MessageDigest digest = null;

	/**
	 * Hashes a String using the Md5 algorithm and returns the result as a
	 * String of hexadecimal numbers. This method is synchronized to avoid
	 * excessive MessageDigest object creation. If calling this method becomes a
	 * bottleneck in your code, you may wish to maintain a pool of MessageDigest
	 * objects instead of using this method.
	 * <p>
	 * A hash is a one-way function -- that is, given an input, an output is
	 * easily computed. However, given the output, the input is almost
	 * impossible to compute. This is useful for passwords since we can store
	 * the hash and a hacker will then have a very hard time determining the
	 * original password.
	 * <p>
	 * In Jive, every time a user logs in, we simply take their plain text
	 * password, compute the hash, and compare the generated hash to the stored
	 * hash. Since it is almost impossible that two passwords will generate the
	 * same hash, we know if the user gave us the correct password or not. The
	 * only negative to this system is that password recovery is basically
	 * impossible. Therefore, a reset password method is used instead.
	 * 
	 * @param data
	 *            the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public synchronized static final String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. "
						+ "Jive will be unable to function normally.");
				nsae.printStackTrace();
			}
		}
		// Now, compute hash.
		digest.update(data.getBytes());
		return toHex(digest.digest());
	}

	/**
	 * Turns an array of bytes into a String representing each byte as an
	 * unsigned hex number.
	 * <p>
	 * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
	 * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
	 * Distributed under LGPL.
	 * 
	 * @param hash
	 *            an rray of bytes to convert to a hex-string
	 * @return generated hex string
	 */
	public static final String toHex(byte hash[]) {
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}
		return buf.toString();
	}
	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte toByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] toBytes(String hex) {
		if (hex == null || hex.equals("")) {
			return null;
		}
		hex = hex.toUpperCase();
		int length = hex.length() / 2;
		char[] hexChars = hex.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
		}
		return d;
	}
	/**
	 * swap by bytes
	 * 
	 * @param bytes
	 *
	 * @return bytes
	 */
	public static byte[] swapBytes(byte[] bytes){
		if(bytes==null || bytes.length <2)
			return null;
		
		byte[] nbytes = new byte[bytes.length];
		
		for(int i=0;i<bytes.length;i++){
			nbytes[i]=bytes[bytes.length-1-i];
		}
		
		return nbytes;
	}
	/**
	 * Convert byte[4] to short
	 * 
	 * @param bytes
	 *        byte[4]
	 * @return long
	 */
	public static long toInt(byte[] bytes) {
		int mask = 0xff;
		int temp = 0;
		long n = 0;
		for (int i = 0; i < 4 ; i++) {
			n <<= 8;
			temp = bytes[i] & mask;
			n |= temp;
		}
		return n;
	}
	/**
	 * Convert byte[2] to short
	 * 
	 * @param bytes
	 *        byte[2]
	 * @return short
	 */
	public static short toShort(byte[] bytes) {
		int mask = 0xff;
		int temp = 0;
		short n = 0;
		for (int i = 0; i < 2 ; i++) {
			n <<= 4;
			temp = bytes[i] & mask;
			n |= temp;
		}
		return n;
	}
	
	public static byte[] subBytes(byte[] src,int begin,int offset) {
		byte[] subBytes = new byte[offset];
		for(int i=0;i<offset;i++){
			subBytes[i] = src[begin+i];
		}
		return subBytes;
	}
	
	public static boolean rightLRC(byte[] bytes){
		byte clc = 0x00;
		if(bytes == null || bytes.length==1){
			return false;
		}
		
		int length = bytes.length;
		
		for(int i=0;i<length-1;i++){
			clc = (byte) (clc ^ bytes[i]);
		}
		
		if(clc == bytes[length-1]){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Converts a line of text into an array of lower case words. Words are
	 * delimited by the following characters: , .\r\n:/\+
	 * <p>
	 * In the future, this method should be changed to use a
	 * BreakIterator.wordInstance(). That class offers much more fexibility.
	 * 
	 * @param text
	 *            a String of text to convert into an array of words
	 * @return text broken up into an array of words.
	 */
	public static final String[] toLowerCaseWordArray(String text) {
		if (text == null || text.length() == 0) {
			return new String[0];
		}
		StringTokenizer tokens = new StringTokenizer(text, " ,\r\n.:/\\+");
		String[] words = new String[tokens.countTokens()];
		for (int i = 0; i < words.length; i++) {
			words[i] = tokens.nextToken().toLowerCase();
		}
		return words;
	}

	/**
	 * A list of some of the most common words. For searching and indexing, we
	 * often want to filter out these words since they just confuse searches.
	 * The list was not created scientifically so may be incomplete :)
	 */
	private static final String[] commonWords = new String[] { "a", "and",
			"as", "at", "be", "do", "i", "if", "in", "is", "it", "so", "the",
			"to" };
	private static Map<String, String> commonWordsMap = null;

	/**
	 * Returns a new String array with some of the most common English words
	 * removed. The specific words removed are: a, and, as, at, be, do, i, if,
	 * in, is, it, so, the, to
	 */
	public static final String[] removeCommonWords(String[] words) {
		// See if common words map has been initialized. We don't statically
		// initialize it to save some memory. Even though this a small savings,
		// it adds up with hundreds of classes being loaded.
		if (commonWordsMap == null) {
			synchronized (initLock) {
				if (commonWordsMap == null) {
					commonWordsMap = new HashMap<String, String>();
					for (int i = 0; i < commonWords.length; i++) {
						commonWordsMap.put(commonWords[i], commonWords[i]);
					}
				}
			}
		}
		// Now, add all words that aren't in the common map to results
		ArrayList<String> results = new ArrayList<String>(words.length);
		for (int i = 0; i < words.length; i++) {
			if (!commonWordsMap.containsKey(words[i])) {
				results.add(words[i]);
			}
		}
		return (String[]) results.toArray(new String[results.size()]);
	}

	/**
	 * Pseudo-random number generator object for use with randomString(). The
	 * Random class is not considered to be cryptographically secure, so only
	 * use these random Strings for low to medium security applications.
	 */
	private static Random randGen = null;

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = null;

	/**
	 * Returns a random String of numbers and letters of the specified length.
	 * The method uses the Random class that is built-in to Java which is
	 * suitable for low to medium grade security uses. This means that the
	 * output is only pseudo random, i.e., each number is mathematically
	 * generated so is not truly random.
	 * <p>
	 * 
	 * For every character in the returned String, there is an equal chance that
	 * it will be a letter or number. If a letter, there is an equal chance that
	 * it will be lower or upper case.
	 * <p>
	 * 
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * 
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Init of pseudo random number generator.
		if (randGen == null) {
			synchronized (initLock) {
				if (randGen == null) {
					randGen = new Random();
					// Also initialize the numbersAndLetters array
					numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
							+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
							.toCharArray();
				}
			}
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * Intelligently chops a String at a word boundary (whitespace) that occurs
	 * at the specified index in the argument or before. However, if there is a
	 * newline character before <code>length</code>, the String will be chopped
	 * there. If no newline or whitespace is found in <code>string</code> up to
	 * the index <code>length</code>, the String will chopped at
	 * <code>length</code>.
	 * <p>
	 * For example, chopAtWord("This is a nice String", 10) will return "This is
	 * a" which is the first word boundary less than or equal to 10 characters
	 * into the original String.
	 * 
	 * @param string
	 *            the String to chop.
	 * @param length
	 *            the index in <code>string</code> to start looking for a
	 *            whitespace boundary at.
	 * @return a substring of <code>string</code> whose length is less than or
	 *         equal to <code>length</code>, and that is chopped at whitespace.
	 */
	public static final String chopAtWord(String string, int length) {
		if (string == null) {
			return string;
		}

		char[] charArray = string.toCharArray();
		int sLength = string.length();
		if (length < sLength) {
			sLength = length;
		}

		// First check if there is a newline character before length; if so,
		// chop word there.
		for (int i = 0; i < sLength - 1; i++) {
			// Windows
			if (charArray[i] == '\r' && charArray[i + 1] == '\n') {
				return string.substring(0, i);
			}
			// Unix
			else if (charArray[i] == '\n') {
				return string.substring(0, i);
			}
		}
		// Also check boundary case of Unix newline
		if (charArray[sLength - 1] == '\n') {
			return string.substring(0, sLength - 1);
		}

		// Done checking for newline, now see if the total string is less than
		// the specified chop point.
		if (string.length() < length) {
			return string;
		}

		// No newline, so chop at the first whitespace.
		for (int i = length - 1; i > 0; i--) {
			if (charArray[i] == ' ') {
				return string.substring(0, i).trim();
			}
		}

		// Did not find word boundary so return original String chopped at
		// specified length.
		return string.substring(0, length);
	}

	/**
	 * Highlights words in a string. Words matching ignores case. The actual
	 * higlighting method is specified with the start and end higlight tags.
	 * Those might be beginning and ending HTML bold tags, or anything else.
	 * 
	 * @param string
	 *            the String to highlight words in.
	 * @param words
	 *            an array of words that should be highlighted in the string.
	 * @param startHighlight
	 *            the tag that should be inserted to start highlighting.
	 * @param endHighlight
	 *            the tag that should be inserted to end highlighting.
	 * @return a new String with the specified words highlighted.
	 */
	public static final String highlightWords(String string, String[] words,
			String startHighlight, String endHighlight) {
		if (string == null || words == null || startHighlight == null
				|| endHighlight == null) {
			return null;
		}

		// Iterate through each word.
		for (int x = 0; x < words.length; x++) {
			// we want to ignore case.
			String lcString = string.toLowerCase();
			// using a char [] is more efficient
			char[] string2 = string.toCharArray();
			String word = words[x].toLowerCase();

			// perform specialized replace logic
			int i = 0;
			if ((i = lcString.indexOf(word, i)) >= 0) {
				int oLength = word.length();
				StringBuffer buf = new StringBuffer(string2.length);

				// we only want to highlight distinct words and not parts of
				// larger words. The method used below mostly solves this. There
				// are a few cases where it doesn't, but it's close enough.
				boolean startSpace = false;
				char startChar = ' ';
				if (i - 1 > 0) {
					startChar = string2[i - 1];
					if (!Character.isLetter(startChar)) {
						startSpace = true;
					}
				}
				boolean endSpace = false;
				char endChar = ' ';
				if (i + oLength < string2.length) {
					endChar = string2[i + oLength];
					if (!Character.isLetter(endChar)) {
						endSpace = true;
					}
				}
				if ((startSpace && endSpace) || (i == 0 && endSpace)) {
					buf.append(string2, 0, i);
					if (startSpace && startChar == ' ') {
						buf.append(startChar);
					}
					buf.append(startHighlight);
					buf.append(string2, i, oLength).append(endHighlight);
					if (endSpace && endChar == ' ') {
						buf.append(endChar);
					}
				} else {
					buf.append(string2, 0, i);
					buf.append(string2, i, oLength);
				}

				i += oLength;
				int j = i;
				while ((i = lcString.indexOf(word, i)) > 0) {
					startSpace = false;
					startChar = string2[i - 1];
					if (!Character.isLetter(startChar)) {
						startSpace = true;
					}

					endSpace = false;
					if (i + oLength < string2.length) {
						endChar = string2[i + oLength];
						if (!Character.isLetter(endChar)) {
							endSpace = true;
						}
					}
					if ((startSpace && endSpace)
							|| i + oLength == string2.length) {
						buf.append(string2, j, i - j);
						if (startSpace && startChar == ' ') {
							buf.append(startChar);
						}
						buf.append(startHighlight);
						buf.append(string2, i, oLength).append(endHighlight);
						if (endSpace && endChar == ' ') {
							buf.append(endChar);
						}
					} else {
						buf.append(string2, j, i - j);
						buf.append(string2, i, oLength);
					}
					i += oLength;
					j = i;
				}
				buf.append(string2, j, string2.length - j);
				string = buf.toString();
			}
		}
		return string;
	}

	/**
	 * Escapes all necessary characters in the String so that it can be used in
	 * an XML doc.
	 * 
	 * @param string
	 *            the string to escape.
	 * @return the string with appropriate characters escaped.
	 */
	public static final String escapeForXML(String string) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (string == null || string.length() == 0) {
			return string;
		}
		char[] sArray = string.toCharArray();
		StringBuffer buf = new StringBuffer(sArray.length);
		char ch;
		for (int i = 0; i < sArray.length; i++) {
			ch = sArray[i];
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else if (ch == '"') {
				buf.append("&quot;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}
	
	/**
	 * checkString 
	 * 
	 * @param string
	 *            the string to check.
	 * @param string
	 *            the string to define. ("\\.do")
	 * @return the boolean with check result.
	 */
	public static final boolean checkString(String string,String pattern) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (string == null || string.length() == 0 || pattern == null || pattern.length() == 0) {
			return false;
		}
		
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(string);
		
		return m.find();
	}
	
	public synchronized static final String toUppeFirst(String str){
		return str.substring(0, 1).toUpperCase()+str.substring(1);
	}

	public synchronized static final String toLowerFirst(String str){
		return str.substring(0, 1).toLowerCase()+str.substring(1);
	}
	
	public synchronized static final String prefixToUpper(String str){
		return prefixToUpper(str,"_",false);
	}

	public synchronized static final String prefixToUpperNot(String str){
		return prefixToUpperNot(str,"_");
	}

	public synchronized static final String prefixToUpperNot(String str,String prefix){
		String result = null;
		
		if (str != null && str.length() > 1 && prefix!=null && !prefix.equals("")) {
			result = prefixToUpper(str, prefix,false);
			result = result.substring(0, 1).toLowerCase() + result.substring(1);
		} else{
			result=str;
		}
		
		return result;
	}
	
	//database field change bean field
	public synchronized static final String prefixToUpper(String str,String prefix,boolean enableCase){
		
		if(prefix == null)
			if(enableCase)
				return str;
			else
				return str.toLowerCase();

		if (str != null && str.indexOf(prefix) < 0){
			return str.toLowerCase();
		}
		
		//spit prefix
		String result="";
		String tmp="";
		StringTokenizer tokens = new StringTokenizer(str, prefix);
		while (tokens.hasMoreTokens()) {
			tmp = tokens.nextToken();
			tmp = tmp.toLowerCase();
			result += tmp.substring(0, 1).toUpperCase()+tmp.substring(1);
		}

		return result;
	}
	
	//split for prefix
	public synchronized static final String upperToPrefix(String str,String prefix,boolean beginEnable){

		if(prefix==null || prefix.equals("")){
				if(beginEnable)
					return str;
				else
					return str.substring(0, 1).toLowerCase()+str.substring(1);
		}
		
		String result="";

		Pattern p = Pattern.compile("[A-Z]+[a-z]*");
		Matcher m = p.matcher(str);
		
		while(m.find()){
			result += prefix+m.group().toLowerCase();
		}
		
		if(result != null && result.length() > 2) {
			result = result.substring(1);
		}
		
		return result;
	}
	//split for prefix,enable begin
	public synchronized static final String upperToPrefix(String str,String prefix){
		return upperToPrefix(str,prefix,true);
	}
	//split for prefix,not enable begin
	public synchronized static final String upperToPrefixNot(String str,String prefix){
		return upperToPrefix(str,prefix,false);
	}
	
	//default not split,enable begin
	public synchronized static final String upperToPrefix(String str){
		return upperToPrefix(str,null);
	}
	//default not split,not enable begin
	public synchronized static final String upperToPrefixNot(String str){
		return upperToPrefixNot(str,null);
	}
	
	//default split case , specially prefix
	public synchronized static final String formatHump(String str,String prefix){
		
		if(prefix==null || prefix.equals(""))
			return str;
		
		String result="";

		Pattern p = Pattern.compile("[A-Z]*");
		Matcher m = p.matcher(str);
		
		while(m.find()){
			result += prefix+m.group().toLowerCase();
		}
		
		if(result != null && result.length() > 2) {
			result = result.substring(1);
		}
		
		return result;
	}
	
	public static final String getMethod(String columnName,String returnType){
		
		StringBuilder createBuf = new StringBuilder();
		createBuf.append("\tpublic "+returnType+" get"+toUppeFirst(columnName)+"() {\n");
		createBuf.append("\treturn "+columnName+";\n");
		createBuf.append("\t}\n");

		return createBuf.toString();
	}
	
	public static final String setMethod(String columnName,String returnType){
		
		StringBuilder createBuf = new StringBuilder();
		createBuf.append("\tpublic void set"+toUppeFirst(columnName)+"("+returnType+" "+columnName+") {\n");
		createBuf.append("\t this."+columnName+" = "+columnName+";\n");
		createBuf.append("\t}\n");

		return createBuf.toString();
	}
	
	public static final String createMethod(String columnName,String returnType){
		return getMethod(columnName,returnType)+setMethod(columnName,returnType);
	}
	
	public static String getFunName(String objName) {
		String funName = objName.substring(0, 1).toUpperCase();

		if (objName.length() > 1) {
			funName += objName.substring(1);
		}
		return funName;
	}
	
	public static String getCatalog(Class<?> cls,int begin) {

		String value=null;
		LinkedList<String> stack = new LinkedList<String>();

		String clsName = cls.getName();
		
		StringTokenizer st = new StringTokenizer(clsName,".");
		while(st.hasMoreTokens()){
			value = st.nextToken();
			stack.push(value);
		}
		
		int i=0;
		while(stack.size() > 0){
			value = (String) stack.pop();
			
			if (i==begin){
				return value;
			} else{
				i++;
			}
		}
		
		return null;
	}
	
	public static void noNull(String str) throws NullPointerException{
		if(str == null || str.equals("")){
			throw new NullPointerException("cant no null");
		}
	}
	public static boolean isNull(String str) {
		if(str == null || str.equals("")){
			return true;
		}
		return false;
	}

	public static String notEmpty(String str) throws NullPointerException{
		if(str == null){
			return "";
		}else {
			return str;
		}
	}
	
	public static String fieldValue(Object entity) throws NullPointerException{
		String fieldValue=null;
		
		if(entity.getClass().isAssignableFrom(Integer.class)
				||entity.getClass().isAssignableFrom(Long.class)
				||entity.getClass().isAssignableFrom(Float.class)
				||entity.getClass().isAssignableFrom(Double.class)){
			fieldValue = entity.toString();
		//}else if(entity.getClass().isAssignableFrom(String.class)){
		}else {
			fieldValue = "'"+entity.toString()+"'";
		}
		
		return fieldValue;
	}
}