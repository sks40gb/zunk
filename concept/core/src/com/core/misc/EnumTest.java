package com.core.misc;

/**
 *
 * @author sunil
 */
public class EnumTest {
    
  // Here, "static" may be left out, since enum types which are class
  // members are implicitly static.
    public enum Color{
        GREEN ("green"),RED("red"),BLUE("blue"){
            @Override
            public String getColor(){
                return "blue Override";
            }
        },YELLOW("yellow");  //note semicolon needed only when extending behavior

        
        String col;
        private Color(String name){
            this.col = name;
        }
        
        public String getColor(){
            return col.toLowerCase();
        }
    };

    public enum Day{
        SUNDAY(1),MONDAY(2),TUESDSAY(3),THURSDAY(4),FRIDAY(5){
            public int getMe(){
                return -1;
            }

        },SATURDAY(6);
        int dayInt;
        Day(int i){
            dayInt = i;
        }
        public int getDayNumber(){
            return dayInt;
        }
    }
    
    public static void main(String[] args) {

        Day day = Day.FRIDAY;
        System.out.println(Day.FRIDAY.getDayNumber());

        switch(day){
            case SUNDAY:
                System.out.println("Weekend");
                break;
            default:
                System.out.println("Weekday");
        }
       

        
//        Color col = Color.BLUE;
//
//        callme(col);
//
//        callme(Color.GREEN);
//
//        callme(Color.RED);
//
//        callme(Color.BLUE);
    }
    
    public static void callme(Color e){
        
        System.out.println(e + "======== " + e.getColor());        
        
    }
}
