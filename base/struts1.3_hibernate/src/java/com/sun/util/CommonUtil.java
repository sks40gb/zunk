/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.util;

import com.sun.constant.CommonConstant;
import java.util.Collection;

/**
 *
 * @author shanmugam
 */
public class CommonUtil {

    public static boolean isNull(Object o){
        return ! isNotNull(o);
    }
    
    public static  boolean isNotNull(Object o){
        if(o == null || o.toString().trim().equals(CommonConstant.EMPTY)){
            return false;
        }else{
          return true;
        }
    }
    public static  boolean isNotEmpty(Collection list){
        if(list == null || list.isEmpty()){
            return false;
        }else{
          return true;
        }
    }

}
