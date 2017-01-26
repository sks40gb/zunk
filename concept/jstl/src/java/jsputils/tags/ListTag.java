package jsputils.tags;

import javax.servlet.jsp.JspException;

import java.util.ArrayList;

import java.io.IOException;

public class ListTag extends VarTagSupport {
    private ArrayList list;

    public ListTag() {
        list = new ArrayList();
    }

    public void add(Object item) {
        list.add(item);
    }

    public void doTag() throws JspException, IOException {
        getJspBody().invoke(null);
        export(list);
    }

}
