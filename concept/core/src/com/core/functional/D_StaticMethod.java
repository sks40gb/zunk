package com.core.functional;

/**
 * All the static method must be defined within the interface and keep objected related information in classes. In other
 * words, keep all static content in interface.
 *
 * Static method defined in the interface can only be accessed through Interface name only.
 *
 * interface can have main() method which can be called directly rather than creating the class for it.
 * 
 * @author sunsingh
 */
public class D_StaticMethod {

    public static void main(String[] args) {
        System.out.println(StringUtil.toUpperCase("John")); //@Valid
        //System.out.println(CommonUtil.toUpperCase("John")); // @Invalid
        //System.out.println(new CommonUtil().toUpperCase("John")); // @Invalid
    }

    interface StringUtil {

        public static String toUpperCase(String s) {
            return s.toUpperCase();
        }
        
         //this method can be called directly instead of creating new class for it.
         // Java StringUtil
         public static void main(String[] args) {
             System.out.println("Main method inside interface");
         }
    }

    class CommonUtil implements StringUtil {
        
    }

}
