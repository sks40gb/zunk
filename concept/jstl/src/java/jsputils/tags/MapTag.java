package jsputils.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import java.util.LinkedHashMap;

public class MapTag extends VarTagSupport
        implements DynamicAttributes {
    private LinkedHashMap map;

    public MapTag() {
        map = new LinkedHashMap();
    }

    public void setDynamicAttribute(String uri,
            String localName, Object value)
            throws JspException {
        map.put(localName, value);
    }

    public void doTag() throws JspException {
        export(map);
    }

}
