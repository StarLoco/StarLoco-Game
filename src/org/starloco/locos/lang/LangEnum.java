package org.starloco.locos.lang;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by Locos on 04/04/2018.
 */
public enum  LangEnum {

    FRENCH("fr"),
    ENGLISH("en"),
    SPANISH("es"),
    PORTUGUESE("pt");

    private final String flag;
    private Map<String,Object> result;

    LangEnum(String flag) {
        this.flag = flag;
        this.result = loadYAML(flag + "_" + flag.toUpperCase() + ".yaml");
    }

    public String getFlag() {
        return flag;
    }

    public String trans(String key, Object... str) {
        if(this.result == null) {
            result = loadYAML(flag + "_" + flag.toUpperCase() + ".yaml");
            return key + " result null";
        }
        String sentence = (String) this.result.get(key);
        if(sentence == null)
            return key + " not found";
        byte count = 1;
        for(Object t : str) {
            sentence = sentence.replace("#" + count, String.valueOf(t));
            count++;
        }
        return sentence;
    }

    private static Map<String, Object> loadYAML(String fileName) {
        return (Map<String, Object>) new Yaml().load(LangEnum.class.getResourceAsStream("translation/" + fileName));
    }
}
