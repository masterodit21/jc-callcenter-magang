/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;


import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author lenovo
 */
public class StringManipulationAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        
        Nset data =currdata.getData("args");                
        String code = currdata.getData("code").toString().trim();
        
        String param1 = response.getVirtualString(data.get("param1").toString());
        String param2 = response.getVirtualString(data.get("param2").toString());
        String param3 = response.getVirtualString(data.get("param3").toString());
        
        
        String result = "";
        
        if (code.equalsIgnoreCase("concat")) {
            result = param1.concat(param2).concat(param3).concat(response.getVirtualString(data.get("param4").toString())).concat(response.getVirtualString(data.get("param5").toString())).concat(response.getVirtualString(data.get("param6").toString())).concat(response.getVirtualString(data.get("param7").toString())).concat(response.getVirtualString(data.get("param8").toString())).concat(response.getVirtualString(data.get("param9").toString()));
            
        }else if (code.equalsIgnoreCase("substring")) {
            try {
                if (Utility.getInt(param3)!=0) {
                    result = param1.substring(Utility.getInt(param2), Utility.getInt(param3));
                }else{
                    result = param1.substring(Utility.getInt(param2));
                }
            } catch (Exception e) { }
        }else if (code.equalsIgnoreCase("copystring")) {    
            try {
                if (Utility.getInt(param3)!=0) {
                    result = param1.substring(Utility.getInt(param2)-1, Utility.getInt(param2)-1+Utility.getInt(param3));
                }else{
                    result = param1.substring(Utility.getInt(param2)-1);
                }   
            } catch (Exception e) { }
        }else if (code.equalsIgnoreCase("copyfromright")) {    
            try {
                result = param1.substring(param1.length()-Utility.getInt(param2),param1.length());
            } catch (Exception e) { }
        }else if (code.equalsIgnoreCase("indexof")) {
            result = param1.indexOf(param2)+"";
        }else if (code.equalsIgnoreCase("split")) {
            Nset n = Nset.readsplitString(param1,param2);
            response.setVirtual(data.get("result").toString(), n);
            return true;
        }else if (code.equalsIgnoreCase("replace")) {
            result = param1.replace(param2, param3);
        }else if (code.equalsIgnoreCase("")) {
             result = formatString(param1, param2);
        }
        
        response.setVirtual(data.get("result").toString(), result);
        return true;
    }
    private String formatString(String code, String string){
        if (code.equalsIgnoreCase("currency")) {
            string = Utility.formatCurrency(string);
        }else if (code.equalsIgnoreCase("number")) {
            string = Utility.getNumber(string)+"";           
        }else if (code.equalsIgnoreCase("lowercase")) {
            string = string.toLowerCase();
        }else if (code.equalsIgnoreCase("uppercase")) {
            string = string.toUpperCase();
        }else if (code.equalsIgnoreCase("reverse")) {
            string = reverseWords(string);
        }else if (code.equalsIgnoreCase("length")) {
            string = string.length()+"";
        }else if (code.equalsIgnoreCase("trim")) {
            string = string.trim();
        }else if (code.equalsIgnoreCase("capital")) {
            string = WordUtils.capitalizeFully(string);
        }
        return string;
    }
    
    
    private static void reverse(char[] buf, int start, int end) {
        for (int i = start, j = end - 1; i < j; i++, j--) {
            char swap = buf[i];
            buf[i] = buf[j];
            buf[j] = swap;
        }
    }
    public static String reverseWords(String sentence) {
        char[] buf = sentence.toCharArray();

        // Reverse the string, character-wise
        reverse(buf, 0, buf.length);

        // Within each word, reverse the characters again
        int wordEnd = 0;
        for (int wordStart = 0; wordStart < buf.length; wordStart = wordEnd + 1) {
            for (wordEnd = wordStart; wordEnd < buf.length && buf[wordEnd] != ' '; wordEnd++) {}

            // wordStart is at the start of a word.
            // wordEnd is just past the end of the word.
            reverse(buf, wordStart, wordEnd);
        }
        return new String(buf);
    }
    
    
    
}
