package org.starloco.locos.common;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

public class CryptManager {

    public final static char[] CELL_ENCODING_LOOKUP_TABLE = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '-', '_'};

    private static final int[] CELL_DECODING_LOOKUP_TABLE = new int[128];

    static {
        // Initialize all elements to -1 indicating invalid characters
        Arrays.fill(CELL_DECODING_LOOKUP_TABLE, -1);

        // Populate the decoding lookup table based on CELL_ENCODING_LOOKUP_TABLE
        for (int i = 0; i < CELL_ENCODING_LOOKUP_TABLE.length; i++) {
            CELL_DECODING_LOOKUP_TABLE[CELL_ENCODING_LOOKUP_TABLE[i]] = i;
        }
    }

    private final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String cellID_To_Code(int cellID) {
        return CELL_ENCODING_LOOKUP_TABLE[cellID >> 6] + "" + CELL_ENCODING_LOOKUP_TABLE[cellID & 0x3F];
    }

    public int cellCode_To_ID(String cellCode) {
        char char1 = cellCode.charAt(0), char2 = cellCode.charAt(1);
        int code1 = CELL_DECODING_LOOKUP_TABLE[char1] << 6, code2 = CELL_DECODING_LOOKUP_TABLE[char2];
        return code1 + code2;
    }

    public static int getIntByHashedValue(char c) {
        return CELL_DECODING_LOOKUP_TABLE[c];
    }

    public static char getHashedValueByInt(int c) {
        return CELL_ENCODING_LOOKUP_TABLE[c];
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
        return strArray[(num & 0xF)];
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
            result += c & 0xF;
        return result & 0xF;
    }

    private String decimalToHexadecimal(int c) {
        if(c > 255) c = 255;
        return HEX_CHARS[c >> 4] + "" + HEX_CHARS[c & 0xF];
    }

    private String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch >> 4));
                resultStr.append(toHex(ch & 0xF));
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
