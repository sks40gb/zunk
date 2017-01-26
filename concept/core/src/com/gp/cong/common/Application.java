/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gp.cong.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author sunils
 */
public class Application {

    public static final String CONTEXT_PATH = "/logisoft";
    public static final String JPS_PATH = CONTEXT_PATH + "/jsps";
    public static final String JS_PATH = CONTEXT_PATH + "/js";
    public static final String CSS_PATH = CONTEXT_PATH + "/css";
    public static final String IMAGE_PATH = CONTEXT_PATH + "/img";
    private static Properties prop = null;

    private static void init() {
        prop = new Properties();
        try {
            System.out.println("PRO -------------> " + prop);
            prop.load(Application.class.getResourceAsStream("/com/gp/cong/common/application.properties"));
        } catch (FileNotFoundException e) {
            System.out.println("file is not found");
            //e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO is not found");
           // e.printStackTrace();
        }
    }

    public static String getProperty(String property) {
        init();
        if (prop == null) {
        }
        return prop.getProperty(property);
    }
}
