package org.starloco.locos.database.data;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by Locos on 16/07/2017.
 */
public class BaseData extends FunctionDAO<Object> {
    public BaseData(HikariDataSource dataSource) {
        super(null, "");
    }

    @Override
    public void loadFully() {

    }

    @Override
    public Object load(int id) {
        return null;
    }

    @Override
    public boolean insert(Object entity) {
        return false;
    }

    @Override
    public void delete(Object entity) {

    }

    @Override
    public void update(Object entity) {

    }

    @Override
    public Class<?> getReferencedClass() {
        return BaseData.class;
    }
}
