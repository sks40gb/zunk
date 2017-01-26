/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bala
 */

/**
 * A rudimentary command line parser.
 * <p>
 * The command arguments are specified by a pattern consisting of options and positionals
 * <table border="1">
 * <th>    item       <th>optional <th>short name<th>has value<th>syntax                 <tb>
 * <tr><td>option     <td>no       <td>no        <td>no       <td> --xyz                 </tr>
 * <tr><td>option     <td>no       <td>no        <td>yes      <td> --xyz[=] value        </tr>
 * <tr><td>option     <td>no       <td>yes       <td>no       <td> {-x|--xyz}            </tr>
 * <tr><td>option     <td>no       <td>yes       <td>yes      <td> {-x|--xyz[=]} value   </tr>
 * <tr><td>option     <td>yes      <td>no        <td>no       <td> --xyz                 </tr>
 * <tr><td>option     <td>yes      <td>no        <td>yes      <td> --xyz[=] value        </tr>
 * <tr><td>option     <td>yes      <td>yes       <td>no       <td> [{-x|--xyz}]          </tr>
 * <tr><td>option     <td>yes      <td>yes       <td>yes      <td> [{-x|--xyz[=]} value] </tr>
 * <tr><td>positional <td>no       <td>          <td>         <td> aaa                   </tr>
 * <tr><td>positional <td>yes      <td>          <td>         <td> [aaa]                 </tr>
 * <tr><td>positional <td>1 or more<td>          <td>         <td> aaa ...               </tr>
 * <tr><td>positional <td>0 or more<td>          <td>         <td> [aaa] ...             </tr>
 * </table>
 */

public class CommandLine {

    public CommandLine (String spec) {
        parseSpec(spec);
    }
    
    // Storage for the parsed command
    
    private Map options = new HashMap();
    private ArrayList positionals = new ArrayList();
    
    /**
     * Parse a command line.
     * Parses and stores options and positionals for later retrieval.
     * Checks for required/allowed options and positionals.
     * @param The command line arguments to be parsed, as an array of String's.
     * @return true if the command line is valid, else false.
     */
    public boolean parse(String[] chunks) {

        // Clear storage for results
        options.clear();
        positionals.clear();

        int pos = 0;
        while (pos < chunks.length) {
            if (chunks[pos].startsWith("--")) {
                String optionName = chunks[pos].substring(2);
                String optionValue = "";
                OptionSpec spec = (OptionSpec) optionSpecs.get(optionName);
                if (spec == null) {
                    Log.print("CommandLine: "+optionName+"not an option after --");
                    return false;
                }
                if (spec.hasValue) {
                    pos++;
                    if (pos < chunks.length && chunks[pos].equals("=")) {
                        pos++;
                    }
                    if (pos < chunks.length) {
                        optionValue = chunks[pos];
                    } else {
                        Log.print("CommandLine: No value for option: "+optionName);
                        return false;
                    }
                }
                options.put(spec.longName, optionValue);
            } else if (chunks[pos].startsWith("-")) { 
                if (chunks[pos].length() == 1) {
                    Log.print("CommandLine: no option after -");
                    return false;
                }
                for (int i = 1; i < chunks[pos].length(); i++) {
                    String optionName = Character.toString(chunks[pos].charAt(i));
                    OptionSpec spec = (OptionSpec) optionSpecs.get(optionName);
                    if (spec == null) {
                        Log.print("CommandLine: '"+optionName+"'not an option after -");
                        return false;
                    }
                    if (spec.hasValue) {
                        if (i + 1< (chunks[pos].length())) {
                            options.put(spec.longName, chunks[pos].substring(i + 1));
                        } else {
                            pos++;
                            if (pos < chunks.length && chunks[pos].equals("=")) {
                                pos++;
                            }
                            if (pos < chunks.length) {
                                options.put(spec.longName, chunks[pos]);
                            } else {
                                Log.print("CommandLine: No value for option: "+optionName);
                                return false;
                            }
                        }
                        break;
                    } else {
                        options.put(spec.longName, "");
                    }
                }
            } else {
                positionals.add(chunks[pos]);
            }
            pos++;
        }
                                                                                                    
        // Check for number of positional parameters
        boolean result = true;
        if (positionals.size() < minPositionalCount || positionals.size() > maxPositionalCount) {
            Log.print("CommandLine: Wrong number of positionals: "+positionals.size()
                      +" not in "+minPositionalCount+" .. "+maxPositionalCount);
            result = false;
        }

        Iterator itx = options.entrySet().iterator();
        while (itx.hasNext()) {
            Map.Entry entry = (Map.Entry) itx.next(); 
        }


        // Check for required parameters
        Iterator it = optionSpecs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next(); 
            OptionSpec spec = (OptionSpec) entry.getValue();
            if (spec.required) {
                Object key = entry.getKey();
                if (key.equals(spec.longName) && options.get(key) == null ) {
                    Log.print("CommandLine: Required parameter missing: "+key);
                    result = false;
                }
            }
        }

        return result;
    }

    // Methods to return information collected from command line

    /**
     * Get number of positionals seen on the command line.
     */
    public int getPositionalCount() {
        return positionals.size();
    }
   
    /**
     * Get the value of the i'th positional on the command line.
     */
    public String getPositional(int i) {
        return (String) positionals.get(i);
    }
   
    /**
     * Get the value of the named option.  Returns null if none given.
     */
    public String getOption(String name) {
        OptionSpec spec = (OptionSpec) optionSpecs.get(name);
        if (spec == null) {
            Log.quit("CommandLine: Undefined option: "+name);
        }
        return ((String) options.get(spec.longName));
    }
   
    // Storage for command specifications
    // These are public only to allow unit tests.  They should not be used
    // for other purposes from outside this class.
    
    /** Public for unit testing only.  Do not use outside of the CommandLine class. */
    public Map optionSpecs = new HashMap();
    /** Public for unit testing only.  Do not use outside of the CommandLine class. */
    public int minPositionalCount = 0;
    /** Public for unit testing only.  Do not use outside of the CommandLine class. */
    public int maxPositionalCount = 0;
    /** Public for unit testing only.  Do not use outside of the CommandLine class. */
    
    // A class for option specification
    /** Public for unit testing only.  Do not use outside of the CommandLine class. */
    public class OptionSpec {
        /** Public for unit testing only.  Do not use outside of the CommandLine class. */
        public String longName;
        /** Public for unit testing only.  Do not use outside of the CommandLine class. */
        public boolean required = false;
        /** Public for unit testing only.  Do not use outside of the CommandLine class. */
        public boolean hasValue = false;

        OptionSpec(String longName, boolean required, boolean hasValue) {
            this.longName  = longName; 
            this.required  = required; 
            this.hasValue  = hasValue; 
        }

        /** Public for unit testing only.  Do not use outside of the CommandLine class. */
        public String toString() {
            return "OptionSpec["+longName
                +(required ? ",required" : "")
                +(hasValue ? ",hasValue" : "")
                +"]";
        }
    }

    // The following constitute a simple recursive descent parser for command specifications

    final private static char END_MARKER = '$';
    Tokenizer tz;
    String token = "";

    private void parseSpec(String spec) {
        tz = new Tokenizer(spec);
        tz.next();
        while (token.charAt(0) != END_MARKER) {
            boolean required = true;
            if (token.length() == 1 && maybe('[')) {
                required = false;
            }
            if (token.charAt(0) == '-' || token.charAt(0) == '{') {
                if (maxPositionalCount > 0) {
                    patternError();
                }
                parseOptionSpec(required);
            } else {
                parsePositionalSpec(required);
            }
            if (! required) {
                accept(']');
            }
            if (token.charAt(0) == '.') {
                if (maxPositionalCount == 0) {
                    patternError();
                }
                maxPositionalCount = Integer.MAX_VALUE;
                tz.next();
                if (token.charAt(0) != END_MARKER) {
                    patternError();
                }
            }
        }
    }

    private void parseOptionSpec(boolean required) {
        String shortName = null;
        String longName;
        boolean brace = false;
        boolean hasValue = false;

        if (maybe('{')) {
            brace = true;
            if (token.length() == 1 && maybe('-')) {
                if (! isWord(token) || token.length() != 1) {
                    patternError();
                }
                shortName = token;
                tz.next();
                accept('|');
            }
        }

        if (token.length() != 2|| ! maybe('-')) {
            patternError();
        }
        if (! isWord(token)) {
            patternError();
        }
        longName = token;
        tz.next();
        if (token.length() == 3 && maybe('[')) {
            hasValue = true;
        }
        if (brace) {
            accept('}');
        }
        if (hasValue) {
            if (! isWord(token)) {
                patternError();
            }
            tz.next();
        }

        OptionSpec spec = new OptionSpec(longName,required,hasValue); 
        if (shortName != null) {
            optionSpecs.put(shortName,spec);
        }
        optionSpecs.put(longName,spec);
    }

    private void parsePositionalSpec(boolean required) {
        if (! isWord(token)) {
            patternError();
        }
        maxPositionalCount ++;
        if (required) {
            minPositionalCount ++;
        }
        tz.next();
    }

    // A class for tokenizing the command-line spec
    // Tokenizer.next() gives the next token or null
    private class Tokenizer {

        Matcher mat;
        String  tail;

        // create a Tokenizer
        Tokenizer (String specs) {
            tail = specs;
            // Pattern for tokens in a command-line spec
            Pattern pat = Pattern.compile("\\s+"          // whitespace (starts with <= ' ')                                          +"|\\{|\\}"     // left, right brace
                                          +"|\\|"         // vertical bar
                                          +"|\\[\\=\\]"   // [=]
                                          +"|\\[|\\]"     // left, right bracket
                                          +"|\\{|\\}"     // left, right brace
                                          +"|\\.\\.\\."   // ...
                                          +"|--?"         // - or --
                                          +"|[a-z][a-z0-9_]*");   // word

            // create a matcher (with no given string)
            //mat = pat.matcher(null);
			 mat = pat.matcher(tail);
        }

        // get the next non-whitespace token from the command-line spec
        // all tokens should be recognized by the compiler
        // return next token, or null if end of pattern
        void next() {

            // Look for non-whitespace token
            int testcount = 50; // $$$$$$$$$$$
            do {
                if (testcount-- <= 0) {
                    System.exit(1);
                }
                mat.reset(tail);
                if (! mat.lookingAt()) {
                    if (tail.length() > 0) {
                        // error, characters that do not form token
                        patternError();
                    }
                    // Return an end-of-input marker
                    token = Character.toString(END_MARKER);
                    return;
                }
                token = tail.substring(0,mat.end());
                tail = tail.substring(mat.end());
            } while (token.charAt(0) <= ' ');
        }
    }

    private boolean maybe(char ch) {
        if (token.charAt(0) == ch) {
            tz.next();
            return true;
        }
        return false;
    }

    private void accept(char ch) {
        if (! maybe(ch)) {
            Log.print("CommandLine: expecting '"+ch+"'");
            patternError();
        }
    }

    private boolean isWord(String token) {
        char ch = token.charAt(0);
        return (ch >= 'a' && ch <= 'z' || ch == '_');
    }


    void patternError() {
        Log.quit("CommandLine: patternError at: "+token+tz.tail);
    }
}
