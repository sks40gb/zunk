package jsputils.el;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.el.FunctionMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.HashMap;

public class FNMapper implements FunctionMapper {
    public static final String JSTL_FN_PREFIX = "fn";
    public static final String JSTL_FN_CLASS
        = "org.apache.taglibs.standard.functions.Functions";
    private static FNMapper JSTL_FN_Mapper = null;
    private HashMap functionMap;

    public static synchronized FNMapper getInstance(
            String id) throws JspException {
        if (!id.equals(JSTL_FN_PREFIX))
            throw new JspException("Unknown ID: " + id);
        if (JSTL_FN_Mapper == null)
            JSTL_FN_Mapper = new FNMapper(
                JSTL_FN_PREFIX, JSTL_FN_CLASS);
        return JSTL_FN_Mapper;
    }

    private FNMapper(String prefix, String className)
            throws JspException {
        functionMap = new HashMap();
        try {
            Class clazz = Class.forName(className);
            buildMap(prefix, clazz);
        } catch (ClassNotFoundException x) {
            throw new JspException(x);
        }
    }

    private void buildMap(String prefix, Class clazz) {
        Method methods[] = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (Modifier.isStatic(m.getModifiers()))
                functionMap.put(prefix+':'+m.getName(), m);
        }
    }

    public Method resolveFunction(
            String prefix, String localName) {
        return (Method) functionMap.get(prefix+':'+localName);
    }

}
