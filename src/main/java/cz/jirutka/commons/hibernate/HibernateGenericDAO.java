/*
 * Copyright (c) 2012 Jakub Jirutka <jakub@jirutka.cz>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the  GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.jirutka.commons.hibernate;

import cz.jirutka.commons.hibernate.criteria.ExtendedCriteria;
import cz.jirutka.commons.persistence.Persistable;
import cz.jirutka.commons.persistence.dao.GenericDAO;
import cz.jirutka.commons.persistence.dao.PagingOrdering;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.persistence.NoResultException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

/**
 * Hibernate implementation of the {@link GenericDAO} interface.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @version 2012-07-09
 * @since 1.1
 */
public class HibernateGenericDAO implements GenericDAO {

    public static final String CACHE_REGION_NATURAL_ID = "org.hibernate.cache.region.NaturalIdentifiersCache";

    protected final SessionFactory sessionFactory;
    
    
    /**
     * Create a new instance of the Hibernate GenericDAO.
     * 
     * @param sessionFactory Hibernate Session Factory
     */
    public HibernateGenericDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    
    @Override
    public Long count(Class<? extends Persistable> clazz) {
        return (Long) session().createCriteria(clazz)
                .setProjection(Projections.rowCount())
                .list().get(0);
    }

    @Override
    public void delete(Persistable entity) {
        session().delete(entity);
    }

    @Override
    public void delete(Serializable id, Class<? extends Persistable> clazz) throws NoResultException {
        String className = clazz.getSimpleName();

        int deleted = session().createQuery(
                    "DELETE "+ className +" AS e " +
                    "WHERE e.id = :id")
                .setParameter("id", id)
                .executeUpdate();

        if (deleted == 0) {
            throw new NoResultException(String.format("No such entity %s with id = %s", className, id));
        }
    }

    @Override
    public <E extends Persistable>
            E findByPrimaryKey(Serializable id, Class<E> clazz) throws NoResultException {

        E entity = (E) session().get(clazz, id);

        if (entity == null) {
            throw new NoResultException(String.format("No such entity %s with id = %s", clazz.getSimpleName(), id));
        }
        return entity;
    }

    @Override
    public <E extends Persistable>
            List<E> getAll(Class<E> clazz) {

        return session().createCriteria(clazz).list();
    }

    @Override
    public <E extends Persistable>
            List<E> findByExample(E exampleInstance, String[] includeProperties, PagingOrdering paging, Class<E> clazz) {
        
        final List<String> properties = Arrays.asList(includeProperties);
        PropertySelector selector = new Example.PropertySelector() {
                public boolean include(Object propertyValue, String propertyName, Type type) {
                    return properties.contains(propertyName);
                }
        };
        return createCriteria(clazz)
                .add(Example.create(exampleInstance)
                        .setPropertySelector(selector))
                .setPagingOrdering(paging)
                .list();
    }

    @Override
    public <E extends Persistable> 
            E findByNaturalKey(Object key, Class<E> clazz) throws NoResultException {
        
        String className = clazz.getSimpleName();
        ClassMetadata metadata = sessionFactory.getClassMetadata(clazz);
        String[] names = HibernateUtils.getNaturalIdentifierNames(metadata);
        
        if (names.length == 0) {
            throw new IllegalArgumentException(String.format(
                    "Entity class %s does not contain NaturalId", className));
        }
        if (names.length > 1) {
            throw new IllegalArgumentException(String.format(
                    "Entity class %s has more than one property with @NaturalId annotation", className));
        }
        
        return findByNaturalKey(names[0], key, clazz);
    }

    public <E extends Persistable>
            E findByNaturalKey(String property, Object value, Class<E> clazz) throws NoResultException {

        E entity = (E) createCriteria(clazz)
                .add(Restrictions.naturalId()
                    .set(property, value))
                .setCacheable(true)
                .setCacheRegion(CACHE_REGION_NATURAL_ID)
                .uniqueResult();

        if (entity == null) {
            throw new NoResultException(String.format(
                    "No such entity %s with NaturalId %s = %s", clazz.getSimpleName(), property, value));
        }
        return entity;
    }

    @Override
    public <E extends Persistable>
            List<E> findByProperty(String property, Object value, PagingOrdering paging, Class<E> clazz) {

        return createCriteria(clazz)
                .add(Restrictions.eq(property, value))
                .setPagingOrdering(paging)
                .list();
    }
    
    @Override
    public <E extends Persistable>
            List<E> getPaginated(PagingOrdering paging, Class<E> clazz) {
        
        return createCriteria(clazz)
                .setPagingOrdering(paging)
                .list();
    }

    @Override
    public boolean isPersistent(Serializable id, Class<? extends Persistable> clazz) {
        Long result = (Long) session().createCriteria(clazz)
                .add(Restrictions.idEq(id))
                .setProjection(Projections.rowCount())
                .uniqueResult();
        return (result == 1) ? true : false;
    }

    @Override
    public <E extends Persistable>
            E load(Serializable id, Class<E> clazz) {

        return (E) session().load(clazz, id);
    }

    @Override
    public Serializable save(Persistable entity) {
        return session().save(entity);
    }

    @Override
    public void saveOrUpdate(Persistable entity) {
        session().saveOrUpdate(entity);
    }

    @Override
    public void update(Persistable entity) {
        session().update(entity);
    }


    /**
     * Create a new <tt>Criteria</tt> for the given entity class and decorate
     * it with the {@linkplain VisitableCriteria}.
     * 
     * @see Session#createCriteria(java.lang.Class) 
     * 
     * @param persistentClass an entity class
     * @return the decorated criteria instance
     */
    protected ExtendedCriteria createCriteria(Class<? extends Persistable> persistentClass) {
        
        Criteria criteria = session().createCriteria(persistentClass);
        return new ExtendedCriteria(criteria);
    }
    
    /**
     * Return the current Hibernate Session for this thread.
     * 
     * @return the Hibernate Session
     */
    protected Session session() {
        return sessionFactory.getCurrentSession();
    }
    
}
