package com.axxx.dps.apv.persistence;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface GenericEntityDao<T extends GenericEntity<T>> {

    public abstract GenericEntity<?> getEntity(Class<? extends GenericEntity<?>> clazz, Integer id);

    public abstract T getById(Number id);

    public abstract T getByField(String fieldName, Object fieldValue);

    public abstract void update(T entity);

    public abstract void create(T entity) throws RuntimeException;

    public abstract void saveOrUpdate(T entity);

    public abstract void delete(T entity);

    public abstract T refresh(T entity);

    public abstract List<T> list();

    public abstract List<T> listByField(String fieldName, Object fieldValue);

    public abstract List<T> list(Class<? extends T> objectClass, Criterion filter, Order order, Integer limit,
            Integer offset);

    public abstract Long count();

    public abstract Long count(Class<? extends T> objectClass, Criterion filter, Order order, Integer limit,
            Integer offset);

    public abstract void evict(T object);

    public abstract void evictCollection(String roleName);

    public abstract void flush();

    public abstract void setReadOnly(T object, boolean readOnly);

    public abstract void clearSession();

}