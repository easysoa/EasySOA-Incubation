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
//@Transactional(readOnly=true)
// NB. if no session in view filter, tx would always be required only to close session at its end
// (at least only readOnly by default)
public abstract class GenericEntityServiceImpl<T extends GenericEntity<T>> implements GenericEntityService<T> {

    /** to be implemented by returning an autowired field */
    protected abstract GenericEntityDao<T> getGenericDao();

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#getEntity(java.lang.Class, java.lang.Integer)
     */
    @Override
    public GenericEntity<?> getEntity(Class<? extends GenericEntity<?>> clazz, Integer id) {
        return getGenericDao().getEntity(clazz, id);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#getById(java.lang.Number)
     */
    @Override
    public T getById(Number id) {
        return getGenericDao().getById(id);
    }

    protected T getByField(String fieldName, String fieldValue) {
        return getGenericDao().getByField(fieldName, fieldValue);
    }

    /**
     * By default, calls the create method but this method can
     * be overrided to have a more complex business logic.
     */
    @Override
    @Transactional
    public void create(T entity) {
        if (entity.isNew()) {
            getGenericDao().create(entity);
        } else {
            throw new EntityNotNewException("The entity is not a new entity. You should use update instead of create.");
        }
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#update(T)
     */
    @Override
    @Transactional
    public void update(T entity) {
        getGenericDao().update(entity);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#delete(T)
     */
    @Override
    @Transactional
    public void delete(T entity) {
        getGenericDao().delete(entity);
    }

    @Override
    @Transactional
    public void delete(Number id) {
        T entity = getGenericDao().getById(id); // TODO exception if not found
        getGenericDao().delete(entity);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#refresh(T)
     */
    @Override
    public T refresh(T entity) {
        return getGenericDao().refresh(entity);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#list()
     */
    @Override
    public List<T> list() {
        return getGenericDao().list();
    }

    protected List<T> listByField(String fieldName, Object fieldValue) {
        return getGenericDao().listByField(fieldName, fieldValue);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#count()
     */
    @Override
    public Long count() {
        return getGenericDao().count();
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#evict(T)
     */
    @Override
    public void evict(T entity) {
        getGenericDao().flush();
        getGenericDao().evict(entity);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#flush()
     */
    @Override
    public void flush() {
        getGenericDao().flush();
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#setReadOnly(T, boolean)
     */
    @Override
    public void setReadOnly(T object, boolean readOnly) {
        getGenericDao().setReadOnly(object, readOnly);
    }

    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityService#clearSession()
     */
    @Override
    public void clearSession() {
        getGenericDao().clearSession();
    }

}
