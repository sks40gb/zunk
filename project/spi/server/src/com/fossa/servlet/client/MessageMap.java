/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.client;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JPasswordField;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class decodes an XML list of values into a Map or encode from a Map
 * into an XML value list.
 */
public class MessageMap implements MessageConstants {

    /**
     * Decode a list of values into a Map.
     * @param valueList An element whose children are
     *                  key-value pairs, with an attribute
     *                  "key" and a corresponding value
     *                  as content.  (e.g., "value_list",
     *                  "error_flag_list", ...)
     * @return a Map containing the same key-value pairs
     */
    public static Map decode(Element valueList) {
        Map result = null;    //Holds the attribute and its value.
        Node child = valueList.getFirstChild();
        String name = "";
        String value = "";
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) { 
                if (result == null) {
                   result = new HashMap();
                }
                Element childElement = (Element) child;
                name = childElement.getAttribute(A_NAME);                
                value = XmlUtil.getTextFromNode(child);                
                result.put(name, value);
            }
            child = child.getNextSibling();
        }
        return result;
    }

    /**
     * Used for Project_Fields to parse the field sequence from the name.
     * <p>
     * Note:  sequencing starts at 1, not 0
     * </p>
     * Input format:  <value name="1#sequence">1</value>
     *                <value name="1#field_name">Recip</value>
     *                <value name="1#field_type">name</value>
     *                      ...
     * Output format HashMap:  key="Recip" value=(HashMap) propertyMap
     *                         key="Copyee" value=(HashMap) propertyMap
     *                         ...
     * propertyMap format:        key="sequence" value=1
     *                            key="field_type" value=name
     *                            ...
     *                            key="values" valueMap
     * table_values:
     * valueMap format:        key=1:1 value=Email
     *                         key=1:2 value=Legal Document / Agreement
     * @see #decode
     * @see #encodeList
     */
    public static Map decodeList(Element valueList) {
        int tableSeq = -1;
        int valueSeq = -1;
        int priorTableSeq = -1;
        Map valueMap = new HashMap();

        Map result = new HashMap();
        Node child = valueList.getFirstChild();
        int priorSeq = -1;
        Map fieldMap = new HashMap();
        String name = "";
        ArrayList tableName = new ArrayList(1);
        tableName.add(0, "dummy");
        String value = "";
        int seq = -1;

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) { 
                Element childElement = (Element) child;
                // get the field sequence
                name = childElement.getAttribute(A_NAME);            
                if (name.indexOf("#") < 0) {
                    // must be a table value
                    tableSeq = Integer.parseInt(name.substring(0, name.indexOf(":")));
                    // remove the number from the name
                    valueSeq = Integer.parseInt(name.substring(name.indexOf(":") + 1));
                    value = XmlUtil.getTextFromNode(child);
                    // table value
                    if (tableSeq != priorTableSeq) {
                        if (priorTableSeq > -1) {                            
                            result.put((String)tableName.get(priorTableSeq), (Object)valueMap);
                            valueMap = new HashMap();
                        }
                        priorTableSeq = tableSeq;
                        valueMap.put(String.valueOf(valueSeq), value);
                    } else {
                        valueMap.put(String.valueOf(valueSeq), value);
                    }
                } else {
                    seq = Integer.parseInt(name.substring(0, name.indexOf("#")));
                    // remove the number from the name
                    name = name.substring(name.indexOf("#") + 1);
                    value = XmlUtil.getTextFromNode(child);
                    if (name.equals("table_name")) {
                        tableName.ensureCapacity(tableName.size() + 1);
                        tableName.add(tableName.size(), value);
                    }
                    
                    if (seq != priorSeq 
                        && seq == 0) {
                        // deleted field, write the fieldname as key with a null map                    
                        result.put(value, null);
                    } else if (seq != priorSeq) {
                        if (priorSeq > -1) {
                            result.put(String.valueOf(priorSeq), (Object)fieldMap);
                            fieldMap = new HashMap();
                        }
                        priorSeq = seq;
                        fieldMap.put(name, value);
                    } else {
                        fieldMap.put(name, value);
                    }
                }
            }
            child = child.getNextSibling();
        }
        if (priorSeq > -1 && seq != 0) {
            result.put(String.valueOf(seq), (Object)fieldMap);
        }
        if (priorTableSeq > -1 && tableSeq > 0) {
            // write the value map of the prior table
            result.put((String)tableName.get(priorTableSeq), (Object)valueMap);
        }
        return (result.size() == 0 ? null : result);
    }
    
    /**
     * Encode a map as an XML value_list.
     * @param writer The MessageWriter where result is written.
     * @param map The given Map.  May be null.
     */
    public static void encode(MessageWriter writer, Map map)
    throws IOException
    {
        encode(writer,map,null);
    }

    /**
     * Encode the changes to a map as an XML value_list.
     * @param writer The MessageWriter where result is written.
     * @param map The given Map.  May be null, even if
     *    old map is not null.
     * @param oldMap The map prior to changes.  May be null.
     */
    public static void encode(MessageWriter writer, Map map, Map oldMap)
    throws IOException
    {
        if (map == null) {
            // Note:  Can have oldMap non-null, if there are fields with values in them
            // that are not selected for inclusion on unitize screens.
            return;
        }        
        if (oldMap == null) {
            oldMap = Collections.EMPTY_MAP;
        }
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object oldValue = oldMap.get(key);
            if (oldValue == null || ! oldValue.equals(value)) {                
                writer.startElement(T_VALUE_LIST);                
                writer.startElement(T_VALUE);
                writer.writeAttribute(A_NAME, (String) key);
                writer.writeContent((String) value);
                writer.endElement();
            }
        }
        
         writer.endElement();
        
    }

    /**
     * Used for Project_Fields to put the field sequence on the property names.
     * <p>
     * Output format:  <value name="1#sequence">1</value>
     *                 <value name="1#field_name">Recip</value>
     *                 <value name="1#field_type">name</value>
     *                      ...
     * Input format HashMap:  key="Recip" value=(HashMap) valueMap
     *                        key="Copyee" value=(HashMap) valueMap
     *                        ...
     * valueMap format:       key="sequence" value=1
     *                        key="field_type" value=name
     *                        ...
     * @see #encode
     * @see #decodeList
     */
    public static void encodeList(MessageWriter writer, Map map, Map oldMap, String id)
    throws IOException
    {
        if (map == null && oldMap == null) {
            return;
        }
        Map old = oldMap;        
        Iterator it = null;
        if (oldMap == null) {
            oldMap = Collections.EMPTY_MAP;
            it = map.entrySet().iterator();
            old = oldMap;
        }
        if (map == null) {
            map = Collections.EMPTY_MAP;
            it = oldMap.entrySet().iterator();
            old = map;
        }
        if (it == null) {
            it = map.entrySet().iterator();
        }

        int seq = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            if (id.equals("")) {
                seq = Integer.parseInt((String)key);
            }
            
            /** value is the properties HashMap */
            Map value = (Map)entry.getValue();            
            Map oldValue = (Map)old.get(key);            
            Iterator itValue = null;
            if (value == null) {
                // null map because of deleted field
                
                writer.startElement(T_VALUE_LIST);                
                writer.startElement(T_VALUE);
                writer.writeAttribute(A_NAME, 0 + "#field_name");
                writer.writeContent((String)entry.getKey());
                writer.endElement();
            } else {
                if (oldValue == null) {
                    oldValue = Collections.EMPTY_MAP;
                }
                if (value != oldValue) {
                    
                    writer.startElement(T_VALUE_LIST);                    
                    itValue = value.entrySet().iterator();
                    while (itValue.hasNext()) {
                        if (value == null) {
                            seq = 0;
                        } else if (! id.equals("")) {
                            seq = Integer.parseInt((String)value.get((Object)id));
                        }
                        Map.Entry entryValue = (Map.Entry) itValue.next();
                        writer.startElement(T_VALUE);
                        writer.writeAttribute(A_NAME, seq + "#" + (String)entryValue.getKey());
                        String val = (String)entryValue.getValue();
                        writer.writeContent(val);
                        writer.endElement();
                    }
                }
            }
        }        
           writer.endElement();
        
    }

    /**
     * Used for users and teams to sequence the values for upload.
     * <p>
     * @param map - HashMap of the form:
     *      key="1u#users_id" value=3
     *      key="1u#teams_id" value=1
     *      key="1u#first_name" value=Bill
     *      key="1u#last_name" value=Easton
     *          ... (all users)
     *      key="13t#teams_id" value=1
     *      key="13t#users_id" value=3
     *      key="13t#team_name" value=Lexpar
     * Output format:  <value name="1u#users_id">1</value>
     *                 <value name="1u#teams_id">1</value>
     *                 <value name="1u#first_name">Bill</value>
     *                 <value name="1u#last_name">Easton</value>
     *                 <value name="1u#role">Admin</value>
     *                      ... (all users)
     *                 <value name="13t#teams_id">1</value>
     *                 <value name="13t#users_id">1</value>
     *                 <value name="13t#team_name">Lexpar</value>
     *                      ... (all teams)
     * @see #encode
     * @see #encodeList
     * @see #decodeList
     */
    public static void encodeUsersList(MessageWriter writer, Map map)
    throws IOException
    {
        if (map == null) {
            Log.print("(MessageMap).encodeUsersList map is null !!");
            return;
        }        
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String)entry.getKey();
            String value = "";
            if ( (entry.getValue()).getClass() == String.class ) {
                value = (String)entry.getValue();
            } else if ( (entry.getValue()).getClass() == Long.class ) {
                value = entry.getValue().toString();
            } else {
                char[] chrs = ((JPasswordField)entry.getValue()).getPassword();
                value = new String(chrs);
            }

            writer.startElement(T_VALUE_LIST);            
            writer.startElement(T_VALUE);
            writer.writeAttribute(A_NAME, key);
            writer.writeContent(value);            
            writer.endElement();
        }        
            writer.endElement();        
    }
}