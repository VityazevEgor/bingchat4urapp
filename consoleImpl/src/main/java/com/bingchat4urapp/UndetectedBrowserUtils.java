package com.bingchat4urapp;

import com.jogamp.common.util.InterruptSource.Thread;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class UndetectedBrowserUtils {
    public enum MessageType{
        html,
        documentState
    }
    public static class JSMessage {
        private MessageType type;
        private String data;

        public void setType(MessageType type){
            this.type = type;
        }

        public MessageType getType(){
            return this.type;
        }

        public void setData(String data){
            this.data = data;
        }
        
        public String getData(){
            return this.data;
        }
    }

    // Карта соответствия между кодом клавиши и символом
    private static final Map<Integer, Character> keyCodeToCharMap = new HashMap<>();

    static {
        // Заполнение карты соответствий 
        keyCodeToCharMap.put(KeyEvent.VK_A, 'a');
        keyCodeToCharMap.put(KeyEvent.VK_B, 'b');
        keyCodeToCharMap.put(KeyEvent.VK_C, 'c');
        keyCodeToCharMap.put(KeyEvent.VK_D, 'd');
        keyCodeToCharMap.put(KeyEvent.VK_E, 'e');
        keyCodeToCharMap.put(KeyEvent.VK_F, 'f');
        keyCodeToCharMap.put(KeyEvent.VK_G, 'g');
        keyCodeToCharMap.put(KeyEvent.VK_H, 'h');
        keyCodeToCharMap.put(KeyEvent.VK_I, 'i');
        keyCodeToCharMap.put(KeyEvent.VK_J, 'j');
        keyCodeToCharMap.put(KeyEvent.VK_K, 'k');
        keyCodeToCharMap.put(KeyEvent.VK_L, 'l');
        keyCodeToCharMap.put(KeyEvent.VK_M, 'm');
        keyCodeToCharMap.put(KeyEvent.VK_N, 'n');
        keyCodeToCharMap.put(KeyEvent.VK_O, 'o');
        keyCodeToCharMap.put(KeyEvent.VK_P, 'p');
        keyCodeToCharMap.put(KeyEvent.VK_Q, 'q');
        keyCodeToCharMap.put(KeyEvent.VK_R, 'r');
        keyCodeToCharMap.put(KeyEvent.VK_S, 's');
        keyCodeToCharMap.put(KeyEvent.VK_T, 't');
        keyCodeToCharMap.put(KeyEvent.VK_U, 'u');
        keyCodeToCharMap.put(KeyEvent.VK_V, 'v');
        keyCodeToCharMap.put(KeyEvent.VK_W, 'w');
        keyCodeToCharMap.put(KeyEvent.VK_X, 'x');
        keyCodeToCharMap.put(KeyEvent.VK_Y, 'y');
        keyCodeToCharMap.put(KeyEvent.VK_Z, 'z');
        keyCodeToCharMap.put(KeyEvent.VK_0, '0');
        keyCodeToCharMap.put(KeyEvent.VK_1, '1');
        keyCodeToCharMap.put(KeyEvent.VK_2, '2');
        keyCodeToCharMap.put(KeyEvent.VK_3, '3');
        keyCodeToCharMap.put(KeyEvent.VK_4, '4');
        keyCodeToCharMap.put(KeyEvent.VK_5, '5');
        keyCodeToCharMap.put(KeyEvent.VK_6, '6');
        keyCodeToCharMap.put(KeyEvent.VK_7, '7');
        keyCodeToCharMap.put(KeyEvent.VK_8, '8');
        keyCodeToCharMap.put(KeyEvent.VK_9, '9');

        // Пробел
        keyCodeToCharMap.put(KeyEvent.VK_SPACE, ' ');

        // Специальные символы (без Shift)
        keyCodeToCharMap.put(KeyEvent.VK_MINUS, '-'); // "-"
        keyCodeToCharMap.put(KeyEvent.VK_EQUALS, '='); // "="
        keyCodeToCharMap.put(KeyEvent.VK_BRACELEFT, '{'); // "{"
        keyCodeToCharMap.put(KeyEvent.VK_BRACERIGHT, '}'); // "}"
        keyCodeToCharMap.put(KeyEvent.VK_BRACELEFT, '['); // "["
        keyCodeToCharMap.put(KeyEvent.VK_BRACERIGHT, ']'); // "]"
        keyCodeToCharMap.put(KeyEvent.VK_SEMICOLON, ';'); // ";"
        keyCodeToCharMap.put(KeyEvent.VK_QUOTE, '\''); // "'"
        keyCodeToCharMap.put(KeyEvent.VK_BACK_SLASH, '\\'); // "\"
        keyCodeToCharMap.put(KeyEvent.VK_COMMA, ','); // ","
        keyCodeToCharMap.put(KeyEvent.VK_PERIOD, '.'); // "."
        keyCodeToCharMap.put(KeyEvent.VK_SLASH, '/'); // "/"
    }
    public static char getCharFromKeyCode(int keyCode) {
        return keyCodeToCharMap.getOrDefault(keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    public static Integer getKeyCodeFromChar(Character c){
        return keyCodeToCharMap.entrySet().stream().filter(item-> item.getValue().equals(c)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public static void sleep(long miliSeconds){
        try{
            Thread.sleep(miliSeconds);
        } catch (Exception ex){}
    }
}
