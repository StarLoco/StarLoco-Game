package org.starloco.locos.common;

import org.apache.commons.lang.StringEscapeUtils;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class CryptManager {

    public final static char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '-', '_'};
    private final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String cellID_To_Code(int cellID) {
        return HASH[cellID>>6] + "" + HASH[cellID & 0x3F];
    }

    public int cellCode_To_ID(String cellCode) {

        char char1 = cellCode.charAt(0), char2 = cellCode.charAt(1);
        int code1 = -1, code2 = -1, a = 0;

        while (a < HASH.length) {
            if (HASH[a] == char1)
                code1 = a << 6;
            if (HASH[a] == char2)
                code2 = a;
            if(code1 != -1 && code2 != -1)
                return (code1 + code2);
            a++;
        }
        throw new IllegalStateException("invalid cellCode passed to cellCode_To_ID");
    }

    public static int getIntByHashedValue(char c) {
        for (int a = 0; a < HASH.length; a++)
            if (HASH[a] == c)
                return a;
        return -1;
    }

    public static char getHashedValueByInt(int c) {
        return HASH[c];
    }

    public String prepareMapDataKey(String key) {
        StringBuilder data = new StringBuilder();
        int num2 = (key.length() - 2), i = 0;

        while ((i <= num2)) {
            data.append((char) Integer.parseInt(key.substring(i, i+2), 16));
            i = (i + 2);
        }

        return unescape(data.toString());
    }

    private static String unescape(String data) {
        return StringEscapeUtils.unescapeJava(data);
    }

    public static String checksumKey(String data) {
        int num = 0;
        int num3 = (data.length() - 1);
        int i = 0;
        while ((i <= num3)) {
            num = (num + ((int) (data.substring(i, i + 1).charAt(0)) % 16));
            i++;
        }

        String[] strArray = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        return strArray[(num % 16)];
    }

    public static String decryptMapData(String mapData, String key) throws UnsupportedEncodingException {
        key = prepareKey(key);
        String strsum = checksumKey(key);
        int checksum = Integer.parseInt(strsum, 16) * 2;
        mapData = decypherData(mapData, key, checksum);
        return mapData;
    }

    public static String decypherData(String Data, String Key, int Checksum) {
        StringBuilder dataToDecrypt = new StringBuilder();
        int num4 = (Data.length() - 2);
        int i = 0;
        while ((i <= num4)) {
            String sub = Data.substring(i, i+2);
            int num = Integer.parseInt(sub, 16);
            int s = (int) (Math.round((double) (((((i) / 2) + Checksum) % (double) (Key.length())))));
            int num2 = (int) Key.substring(s, s+1).charAt(0);
            dataToDecrypt.append(String.valueOf((char) (((char) num) ^ ((char) num2))));
            i = (i + 2);
        }
        return unescape(dataToDecrypt.toString());
    }

    public static boolean isMapCiphered(String mapData) {
        int nb = 0;
        for (char a : mapData.toCharArray()) if (Character.isDigit(a)) nb++;
        return (nb > 1000);
    }

    // prepareData
    public String cryptMessage(String message, String key) {
        StringBuilder str = new StringBuilder();
        message = message.replace("'", "\'");
        // Append keyId
        str.append(HEX_CHARS[1]);
        // Append checksum
        int checksum = checksum(message);
        str.append(HEX_CHARS[checksum]);
        // Prepare key cause it's hexa form
        int c = checksum * 2;
        String data = encode(message);
        int keyLength = key.length();

        for (int i = 0; i < data.length(); i++)
            str.append(decimalToHexadecimal(data.charAt(i) ^ key.charAt((i + c) % keyLength)));

        return str.toString();
    }

    public String decryptMessage(String message, String key) {
        try {
            int c = Integer.parseInt(Character.toString(message.charAt(1)), 16) * 2;
            StringBuilder str = new StringBuilder();
            int j = 0, keyLength = key.length();

            for (int i = 2; i < message.length(); i = i + 2) {
                try {
                    str.append((char) (Integer.parseInt(message.substring(i, i + 2), 16) ^ key.charAt((j++ + c) % keyLength)));
                } catch (Exception ignored) {
                    System.out.println("CryptManager : DecryptMessage : " + message + " (key: " + key + ") : " + i + " to" + (i + 2));
                }
            }
            String data = str.toString();
            data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            data = data.replaceAll("\\+", "%2B");
            message = URLDecoder.decode(data, "UTF-8").replace("'", "\'");
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String prepareKey(String key) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < key.length(); i += 2)
            sb.append((char) Integer.parseInt(key.substring(i, i + 2), 16));

        return URLDecoder.decode(sb.toString(), "UTF-8");
    }

    private int checksum(String data) {
        int result = 0;
        for(char c : data.toCharArray())
            result += c % 16;
        return result % 16;
    }

    private String decimalToHexadecimal(int c) {
        if(c > 255) c = 255;
        return HEX_CHARS[c / 16] + "" + HEX_CHARS[c % 16];
    }

    private String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private boolean isUnsafe(char ch) {
        return ch > 255 || "+%".indexOf(ch) >= 0;
    }
}
