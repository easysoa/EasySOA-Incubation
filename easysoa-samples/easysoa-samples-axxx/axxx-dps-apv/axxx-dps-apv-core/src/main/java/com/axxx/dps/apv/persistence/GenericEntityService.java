package com.axxx.dps.apv.persistence;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * To be used by the web layer.
 * This is the business layer, that marks transactional boundaries.
 * 
 * @author mdutoo
 *
 * @param <T>
 */
public interface GenericEntityService<T extends GenericEntity<T>> {

    public abstract GenericEntity<?> getEntity(Class<? extends GenericEntity<?>> clazz, Integer id);

    public abstract T getById(Number id);

    @Transactional
    public abstract void create(T entity);

    @Transactional
    public abstract void update(T entity);

    @Transactional
    public abstract void delete(T entity);

    @Transactional
    public abstract void delete(Number id);

    public abstract T refresh(T entity);

    public abstract List<T> list();

    public abstract Long count();

    public abstract void evict(T entity);

    public abstract void flush();

    public abstract void setReadOnly(T object, boolean readOnly);

    public abstract void clearSession();

}