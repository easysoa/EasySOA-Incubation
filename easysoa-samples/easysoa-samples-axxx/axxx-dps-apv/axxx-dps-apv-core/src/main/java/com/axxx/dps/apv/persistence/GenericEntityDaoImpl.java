package com.axxx.dps.apv.persistence;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;


/**
 * To be extended for a given entity type, and annotated by @Repository
 * (access sessionFactory, instead of "session in view pattern")
 * 
 * @author mdutoo
 *
 * @param <T>
 */
public abstract class GenericEntityDaoImpl<T extends GenericEntity<T>> implements GenericEntityDao<T> {

    @Autowired
    private SessionFactory sessionFactory;
    
    private Class<T> objectClass;
    
    @SuppressWarnings("unchecked")
    public GenericEntityDaoImpl() {
        int retriesCount = 0;
        Class<?> clazz = getClass();
        while(!(clazz.getGenericSuperclass() instanceof ParameterizedType) && (retriesCount < 5)) {
            clazz = clazz.getSuperclass();
            retriesCount ++;
        }
        objectClass = (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    protected final Class<T> getObjectClass() {
        return objectClass;
    }
    
    protected final SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#getEntity(java.lang.Class, java.lang.Integer)
     */
    @Override
    public GenericEntity<?> getEntity(Class<? extends GenericEntity<?>> clazz, Integer id) {
        return (GenericEntity<?>) sessionFactory.getCurrentSession().get(clazz, id);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#getById(java.lang.Number)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getById(Number id) {
        return (T) sessionFactory.getCurrentSession().get(getObjectClass(), id);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#getByField(java.lang.String, java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getByField(String fieldName, Object fieldValue) {
        return (T) sessionFactory.getCurrentSession().createCriteria(getObjectClass()).add(Restrictions.eq(fieldName, fieldValue)).uniqueResult();
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#update(T)
     */
    @Override
    public void update(T entity) {
        sessionFactory.getCurrentSession().update(entity);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#create(T)
     */
    @Override
    public void create(T entity) throws EntityNotNewException {
        if (entity.isNew()) {
            sessionFactory.getCurrentSession().save(entity);
        } else {
            throw new EntityNotNewException("Entity ID : " + entity.getId());
        }
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#saveOrUpdate(T)
     */
    @Override
    public void saveOrUpdate(T entity) {
        if (entity.isNew()) {
            sessionFactory.getCurrentSession().save(entity);
        } else {
            sessionFactory.getCurrentSession().update(entity);
        }
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#delete(T)
     */
    @Override
    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#refresh(T)
     */
    @Override
    public T refresh(T entity) {
        sessionFactory.getCurrentSession().refresh(entity);
        
        return entity;
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#list()
     */
    @Override
    public List<T> list() {
        return list(getObjectClass(), null, null, null, null);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#listByField(java.lang.String, java.lang.Object)
     */
    @Override
    public List<T> listByField(String fieldName, Object fieldValue) {
        Criterion filter = Restrictions.eq(fieldName, fieldValue);
        return list(getObjectClass(), filter, null, null, null);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#list(java.lang.Class, org.hibernate.criterion.Criterion, org.hibernate.criterion.Order, java.lang.Integer, java.lang.Integer)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> list(Class<? extends T> objectClass, Criterion filter, Order order, Integer limit, Integer offset) {
        List<T> entities = new LinkedList<T>();
        try {
            Criteria criteria = buildCriteria(objectClass, null, filter, order, limit, offset);
            
            entities = criteria.list();
            
            if(order == null) {
                Collections.sort(entities);
            }
            
            return entities;
        } catch(DataAccessException e) {
            return entities;
        }
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#count()
     */
    @Override
    public Long count() {
        return count(getObjectClass(), null, null, null, null);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#count(java.lang.Class, org.hibernate.criterion.Criterion, org.hibernate.criterion.Order, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public Long count(Class<? extends T> objectClass, Criterion filter, Order order, Integer limit, Integer offset) {
        Criteria criteria = buildCriteria(objectClass, Projections.rowCount(), filter, order, limit, offset);
        
        Long count = (Long) criteria.uniqueResult();
        
        return count;
    }
    
    protected Criteria buildCriteria(Class<? extends T> objectClass,
            Projection projection, Criterion filter, Order order, Integer limit, Integer offset) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(objectClass);
        if(projection != null) {
            criteria.setProjection(projection);
        }
        if(filter != null) {
            criteria.add(filter);
        }
        if(limit != null) {
            criteria.setMaxResults(limit);
        }
        if(offset != null) {
            criteria.setFirstResult(offset);
        }
        if(order != null) {
            criteria.addOrder(order);
        }
        return criteria;
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#evict(T)
     */
    @Override
    public void evict(T object) {
        sessionFactory.getCurrentSession().evict(object);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#evictCollection(java.lang.String)
     */
    @Override
    public void evictCollection(String roleName) {
        sessionFactory.getCache().evictCollectionRegion(roleName);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#flush()
     */
    @Override
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#setReadOnly(T, boolean)
     */
    @Override
    public void setReadOnly(T object, boolean readOnly) {
        sessionFactory.getCurrentSession().setReadOnly(object, readOnly);
    }
    
    /* (non-Javadoc)
     * @see com.axxx.dps.apv.persistence.GenericEntityDao#clearSession()
     */
    @Override
    public void clearSession() {
        sessionFactory.getCurrentSession().clear();
    }
    
}
