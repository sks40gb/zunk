package com.core.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sunil
 */
public class MainRegEx {

    public static void main(String[] args) {
        greedy();
        reluctant();
        possessive();
    }

    public static void greedy() {
        String str = "raaam";
        Pattern pattern = Pattern.compile("r[a]+");
        Matcher m = pattern.matcher(str);

        if (m.find()) {
            System.out.println("greedy ------> " + m.group());
        }

    }

    public static void reluctant() {
        String str = "raaam";
        Pattern pattern = Pattern.compile("r[a]+?");
        Matcher m = pattern.matcher(str);

        if (m.find()) {
            System.out.println("reluctant ------> " + m.group());
        }

    }

    public static void possessive() {
        String str = "raaam";
        Pattern pattern = Pattern.compile("r[a]++");
        Matcher m = pattern.matcher(str);

        if (m.find()) {
            System.out.println("possessive------> " + m.group());
        }

    }
}
