package com.core.regex;


import java.util.Date;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.StringTokenizer;

/**
 *
 * @author sunil
 */
public class AdvanceValidationFunctions {

 String codedValue = "input";
 private static HashMap projectFieldsMap = new HashMap();
 String allParameters = null;
 
 
 //★★★★★★★★★★★★★  OLD FUNCITONS ★★★★★★★★★★★★★★★★★★★★★★★★
 
    public String beginChar(String errorMessage, String param) {
        String message = null;
        if (codedValue.startsWith(param) && param.length() == 1) {
           message = errorMessage;
        }
        return message;
    }

//    public String beginWord(String errorMessage, String param) {
//        String message = null;
//        if (codedValue.startsWith(param + " ")) {
//           message = errorMessage;
//        }
//        return message;
//    }

    //CHANGED
    public String beginWord(String errorMessage, String param) {
        String message = null;
        String pattern = "^\\s*" + param + "\\s";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {          
             message = errorMessage;
        }      
        return message;
    }
    
    public String chars(String errorMessage, String param) {
        String message = null;
        if (param.length() == 1) {
            if (codedValue.contains(param)) {
                message = errorMessage;
            }
        }
        return message;
    }

    
//    public String dateCoding(String errorMessage, String param) {
//        String message = null;
//        StringTokenizer tokens = null;
//        tokens = new StringTokenizer(codedValue, "/");
//        try {
//            String DD = tokens.nextToken();
//            String MM = tokens.nextToken();
//            String YYYY = tokens.nextToken();
//            int dd = Integer.parseInt(DD);
//            int mm = Integer.parseInt(MM);
//            int yyyy = Integer.parseInt(YYYY);
//            Date dt = new Date(dd, mm, yyyy);
//        } catch (Exception e) {
//            message = errorMessage;
//        }
//        return message;
//    }

    //CHANGED
    public String dateCoding(String errorMessage, String param) {
        String message = null;
        StringTokenizer tokens = null;
        String DateInStringFormat = null;
        if (codedValue.trim().isEmpty()) {
            return message;
        }
        tokens = new StringTokenizer(codedValue, "/");
        java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
        java.util.Date testDate = new java.util.GregorianCalendar().getTime();
        try {
            String DD = tokens.nextToken();
            String MM = tokens.nextToken();
            String YYYY = tokens.nextToken();
            DateInStringFormat = YYYY + MM + DD;
            testDate = format1.parse(DateInStringFormat);
        } catch (java.text.ParseException e) {
            message = errorMessage;
        }
        if (!format1.format(testDate).equals(DateInStringFormat)) {
            message = errorMessage;
        }
        return message;
    }
   
    
    public String endChar(String errorMessage, String param) {
        String message = null;
        if (codedValue.endsWith(param) && param.length() == 1) {
            message = errorMessage;
        }
        return message;
    }

    public String endWord(String errorMessage, String param) {
        String message = null;
        if (codedValue.endsWith(" " + param)) {
           message = errorMessage;
        }
        return message;
    }

    
//    public String hyphenInNumbers(String errorMessage, String param) {
//        String message = null;
//        boolean hasHypenAtStart = codedValue.startsWith("-");
//        boolean hasHypenAtEnd = codedValue.endsWith("-");
//        if (hasHypenAtStart || hasHypenAtEnd) {
//            message = errorMessage;
//        }
//        return message;
//    }
    //CHANGED 
    public String hyphenInNumbers(String errorMessage, String param) {
        String message = null;
        String pattern = "[A-Za-z@#$%&\\^\\(\\!~`\\|:;\"\'\\*\\+/\\.,)]+";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            return null;
        }
        pattern = "(^-)|(-$)|(\\s-)|(-\\s)";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(codedValue);
        if (m.find()) {
            message = errorMessage;          
        }
        return message;
    }

//    public String hyphenInWords(String errorMessage, String param) {
//        String message = null;
//        boolean hasHypenAtStart = codedValue.startsWith("-");
//        boolean hasHypenAtEnd = codedValue.endsWith("-");
//        if (hasHypenAtStart || hasHypenAtEnd) {
//           message = errorMessage;
//        }
//        return message;
//    }
    
    //CHANGED
    public String hyphenInWords(String errorMessage, String param) {
        String message = null;
        String pattern = "(^-)|(-$)|([^\\s]-)|(-[^\\s])";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }


//    public String monthCoding(String errorMessage, String param) {
//        String message = null;
//        if (codedValue.equalsIgnoreCase(param)) {
//            message = errorMessage;
//        }
//        return message;
//    }

    //CHANGED
    public String monthCoding(String errorMessage, String param) {
        String months = "jan,feb,mar,apr,may,jun,july,aug,sep,oct,nov,dec,January,February,March,April ,May,June,July,August";
        String message = null;
        if (codedValue.equalsIgnoreCase(param)) {
            message = errorMessage;
        }
        for (String month : months.split(",")) {
            if (month.equalsIgnoreCase(codedValue)) {
                message = errorMessage;
            }
        }
        return message;
    }
    
    private String periodInDecimal(String errorMessage, String param) {
        String message = null;
        if (codedValue.indexOf(".") == -1) {
            message = errorMessage;
        }
        if (codedValue.indexOf(".") != codedValue.lastIndexOf(".")) {
            message = errorMessage;
        }
        return message;
    }

//    public String slashInNumbers(String errorMessage, String param) {
//        String message = null;
//        boolean hasSlashAtStart = codedValue.startsWith("/");
//        boolean hasSlashAtEnd = codedValue.endsWith("/");
//        if (hasSlashAtStart || hasSlashAtEnd) {
//           message = errorMessage;
//        }
//        return message;
//    }
    
    //CHANGED
     public String slashInNumbers(String errorMessage, String param) {
        String message = null;
        String pattern = "[A-Za-z@#$%&\\^\\(\\!~`\\|:;\"\'\\*\\+\\.,)]+";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            return null;
        }
        pattern = "^/|/$|(/[\\s])|([\\s]/)";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(codedValue);
        if (m.find()) {
            message = errorMessage;          
        }
        return message;
    }
     
     
//    public String slashInWords(String errorMessage, String param) {
//        String message = null;
//        boolean hasSlashAtStart = codedValue.startsWith("/");
//        boolean hasSlashAtEnd = codedValue.endsWith("/");
//        if (hasSlashAtStart || hasSlashAtEnd) {
//           message = errorMessage;
//        }
//
//        return message;
//    }    
    
    //CHANGED
     public String slashInWords(String errorMessage, String param) {
        String message = null;
        String pattern = "(/[^\\s])|([^\\s]/)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }

//    public String pairsOfCurlyBraces(String errorMessage, String param) {
//        String message = null;
//        char singleLeftQuotes = '{';
//        char singleRightQuotes = '}';
//        int n = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleLeftQuotes) {
//                n++;
//            }
//        }
//
//        int m = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleRightQuotes) {
//                m++;
//            }
//        }
//        if (n != m) {
//            message = errorMessage;
//        }
//        return message;
//    }

    public String pairsOfCurlyBraces(String errorMessage, String param) {
        String message = null;
        char singleLeftCurlybraces = '{';
        char singleRightCurlybraces = '}';
        int n = 0;
        for (int j = 0; j < codedValue.length(); j++) {
            if (codedValue.charAt(j) == singleLeftCurlybraces) {
                n++;
            } else if (codedValue.charAt(j) == singleRightCurlybraces) {
                n--;
            }
            if (n < 0) {
                break;
            }
        }
        if (n != 0) {
            message = errorMessage;
        }
        return message;
    }
     
//    public String pairsOfSquareBracket(String errorMessage, String param) {
//        String message = null;
//        char singleLeftSquare = '[';
//        char singleRightSquare = ']';
//        int n = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleLeftSquare) {
//                n++;
//            }
//        }
//
//        int m = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleRightSquare) {
//                m++;
//            }
//        }
//        if (n != m) {
//            message = errorMessage;
//        }
//        return message;
//    }

//CHANGED    
     public String pairsOfSquareBracket(String errorMessage, String param) {
        String message = null;
        char singleLeftSquare = '[';
        char singleRightSquare = ']';
        int n = 0;
        for (int j = 0; j < codedValue.length(); j++) {
            if (codedValue.charAt(j) == singleLeftSquare) {
                n++;
            } else if (codedValue.charAt(j) == singleRightSquare) {
                n--;
            }
            if (n < 0) {
                break;
            }
        }
        if (n != 0) {
            message = errorMessage;
        }
        return message;
    }
       

            
//    public String pairsOfParanthesis(String errorMessage, String param) {
//        String message = null;
//        char singleLeftParanthesis = '(';
//        char singleRightParanthesis = ')';
//        int n = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleLeftParanthesis) {
//                n++;
//            }
//        }
//        int m = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleRightParanthesis) {
//                m++;
//            }
//        }
//        if (n != m) {
//            message = errorMessage;
//        }
//        return message;
//    }

    //CHANGED
    public String pairsOfParanthesis(String errorMessage, String param) {
        String message = null;
        char singleLeftParanthesis = '(';
        char singleRightParanthesis = ')';
        int n = 0;
        for (int j = 0; j < codedValue.length(); j++) {
            if (codedValue.charAt(j) == singleLeftParanthesis) {
                n++;
            } else if (codedValue.charAt(j) == singleRightParanthesis) {
                n--;
            }
            if (n < 0) {
                break;
            }
        }
        if (n != 0) {
            message = errorMessage;
        }
        return message;
    }
     
     
    public String word(String errorMessage, String param) {
        String message = null;
        if (codedValue.contains(param)) {
            message = errorMessage;
        }
        return message;
    }

 //★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
 
 
 //--------------sunil ------------------------------- 
 
    public String validCharacters(String errorMessage, String param) {
        String message = null;
        if (param.length() == 1) {
            if (codedValue.contains(param)){
                message = errorMessage;
            }
        }
        return message;
    }
    
 //====================== DOCUMENT SOURCE ======================================
// 
//public String bracketSpacing(String errorMessage , String param)
//{   
//    String message = null;
//    String pattern  = "((\\[[^\\s])|([^\\s]\\]))";
//    java.util.regex.Pattern p  = java.util.regex.Pattern.compile(pattern);
//    java.util.regex.Matcher m = p.matcher(codedValue);
//
//    if(m.find())
//    {
//        message = errorMessage;            
//    }
//    return message;
//}
   
    //CHANGED
     public String bracketSpacing(String errorMessage, String param) {
        String message = null;
        String pattern = "(\\[[^\\s].*])|(\\[.*[^\\s]\\])";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
     
    public String mandatoryFields(String errorMessage, String param) {
        String message = null;
        if (codedValue == null || codedValue.trim().equals("") || codedValue.isEmpty()) {
            message = errorMessage;
        }
        return message;        
    }
    
   

//    public  String commaInNumber (String errorMessage , String param)
//    {   
//        String message = null;        
//        String pattern  = "\\d+,\\d+";
//        java.util.regex.Pattern p  = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//        
//        if(m.find())
//        {            
//            message = errorMessage;            
//        }
//        return message;
//    }
    
 //CHANGED   
   public String commaInNumber(String errorMessage, String param) {
        String message = null;
        String pattern = "[A-Za-z@#$%&\\^\\(\\!~`\\|:;\"\'\\*\\+\\.)]+";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            return null;
        }
        pattern = "\\d+\\s*,\\s*\\d+";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(codedValue);
        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
    
    
//    public String consecutiveWord(String errorMessage, String param) {
//        String message = null;
//        String pattern = "\\w+\\s*";
//        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//
//        String match = null;
//        while (m.find()) {
//            if (m.group().trim().equals(match)) {
//                message = errorMessage;
//                break;
//            } else {
//                match = m.group().trim();
//            }
//        }
//        return message;
//    }
   
    //CHANGED
    public String consecutiveWord(String errorMessage, String param) {
        String message = null;
        String pattern = "\\b(\\w+)\\b.*\\b\\1\\b";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        String match = null;
        while (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
    
//    public String exactData(String errorMessage, String param) {
//        String message = null;
//        if (!codedValue.equals(param)) {
//            message = errorMessage + " " + param;
//        }
//        return message;
//    }
  
 //CHANGED   
    public String exactData(String errorMessage, String param) {
        String message = errorMessage;
        if (codedValue.trim().isEmpty()) {
            return null;
        }
        for (String s : allParameters.split(":")) {
            if (codedValue.trim().equals(s.trim())) {
                return null;
            }
        }
        return message;
    }
     
        
//    public String fractions(String errorMessage, String param) {
//        String message = null;
//        String pattern = "(\\d+\\s/\\d+|\\d+/\\s\\d+|\\d+\\s/\\s\\d+)";
//        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//        
//        while (m.find()) {
//               message = errorMessage;
//        }
//        return message;
//    }   
  
 // CHANGED   
    public String fraction(String errorMessage, String param) {
        String message = null;
        String pattern = "[A-Za-z@#$%&\\^\\(\\!~`\\|:;\"\'\\*\\+\\.,)]+";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            return null;
        }
        pattern = "(^\\s*/\\s*)|(/\\s*\\s*$)|(/[^\\s])|([^\\s]/)";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(codedValue);
        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
    public String hyphenatedWord(String errorMessage, String param) {
        String message = null;
        String pattern = "(\\w+\\s-\\w+|\\w+-\\s\\w+|\\w+\\s-\\s\\w+ )";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        while (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
    
    
    public String hyphenSpacing(String errorMessage, String param) {
        String message = null;
        String pattern = "([^(\\s)]-|-[^(\\s)]|^\\s*-|\\s*-$)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
//    public String illegibleWithOtherCodedData (String errorMessage, String param) {
//        String message = null;
//        String pattern = "\\[\\?\\]";
//        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//
//        if (m.find()) {
//            message = errorMessage;
//        }
//        return message;
//    }
    
        //  CHANGED    
//    public String illegibleWithOtherCodedData(String errorMessage, String param) {
//        String message = null;
//        String pattern = "\\w+\\[\\?\\]\\w+";
//        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//
//        if (m.find()) {
//            message = errorMessage;
//        }
//        return message;
//    }
    
    //  CHANGED _2    
    public String illegibleWithOtherCodedData(String errorMessage, String param) {
        String message = null;
        String pattern = "^.+\\[\\?\\].+$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue.trim());

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
//    public String paranthesis(String errorMessage, String param) {
//        String message = null;
//        char singleLeftParanthesis = '(';
//        char singleRightParanthesis = ')';
//        int n = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleLeftParanthesis) {
//                n++;
//            }
//        }
//        int m = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleRightParanthesis) {
//                m++;
//            }
//        }
//        if (n != m) {
//            message = errorMessage;
//        }
//        return message;
//    }

   
    //CHANGED
    public String paranthesis(String errorMessage, String param) {
        String message = null;
        char singleLeftParanthesis = '(';
        char singleRightParanthesis = ')';
        int n = 0;
        for (int j = 0; j < codedValue.length(); j++) {
            if (codedValue.charAt(j) == singleLeftParanthesis) {
                n++;
            } else if (codedValue.charAt(j) == singleRightParanthesis) {
                n--;
            }
            if (n < 0) {
                break;
            }
        }
        if (n != 0) {
            message = errorMessage;
        }
        return message;
    }
    
    
//    public String curlybraces(String errorMessage, String param) {
//        String message = null;
//        char singleLeftCurlybraces = '{';
//        char singleRightCurlybraces = '}';
//        int n = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleLeftCurlybraces) {
//                n++;
//            }
//        }
//        int m = 0;
//        for (int j = 0; j < codedValue.length(); j++) {
//            if (codedValue.charAt(j) == singleRightCurlybraces) {
//                m++;
//            }
//        }
//        if (n != m) {
//            message = errorMessage;
//        }
//        return message;
//    }
    
    //CHANGED
    public String curlybraces(String errorMessage, String param) {
        String message = null;
        char singleLeftCurlybraces = '{';
        char singleRightCurlybraces = '}';
        int n = 0;
        for (int j = 0; j < codedValue.length(); j++) {
            if (codedValue.charAt(j) == singleLeftCurlybraces) {
                n++;
            } else if (codedValue.charAt(j) == singleRightCurlybraces) {
                n--;
            }
            if (n < 0) {
                break;
            }
        }
        if (n != 0) {
            message = errorMessage;
        }
        return message;
    }
    
    
    //NOT WORKING PROPERLY
//    public String slashSpacing(String errorMessage, String param) {
//        String message = null;
//        String pattern = "([^(\\s)]/|/[^(\\s)])";
//        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//
//        if (m.find()) {
//            message = errorMessage;
//        }
//        return message;
//    }
    
    //CHANGED
    public String slashSpacing(String errorMessage, String param) {
        String message = null;
        String pattern = "(/[^\\s])|([^\\s]/)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
   
//=========================== SEARCH DATE ======================================
    
    public String currentDate(String errorMessage, String param) {
        String message = null;
        if (codedValue.trim().isEmpty()) {
            return message;
        }
        java.util.Date date = new java.util.GregorianCalendar().getTime();
        java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
        String currentDate = format1.format(date);
        if (!codedValue.equals(currentDate.toString())) {
            message = errorMessage;
        }
        return message;
    }
      
    public String dateFormat(String errorMessage, String param) {
        String message = null;
        if(codedValue.trim().isEmpty()){
           return null;     
        }
        java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
        java.util.Date testDate = new java.util.GregorianCalendar().getTime();
        try {
            testDate = format1.parse(codedValue);
        } catch (java.text.ParseException e) {
            message = errorMessage;
        }
        if (!format1.format(testDate).equals(codedValue)) {
            message = errorMessage;
        }
        return message;
    }
    
//    public String validDate(String errorMessage, String param) {
//        String message = null;
//        int dateInIntFormat = 0;
//        try {
//            dateInIntFormat = Integer.parseInt(codedValue);
//        } catch (java.lang.NumberFormatException nfe) {
//            message = errorMessage;
//            return message;
//        }
//        if (dateInIntFormat == 0) {
//            message = errorMessage;
//            return message;
//        } else if (codedValue.length() != 8) {
//            message = errorMessage;
//            return message;
//        }
//
//        return message;
//    }
    
    //CHANGED
//    public String validDate(String errorMessage, String param) {
//        String message = null;
//        if(codedValue.trim().isEmpty()){
//            return message;
//        }            
//        if (codedValue.length() != 8) {
//            return errorMessage;
//        }
//        try {
//            String DD = codedValue.substring(0, 2);
//            String MM = codedValue.substring(2, 4);
//            String YYYY = codedValue.substring(4, 8);
//            int dd = Integer.parseInt(DD);
//            int mm = Integer.parseInt(MM);
//            int yyyy = Integer.parseInt(YYYY);
//            System.out.println(dd + "/" + mm + "/" + yyyy);
//            java.util.Date dt = new java.util.Date(dd + "/" + mm + "/" + yyyy);
//        } catch (Exception e) {
//            return errorMessage;
//        }
//        return message;
//    }
    
    //CHANGED_2
     public String validDate(String errorMessage, String param) {
       String message = null;
        String dateStr = "";
        if (codedValue.trim().isEmpty()) {
            return message;
        }
        if(codedValue.trim().length() != 8){
            return  errorMessage;
        }
        try{
        String yyyy = codedValue.substring(0, 4);
        String mm = codedValue.substring(4, 6);
        String dd = codedValue.substring(6, 8);        
        yyyy = (yyyy.equals("0000") ?"2008" : yyyy);
        mm = (mm.equals("00") ? "01" : mm);
        dd = (dd.equals("00") ? "01" : dd);        
        int temp = new Integer(yyyy);
        temp = new Integer(mm);
        temp = new Integer(dd);
        
        dateStr = yyyy + mm + dd;
        }catch(java.lang.Exception e){            
            return errorMessage;
        }
        java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
        java.util.Date testDate = new java.util.GregorianCalendar().getTime();
        try {
            testDate = format1.parse(dateStr);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            message = errorMessage;
        }
        if (!format1.format(testDate).equals(dateStr)) {
            message = errorMessage;
        }
        return message;
    }
    
    public String zeroDate(String errorMessage, String param) {
        String message = null;
        int dateInIntFormat = 2008;
        try {
            dateInIntFormat = Integer.parseInt(codedValue);
        } catch (java.lang.NumberFormatException nfe) {
            //message = errorMessage;            
        }
        if (dateInIntFormat == 0) {
            message = errorMessage;            
        } else if (codedValue.length() != 8) {
            //message = errorMessage;           
        }

        return message;
    }
    
    //==================== BUSINESS CONTACT ===================================
    
    //NOT WORKING PROPERLY
     public String partiesFormatChecking(String errorMessage, String param) {
        if(codedValue.trim().isEmpty()){
            return null;
        }            
        String message = errorMessage;
        String pattern  = "^\\w*\\s*,?\\s*\\w*\\s*,?\\s*\\w*\\s*/\\w*\\s*,?\\s*\\w*\\s*,?\\s*\\w*\\s*$";           
        java.util.regex.Pattern p  = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if(m.find())
        {            
            message = null;
        }
        return message;
    }
    
     
    public String partiesNameFormat(String errorMessage, String param) {
        String message = null;
        String pattern = "((^\\w+\\*.)" +
                "|(^\\w*\\s\\w+)" +
                "|(\\[[^\\?]\\])" +
                "|(\\[.\\].*\\[.\\])" +
                "|(.\\s?\\w+\\*.*[\\w+^\\s+.+])" +
                "|(.+\\s\\w+\\*))";

        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
   
//============================== EMAIL =================================    
    public String illegibleFormat(String errorMessage, String param) {
        String message = null;
        String pattern = "\\[\\?\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }   
    
    public String multiIllegibleFormat(String errorMessage, String param) {
        String message = null;
        String pattern = "\\[\\?\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
    
    //NOT CLEARED
//    public String illegibleWithData(String errorMessage, String param) {
//       String message = null;
//        String pattern  = "\\[\\?\\]";           
//        java.util.regex.Pattern p  = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher m = p.matcher(codedValue);
//        
//        if(m.find())
//        {            
//            message = errorMessage;
//        }
//        return message;
//    }
   
 //CHANGED   
    public String illegibleWithData(String errorMessage, String param) {
        String message = null;
        String pattern = "^.+\\[\\?\\].+$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue.trim());

        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
   
    public String invalidValue(String errorMessage, String param) {
        String message = null;
        if (codedValue.equals(param)) {
            message = errorMessage;
        }
        return message;
    }
  
  //==================== DESCRIPTION / TITLE  ===============================
    public String chkBlankDESC(String errorMessage, String param) {
        String message = null;
        if (codedValue.trim().isEmpty()) {
            message = errorMessage;
        }
        return message;
    }
    
    
    
    public String chkIllegibleDESC(String errorMessage, String param) {
       String message = null;
        String pattern  = "\\[\\?\\]";           
        java.util.regex.Pattern p  = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        
        if(m.find())
        {            
            message = errorMessage;
        }
        return message;
    } 
    
    public String unmatchedDESC_FFNM(String errorMessage, String param) {
        String message = null;
        if (!codedValue.trim().equals((projectFieldsMap.get("File Folder Name")).toString())) {
            message = errorMessage;
        }
        return message;
    } 
   
   public String chkBlankDESC_FFNM(String errorMessage, String param) {
        String message = null;
        if ((codedValue == null || codedValue.trim().isEmpty()) && (! (projectFieldsMap.get("File Folder Name") == null || projectFieldsMap.get("File Folder Name").toString().trim().isEmpty()))) {
            message = errorMessage;
        }        
        return message;
    }  
   
  //========================= COMPLAINT PROBLEM ===========================
  
    public String chkCMPR(String errorMessage, String param) {
        String message = null;
        if (projectFieldsMap.get("Component") != null) {
            if ((!(codedValue == null || codedValue.trim().isEmpty())) && projectFieldsMap.get("Component").toString().trim().isEmpty()) {
                message = errorMessage;
            }
        }
        return message;
    }
  
 //================== FILE FOLDER NAME ============================
  
    public String missingDESCwithvalueonFFNM(String errorMessage, String param) {
        String message = null;
        if ((! codedValue.trim().isEmpty()) && (projectFieldsMap.get("DESC")!= null && projectFieldsMap.get("DESC").toString().trim().isEmpty())) {
            message = errorMessage;
        }
        return message;
    }
  
    /**
     *as it applies for file folder name only,so codedValue is nothing but the value of fields FFNM 
     *so codedValue = projectFieldsMap.get("File Folder Name")
     */
    public String unmatchedFFNM_DESC(String errorMessage, String param) {
        String message = null;
        if (projectFieldsMap.get("DESC") != null) {
            if (!(projectFieldsMap.get("DESC").toString()).equals(codedValue)) {
                message = errorMessage;
            }
        }
        return message;
    }
 
    public String invalidFFNMDTYSnotFileFolder(String errorMessage, String param) {
        String message = null;

        if ((!codedValue.trim().isEmpty()) && (projectFieldsMap.get("Specific Doc Type") != null && ( !projectFieldsMap.get("Specific Doc Type").toString().equals(codedValue)))) {
            message = errorMessage;
        }
        return message;
    }

 //======================  VEHICLE YEAR ======================================
 
    public String chkBlankVYER(String errorMessage, String param) {
        String message = null;
        if(codedValue.trim().isEmpty()){
            message = errorMessage;
        }
        return message;
    }
    
    public String chkVYERinFld(String errorMessage, String param) {
        String message = null;
        String vbod = null;
        Object obj = projectFieldsMap.get("Vehicle Body");
        vbod = ((obj == null) ? "DefULatValuefORVehICLE" : projectFieldsMap.get("Vehicle Body").toString());
        if (codedValue.equals(vbod)) {
            message = errorMessage;
        }
        return message;
    }
  
    public String rangeXVYER(String errorMessage, String param) {
        String message = null;
        if(codedValue.trim().isEmpty()){
            return message;
        }            
        if(codedValue.trim().length() < 5){
            return message;
        }
        String pattern = "(\\.|AND BEYOND)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (!m.find()) {
            message = errorMessage;
        }
        codedValue = codedValue.replace(".", "");
        codedValue = codedValue.replace("AND BEYOND", "");
        if (codedValue.trim().length() > 4) {
            message = errorMessage;
        }
        return message;
    }
 
  //*********** check this function ***************************
    public String rangeXVYER1900(String errorMessage, String param) {
        String message = null;
        if (codedValue.trim().isEmpty()) {
            return message;
        }
        try {
            if (new Integer(codedValue) < 1900) {
                message = errorMessage;
            }
        } catch (Exception e) {
            message = errorMessage;
        }
        return message;
    }

 //=================== VEHICLE MAKE =====================================
 public String chkListsInFld(String errorMessage, String param) {
        String message = errorMessage;
        for (String s : allParameters.split(":")) {
            if (codedValue.trim().equals(s.trim())) {
                return null;
            }
        }
        return message;
    }
 
 //=================== VEHICLE MODEL =====================================
    public String chkInvalidVMOD(String errorMessage, String param) {
        String message = null;
        if (codedValue.trim().equals(param)) {
            return errorMessage;
        }
        return message;
    }
 
 
 //################ cyrus ######################################3
 
 /***--------- DOCUMENT FEATURES ----------------****/
    
    public String validEntry(String errorMessage, String param) {
        String message = null;
        if (codedValue.trim().isEmpty()) {
            return message;
        }
        message = errorMessage;

        for (String s : allParameters.split(":")) {
            if (codedValue.equals(s)) {
                message = null;
                break;
            }
        }
        return message;
    }
 
    /***---------   NUMBER   ----------------****/
        
    //chkNumber Format
    public String chkNUMBERFormat(String errorMessage, String param) {
        String message = errorMessage;
        String pattern = "PAD\\s[A-Z0-9]?[A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        if (null != codedValue) {
            java.util.regex.Matcher m = p.matcher(codedValue);
            if (m.find()) {
                message = null;
            }
        }
        return message;
    }
    
    
    public String chkPADIllegible(String errorMessage, String param) {
        String message = null;
        if(codedValue.isEmpty()){
            return message;
        }
        String pattern = "PAD.*[?]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            message = errorMessage;
        }
        return message;
    }
     
     //chkPADEntry
     public String chkPADEntry(String errorMessage, String param) {
        String message = null;
        String pattern = "[a-zA-Z]";
        int count = 0;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);

        if (null != codedValue) {
            java.util.regex.Matcher m = p.matcher(codedValue);
            while (m.find()) {
                count = count + 1;
            }
            if (count > 4) { //The alphabets should be maximum PAD + 1 alphabet only

                message = errorMessage;
            }
        }
        return message;
    }
     //chkNumericPAD
     
    public String chkNumericPAD(String errorMessage, String param) {
        String message = null;
        String pattern = "[a-zA-Z]";
        int count = 0;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        if (null != codedValue) {
            java.util.regex.Matcher m = p.matcher(codedValue);
            while (m.find()) {
                count = count + 1;
            }
            if (count < 4) { //The alphabets should be maximum PAD + 1 alphabet only

                message = errorMessage;
            }
        }
        return message;
    }
    
     //chkNumEmpty
    public String chkNumEmpty(String errorMessage, String param) {
        String message = null;
        if (null == codedValue) {
            message = errorMessage;
        } else if (codedValue.trim().length() == 0) {
            message = errorMessage;
        }
        return message;
    }
     
     //chkNumRnumSubj
    public String chkNumRnumSubj(String errorMessage, String param) {
        String message = null;
        String rnumber = (String) projectFieldsMap.get("Reference Number");
        if (null != rnumber && rnumber.trim().length() != 0) {
            if (null == codedValue || codedValue.trim().isEmpty()) {
                message = errorMessage;
            }
        }
        return message;
    }
    
      //sNumPref
    public String sNumPref(String errorMessage, String param) {
        String message = null;
        if (null != codedValue) {
            String dtyg = (String) projectFieldsMap.get("General Document Type");
            String dtys = (String) projectFieldsMap.get("Specific Document Type");

            if (null != dtyg && null != dtys) {

                if (dtyg.equalsIgnoreCase("AUTHORIZATION")) {

                    if (dtys.equalsIgnoreCase("CORPORATE ENGINEERING AUTHORIZATION")) {
                        if (!codedValue.startsWith("CEA")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("ENGINEERING CHANGE AUTHORIZATION")) {
                        if (!codedValue.startsWith("ECA")) {
                            message = errorMessage;
                        }
                    }

                } else if (dtyg.equalsIgnoreCase("WORK ORDER")) {

                    if (dtys.equalsIgnoreCase("ENGINEERING WORK ORDER")) {
                        if (!codedValue.startsWith("EWO")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("BUILD WORK ORDER")) {
                        if (!codedValue.startsWith("BWO")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("WORK ORDER")) {
                        if (!codedValue.startsWith("WO")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("GENERAL WORK ORDER")) {
                        if (!codedValue.startsWith("GWO")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("DESIGN WORK ORDER")) {
                        if (!codedValue.startsWith("DWO")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("TEST WORK ORDER")) {
                        if (!codedValue.startsWith("TWO")) {
                            message = errorMessage;
                        }
                    } else if (dtys.equalsIgnoreCase("SHOP WORK ORDER") && dtys.equalsIgnoreCase("SHOP ORDER")) {

                        if (!codedValue.startsWith("SO")) {
                            message = errorMessage;
                        }
                    }

                } else if (dtyg.equalsIgnoreCase("COMMUNICATION") && dtys.equalsIgnoreCase("NHTSA")) {
                    if (!codedValue.startsWith("GM")) {
                        message = errorMessage;
                    }
                }
            }
        }

        return message;
    }     
    
      //Valid Characters -- already available
       
   
    //====================== REFERENCE NUMBER =============================
       
       
    public String chkREFNUMBERFormat(String errorMessage, String param) {
        if (codedValue.trim().isEmpty()) {
            return null;
        }
        String message = null;
        String pattern = "^\\s*PAD\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s[A-Z0-9][A-Z0-9]\\s*$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        if (null != codedValue) {
            java.util.regex.Matcher m = p.matcher(codedValue);
            if (!m.find()) {
                message = errorMessage;
            }
        }

        return message;
    }
        
     public String chkPeriodInNUMBRNUM(String errorMessage, String param) {
        String message = null;
        String pattern = "[A-Za-z@#$%&\\^\\(\\!~`\\|:;\"\'\\*\\+)]+";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(codedValue);
        if (m.find()) {
            return null;
        }
        if (null != codedValue) {
            if (codedValue.indexOf(".") != -1) {
                message = errorMessage;
            }
        }
        return message;
    }
     
//========================= OTHER BATES =======================================

    public String formatOBAT(String errorMessage, String param) {
        String message = null;
        codedValue = codedValue.trim();
        if ((!codedValue.isEmpty()) && (codedValue.length() != 5)) {
            message = errorMessage;
        }
        return message;
    }
    
    public String invalidOBATRange(String errorMessage, String param) {
        String message = errorMessage;
        if (codedValue.isEmpty()) {
            return null;
        }
        if (codedValue.split("-").length != 2) {
            return errorMessage;
        }
        String firstBate = codedValue.split("-")[0].trim();
        String lastBate = codedValue.split("-")[1].trim();
        String firstBateString = "";
        String lastBateString = "";
        int firstBateint = -1;
        int lastBateint = -1;
        String pattern = "\\d+\\s*";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(firstBate);
        if (m.find()) {
            String matchStr = m.group();
            firstBateString = firstBate.substring(0, firstBate.indexOf(matchStr));
            firstBateint = new Integer(matchStr).intValue();
        }
        m = p.matcher(lastBate);
        if (m.find()) {
            String matchStr = m.group();
            lastBateString = lastBate.substring(0, lastBate.indexOf(matchStr));
            lastBateint = new Integer(matchStr).intValue();
        }
        if (firstBateString.equals(lastBateString) && (firstBateint <= lastBateint)) {
            message = null;
        }
        return message;
    }
    
     public String FormatOBAT(String errorMessage, String param) {
        String message = null;
        codedValue = codedValue.trim();
        if ((!codedValue.isEmpty()) && (codedValue.length() != 5)) {
            message = errorMessage;
        }
        return message;
    }
//========================= OTHER BATES =======================================

    public String invalidOPNUMRange(String errorMessage, String param) {
        String message = errorMessage;
        if (codedValue.isEmpty()) {
            return null;
        }
        if (codedValue.split("-").length != 2) {
            return errorMessage;
        }
        String firstBate = codedValue.split("-")[0].trim();
        String lastBate = codedValue.split("-")[1].trim();
        String firstBateString = "";
        String lastBateString = "";
        int firstBateint = -1;
        int lastBateint = -1;
        String pattern = "\\d+\\s*";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(firstBate);
        if (m.find()) {
            String matchStr = m.group();
            firstBateString = firstBate.substring(0, firstBate.indexOf(matchStr));
            firstBateint = new Integer(matchStr).intValue();
        }
        m = p.matcher(lastBate);
        if (m.find()) {
            String matchStr = m.group();
            lastBateString = lastBate.substring(0, lastBate.indexOf(matchStr));
            lastBateint = new Integer(matchStr).intValue();
        }
        if (firstBateString.equals(lastBateString) && (firstBateint <= lastBateint)) {
            message = null;
        }
        return message;
    }
    
    //==================== VEHICLE BODY====================================
    public String chkVBODYear(String errorMessage, String param) {
        String message = null;
        if (codedValue.trim().isEmpty()) {
            codedValue = null;
        }
        if (codedValue.length() < 4) {
            message = errorMessage;
        }        
        String temp = codedValue.substring(0, 2);
        if (!(temp.equals("19") || temp.equals("20"))) {
            message = errorMessage;
        }
        return message;
    }
    
    public String chkVBODvsVPRO(String errorMessage, String param) {
        String message = null;
        if (projectFieldsMap.get("VPRO") != null) {
            if (projectFieldsMap.get("VPRO").toString().isEmpty() && codedValue.trim().isEmpty()) {
                return message;
            }
            if ((projectFieldsMap.get("VPRO").toString()).equals(codedValue)) {
                message = errorMessage;
            }
        }
        return message;
    }
       
}
