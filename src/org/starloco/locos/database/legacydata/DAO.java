package org.starloco.locos.database.legacydata;

public interface DAO<T> {

    void loadFully();
    T load(int id);

    boolean insert(T entity);
    void delete(T entity);
    void update(T entity);

    Class<?> getReferencedClass();
    String getTableName();
}