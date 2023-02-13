package org.starloco.locos.database.data;

public interface DAO<T> {

    void loadFully();
    T load(int id);

    boolean insert(T entity);
    void delete(T entity);
    void update(T entity);

    Class<?> getReferencedClass();
    String getTableName();
}