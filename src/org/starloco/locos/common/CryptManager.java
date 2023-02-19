package org.starloco.locos.common;

import org.apache.commons.lang.StringEscapeUtils;
import org.starloco.locos.area.map.CellCacheImpl;
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

    public String cellID_To_Code(int cellID) {

        int char1 = cellID / 64, char2 = cellID % 64;
        return HASH[char1] + "" + HASH[char2];
    }

    public int cellCode_To_ID(String cellCode) {

        char char1 = cellCode.charAt(0), char2 = cellCode.charAt(1);
        int code1 = 0, code2 = 0, a = 0;

        while (a < HASH.length) {
            if (HASH[a] == char1)
                code1 = a * 64;
            if (HASH[a] == char2)
                code2 = a;
            a++;
        }
        return (code1 + code2);
    }

    public int getIntByHashedValue(char c) {
        for (int a = 0; a < HASH.length; a++)
            if (HASH[a] == c)
                return a;
        return -1;
    }

    public char getHashedValueByInt(int c) {
        return HASH[c];
    }

    public ArrayList<GameCase> parseStartCell(GameMap map, int num) {
        ArrayList<GameCase> list = null;
        String infos;
        if (!map.getPlaces().equalsIgnoreCase("-1")) {
            infos = map.getPlaces().split("\\|")[num];
            int a = 0;
            list = new ArrayList<>();
            while (a < infos.length()) {
                GameCase cell = map.getCase((getIntByHashedValue(infos.charAt(a)) << 6) + getIntByHashedValue(infos.charAt(a + 1)));
                if(cell != null && cell.isWalkable(false))
                    list.add(cell);
                a = a + 2;
            }
        }
        return list;
    }

    public String key = "8fd8ad4a38cdd0432248a76f8f148ceb";

    private List<Short> cellWalkable(GameMap map){
        List<Short> limit = new ArrayList<>();
        short H = map.getH();
        short W = (short) (map.getW()-1);
        short val = 0;
        for (short h = 0; h < H; h++ ){
            if (h ==0) val = W;
            else val = (short) (val + (W * 2) + 1);
            limit.add(val);
            limit.add((short) (val-W));
        }
        for (short w = 1; w < W; w++){
            limit.add(w);
            limit.add((short) (((H * (W+1)+((H-1)*W))-1)-w));
        }
        return limit;
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

    private String unescape(String data) {
        return StringEscapeUtils.unescapeJava(data);
    }

    public String checksumKey(String data) {
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

    public String decryptMapData(String mapData, String key) {
        key = prepareKey(key);
        String strsum = checksumKey(key);
        int checksum = Integer.parseInt(strsum, 16) * 2;
        mapData = decypherData(mapData, key, checksum);
        return mapData;
    }

    public String decypherData(String Data, String Key, int Checksum) {
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

    private boolean mapCrypted(String mapData) {
        int nb = 0;
        for (char a : mapData.toCharArray()) if (Character.isDigit(a)) nb++;
        return (nb > 1000);
    }

    public List<GameCase> decompileMapData(GameMap map, String data, byte sniffed) {
        List<GameCase> cells = new ArrayList<>();
        List<Short> losCells = new ArrayList<>();

        if(mapCrypted(data) && !map.getKey().isEmpty()) {
            try {
                data = this.decryptMapData(data, map.getKey());
            } catch (Exception e) {
                System.err.println("Erreur decypher map data : " + map.getId());
                e.printStackTrace();
            }
        }
        if(PathFinding.outForbiddenCells.get(map.getW() + "_" + map.getH()) == null)
            PathFinding.outForbiddenCells.put(map.getW() + "_" + map.getH(), cellWalkable(map));
        try {
            short cellId = 0;
            for (; cellId < data.length()/10; cellId ++ ){
                String cellData = data.substring(cellId*10, (cellId+1)*10);
                byte[] array = new byte[10];
                for (int i = 0; i < cellData.length(); i++)
                    array[i] = (byte) getIntByHashedValue(cellData.charAt(i));

                boolean walkable = true, los = true;
                short groundSlope, groundLevel;
                int io;

                walkable = (((array[2] & 56 ) >> 3) != 0 && !cellData.equalsIgnoreCase("bhGaeaaaaa") && !cellData.equalsIgnoreCase("Hhaaeaaaaa"));
                if((array[0] & 1) == 0)
                    los = false;
                if(los) losCells.add(cellId);
                short tmp = (short) ((array[4] & 60) >> 2);
                if (tmp != 1) groundSlope = tmp;

                tmp = (short) (array[1] & 15);
                if (tmp != 0) groundLevel = tmp;

                int layerObject2 = ((array[0] & 2) << 12) + ((array[7] & 1) << 12) + (array[8] << 6) + array[9];
                boolean layerObject2Interactive = ((array[7] & 2) >> 1) != 0;
                int obj = (layerObject2Interactive?layerObject2:-1);

                cells.add(new GameCase(map, cellId, walkable, los, obj));

            }
            CellCacheImpl cache = new CellCacheImpl(losCells, map.getW(), map.getH());
            map.setCellCache(cache);
        } catch (Exception e) {
            System.err.println(e.getMessage() + " : mapId : " + map.getId());
            e.printStackTrace();
        }
        return cells;
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

    public String prepareKey(String key) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < key.length(); i += 2)
            sb.append((char) Integer.parseInt(key.substring(i, i + 2), 16));

        try {
            return URLDecoder.decode(sb.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
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
