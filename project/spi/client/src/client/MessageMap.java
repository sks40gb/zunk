/* $Header: /home/common/cvsarea/ibase/dia/src/client/MessageMap.java,v 1.36.6.3 2006/03/09 12:09:16 nancy Exp $ */
package client;

//import common.CodingData;
import common.Log;
import common.Validation;
import common.msg.MessageConstants;
import common.msg.MessageWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPasswordField;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Decode an XML list of values into a Map or encode from a Map
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
        Map result = null;
        Node child = valueList.getFirstChild();
        while (child != null) {          
            if (child.getNodeType() == Node.ELEMENT_NODE) { 
                if (result == null) {
                   result = new HashMap();
                }
                Element childElement = (Element) child;
                String name = childElement.getAttribute(A_NAME);
                String value = "";
                value = common.msg.XmlUtil.getTextFromNode(child);                
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
                    value = common.msg.XmlUtil.getTextFromNode(child);
                    // table value
                    if (tableSeq != priorTableSeq) {
                        if (priorTableSeq > -1) {
                            result.put((String) tableName.get(priorTableSeq), (Object) valueMap);
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
                    value = common.msg.XmlUtil.getTextFromNode(child);
                    if (name.equals("table_name")) {
                        tableName.ensureCapacity(tableName.size() + 1);
                        tableName.add(tableName.size(), value);
                    }
                    if (seq != priorSeq && seq == 0) {
                        // deleted field, write the fieldname as key with a null map
                        result.put(value, null);
                    } else if (seq != priorSeq) {
                        if (priorSeq > -1) {
                            result.put(String.valueOf(priorSeq), (Object) fieldMap);
                            fieldMap = new HashMap();
                        }
                        priorSeq = seq;
                        //Log.print("(MessageMap).decodeList put fieldMap" + name + "/" + value);
                        fieldMap.put(name, value);
                    } else {
                        //Log.print("(MessageMap).decodeList put fieldMap" + name + "/" + value);
                        fieldMap.put(name, value);
                    }
                }
            }
            child = child.getNextSibling();
        }
        if (priorSeq > -1 && seq != 0) {
            //Log.print("(MessageMap).decodeList final" + seq + "/" + name);
            result.put(String.valueOf(seq), (Object)fieldMap);
        }
        if (priorTableSeq > -1 && tableSeq > 0) {
            // write the value map of the prior table
            result.put((String)tableName.get(priorTableSeq), (Object)valueMap);
        }
        return (result.size() == 0 ? null : result);
    }
    
    public static Map validationsList(Element validations) {
        Map validationMap = new HashMap(); 
        String name = "";
        ArrayList validationDataList = null;
        Node child = validations.getFirstChild(); //This is the first 'field'
        
         while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                name = childElement.getAttribute(A_NAME); 
                NodeList rowElement = childElement.getElementsByTagName(T_ROW);                                 
                // 1. I have got the rows. Create a new ListOfValidations List Object.
                validationDataList = new ArrayList(); // for each field , we have a list of Validtion objects
                
                if (rowElement.getLength() > 0) {
                    //2. Iterate the rows. Go to the first row.
                    for (int i = 0; i < rowElement.getLength(); i++) {
                        //3. Open the row. Get the ElementsByTagName(T_COLUMN)
                        Element row = (Element) (rowElement.item(i));
                        //4. You will get a NodeList of Columns. Iterate this List
                        NodeList columns = row.getElementsByTagName(T_COLUMN);
                        Validation validation = new Validation();
                        
                        if (columns.getLength() > 0) {
                            //5. Get the first Node. Get the function name from this node.
                            //6. Go to the next Node. Get the parametes from this node.
                            //7. Construct a Validation object and set the function name and parameters using setters/getters
                            validation.setFunctionName(columns.item(0).getTextContent());
                            validation.setFunctionBody(columns.item(1).getTextContent());
                            validation.setParameter(columns.item(2).getTextContent());
                            validation.setErrorMessage(columns.item(3).getTextContent());
                            validation.setFieldType(columns.item(4).getTextContent());
                            //8. Add the Validation Object to the ListOfValidations 
                            validationDataList.add(validation);
                        }
                    }//9. Go to the next T_ROW as in step 2 which is nothing but the continuation of iteration
                } //10. You have reached the end of T_ROW iteration. 
                //11. Now create a Map named MapValidations. Map the A_NAME with ListOfValidations Object.

                validationMap.put(name, validationDataList);
               // System.out.println("Mapping " + name + " with validations as" + validationDataList.size());
            }            
            //12. Now move to the next field
            child = child.getNextSibling();
         }
        return validationMap;
    }
    
    /**
     * Encode a map as an XML value_list.
     * @param writer The MessageWriter where result is written.
     * @param map The given Map.  May be null.
     */
    public static void encode(MessageWriter writer, Map map)
            throws IOException {
        encode(writer, map, null);
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
        boolean changeSeen = false;
        if (oldMap == null) {
            oldMap = Collections.EMPTY_MAP;
        }
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object oldValue = oldMap.get(key);
            //System.out.println("MessageMap.encode: "+key+": '"+oldValue+"'-->'"+value+"'");
            if (oldValue == null || ! oldValue.equals(value)) {
                if (! changeSeen) {
                    changeSeen = true;
                    writer.startElement(T_VALUE_LIST);
                }
                writer.startElement(T_VALUE);
                writer.writeAttribute(A_NAME, (String) key);
                writer.writeContent((String) value);
                writer.endElement();
            }
        }
        if (changeSeen) {
            writer.endElement();
        }
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
        //Log.print("MessageMap.encodeList) enter");
        Map old = oldMap;
        boolean changeSeen = false;
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
                if (! changeSeen) {
                    changeSeen = true;
                    writer.startElement(T_VALUE_LIST);
                }
                writer.startElement(T_VALUE);
                writer.writeAttribute(A_NAME, 0 + "#field_name");
                writer.writeContent((String)entry.getKey());
                writer.endElement();
            } else {
                if (oldValue == null) {
                    //Log.print("MessageMap.encodeList) oldValue == null for " + key);
                    oldValue = Collections.EMPTY_MAP;
                }
                if (value != oldValue) {
                    if (! changeSeen) {
                        changeSeen = true;
                        writer.startElement(T_VALUE_LIST);
                    }
                    itValue = value.entrySet().iterator();
                    while (itValue.hasNext()) {
                        if (value == null) {
                            seq = 0;
                        } else if (! id.equals("")) {
                            seq = Integer.parseInt((String)value.get((Object)id));
                        }
                        Map.Entry entryValue = (Map.Entry) itValue.next();
                        writer.startElement(T_VALUE);
                        //Log.print("(MessageMap.encodeList) " + seq + "#" + (String)entryValue.getKey()
                        //          + "=" + entryValue.getValue());
                        writer.writeAttribute(A_NAME, seq + "#" + (String)entryValue.getKey());
                        String val = (String)entryValue.getValue();
                        writer.writeContent(val);
                        writer.endElement();
                    }
                }
            }
        }
        if (changeSeen) {
            writer.endElement();
        }
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
        //Log.print("(MessageMap).encodeUsersList map is " + map.size());
        boolean changeSeen = false;
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

            //Log.print("(MessageMap).encodeUsersList key/value " + key + "/" + value);
            
            if (! changeSeen) {
                changeSeen = true;
                writer.startElement(T_VALUE_LIST);
            }
            writer.startElement(T_VALUE);
            writer.writeAttribute(A_NAME, key);
            writer.writeContent(value);            
            writer.endElement();
        }
        if (changeSeen) {
            writer.endElement();
        }
    }
}
