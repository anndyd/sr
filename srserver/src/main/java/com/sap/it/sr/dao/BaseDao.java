package com.sap.it.sr.dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BaseDao<T> {
	private static final Logger LOGGER = Logger.getLogger(BaseDao.class);
	
    @Autowired
    @PersistenceContext(unitName="srserver")
    protected EntityManager em;

    @Autowired
    @PersistenceContext(unitName="grserver")
    protected EntityManager grem;
    
    protected Class<T> clazz;

    @SuppressWarnings("unchecked")
    public BaseDao() {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T create(T t) {
        em.persist(t);
        em.flush();
        return t;
    }

    public void remove(T t) {
        em.remove(t);
        em.flush();
    }

    public void remove(Long id) {
        T t = em.find(clazz, id);
        em.remove(t);
        em.flush();
    }

    public T merge(T t) {
        try {
			return em.merge(t);
		} catch (Exception e) {
			LOGGER.warn("Merge data failed.");
			e.printStackTrace();
			return null;
		}
    }

    /**
     * returns null if the entity not found
     */
    public T load(Long id) {
        return em.find(clazz, id);
    }

    /**
     * throw exception if the entity not found
     * @throws Exception 
     */
    public T get(Long id) throws Exception {
        T t = load(id);
        if (t == null) {
            throw new Exception(String.format("Can't get entity: {}", id));
        }
        return t;
    }

    public List<T> load(List<Long> ids) {
        List<T> ts = new ArrayList<T>();
        for (Long id : ids) {
            T t = load(id);
            if (t != null) {
                ts.add(t);
            }
        }
        return ts;
    }

    /**
     * throw exception if any one of the entities not found
     * @throws Exception 
     */
    public List<T> get(List<Long> ids) throws Exception {
        List<T> ts = new ArrayList<T>();
        for (Long id : ids) {
            T t = get(id);
            ts.add(t);
        }
        return ts;
    }

    public List<T> findAll() {
        return em.createQuery("select t from " + clazz.getSimpleName() + " t", clazz).getResultList();
    }
    
    public Query createQuery(String qlString) {
        return em.createQuery(qlString);
    }

    /**
     * @param orderBy
     *            a simple order by clause, like "id, status desc"
     * @return list of entities
     */
    public List<T> findAll(String orderBy) {
        StringBuilder hql = new StringBuilder("select t from " + clazz.getSimpleName() + " t");

        if (StringUtils.isNotEmpty(orderBy)) {
            String[] expressions = orderBy.split(",");
            for (int i = 0; i < expressions.length; i++) {
                String expression = StringUtils.trim(expressions[i]);
                if (StringUtils.isEmpty(expression)) {
                    continue;
                }

                if (i == 0) {
                    hql.append(" order by ");
                } else if (i > 0) {
                    hql.append(", ");
                }
                hql.append("t.").append(expression);
            }
        }

        return em.createQuery(hql.toString(), clazz).getResultList();
    }
}
