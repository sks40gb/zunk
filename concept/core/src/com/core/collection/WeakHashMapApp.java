
package com.core.collection;

import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author Sunil
 */
public class WeakHashMapApp {
   private static Map map;
   public static void main (String args[]) {
      map = new WeakHashMap();
      map.put(new String("Maine"), "Augusta");
      
      Runnable runner = new Runnable() {
         public void run() {
            while (map.containsKey("Maine")) {
               try {
                  Thread.sleep(500);
               } catch (InterruptedException ignored) {
               }
               System.out.println("Thread waiting");
               System.gc();   // Value will be removed after GC
            }
         }
      };
      Thread t = new Thread(runner);
      t.start();
      System.out.println("Main waiting");
      try {
         t.join();
      } catch (InterruptedException ignored) {
      }
   }
}