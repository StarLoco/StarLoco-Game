package org.starloco.locos.script.converter;


public interface ConverterInterface<Type> {

    void write(Type type, String content);
}
