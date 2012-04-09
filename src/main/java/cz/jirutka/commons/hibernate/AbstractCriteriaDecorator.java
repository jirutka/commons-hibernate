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

import java.util.List;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ScrollableResults;
import org.hibernate.ScrollMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;

/**
 * Abstract decorator for Hibernate {@linkplain Criteria} which supports 
 * generic methods chaining.
 * 
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @param <DECORATOR> the concrete decorator class (for method chaining)
 */
public abstract class AbstractCriteriaDecorator <DECORATOR extends Criteria> implements Criteria {
    
    // decorated Criteria
    protected Criteria criteria;

    
    /**
     * @param criteria the <tt>Criteria</tt> being decorated
     */
    public AbstractCriteriaDecorator(Criteria criteria) {
        this.criteria = criteria;
    }
    
    
    
    ///////////////  ABSTRACT METHODS  ///////////////
    
    /**
     * Decorates the given <tt>Criteria</tt> with <tt>this</tt> decorator. This
     * is used for method chaining.
     * 
     * <p>Should be implemented like this:
     * <pre>{@code
     * protected YourDecorator decorate(Criteria criteria) {
     *     this.criteria = criteria;
     *     return this;
     * }
     * }</pre>
     * </p>
     * 
     * @param criteria the Criteria being decorated
     * @return decorated Criteria
     */
    protected abstract DECORATOR decorate(Criteria criteria);
    
    
    
    ///////////////  TEMPLATE METHODS  ///////////////
    
    /**
     * The root entity which is this criteria chain build on.
     * @see AbstractCriteriaDecorator#getRootCriteriaImpl()
     * 
     * @return entity or class name of the root entity
     */
    protected String getRootEntityOrClassName() {
        return getRootCriteriaImpl().getEntityOrClassName();
    }
    
    /**
     * This iterates recursively through parents of the decorated criteria 
     * and returns the root of criteria chain. The decorated criteria has to
     * be an instance of CriteriaImpl, Subcriteria or AbstractCriteriaDecorator
     * to make this work.
     * 
     * @return the root of criteria chain
     */
    protected CriteriaImpl getRootCriteriaImpl() {
        Criteria parent = criteria;
        
        while (parent instanceof AbstractCriteriaDecorator) {
            parent = ((AbstractCriteriaDecorator) parent).getRootCriteriaImpl();
        }
        while (parent instanceof CriteriaImpl.Subcriteria) {
            parent = ((CriteriaImpl.Subcriteria) parent).getParent();
        }
        
        if (! (parent instanceof CriteriaImpl)) {
            throw new RuntimeException("Decorated criteria have to be instance of "
                + "CriteriaImpl, Subcriteria or AbstractCriteriaDecorator");
        }
        return (CriteriaImpl) parent;
    }
    
    
    
    ///////////////  DELEGATED METHODS  ///////////////
    
    @Override
    public DECORATOR add(Criterion criterion) {
        return decorate(criteria.add(criterion));
    }

    @Override
    public DECORATOR addOrder(Order order) {
        return decorate(criteria.addOrder(order));
    }

    @Override
    public DECORATOR createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
        return decorate(criteria.createAlias(associationPath, alias, joinType, withClause));
    }

    @Override
    public DECORATOR createAlias(String associationPath, String alias, int joinType) throws HibernateException {
        return decorate(criteria.createAlias(associationPath, alias, joinType));
    }

    @Override
    public DECORATOR createAlias(String associationPath, String alias) throws HibernateException {
        return decorate(criteria.createAlias(associationPath, alias));
    }

    @Override
    public DECORATOR createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
        return decorate(criteria.createCriteria(associationPath, alias, joinType, withClause));
    }

    @Override
    public DECORATOR createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
        return decorate(criteria.createCriteria(associationPath, alias, joinType));
    }

    @Override
    public DECORATOR createCriteria(String associationPath, String alias) throws HibernateException {
        return decorate(criteria.createCriteria(associationPath, alias));
    }

    @Override
    public DECORATOR createCriteria(String associationPath, int joinType) throws HibernateException {
        return decorate(criteria.createCriteria(associationPath, joinType));
    }

    @Override
    public DECORATOR createCriteria(String associationPath) throws HibernateException {
        return decorate(criteria.createCriteria(associationPath));
    }

    @Override
    public String getAlias() {
        return criteria.getAlias();
    }

    @Override
    public boolean isReadOnly() {
        return criteria.isReadOnly();
    }

    @Override
    public boolean isReadOnlyInitialized() {
        return criteria.isReadOnlyInitialized();
    }

    @Override
    public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
        return criteria.scroll(scrollMode);
    }

    @Override
    public ScrollableResults scroll() throws HibernateException {
        return criteria.scroll();
    }

    @Override
    public DECORATOR setCacheMode(CacheMode cacheMode) {
        return decorate(criteria.setCacheMode(cacheMode));
    }

    @Override
    public DECORATOR setCacheRegion(String cacheRegion) {
        return decorate(criteria.setCacheRegion(cacheRegion));
    }

    @Override
    public DECORATOR setCacheable(boolean cacheable) {
        return decorate(criteria.setCacheable(cacheable));
    }

    @Override
    public DECORATOR setComment(String comment) {
        return decorate(criteria.setComment(comment));
    }

    @Override
    public DECORATOR setFetchMode(String associationPath, FetchMode mode) throws HibernateException {
        return decorate(criteria.setFetchMode(associationPath, mode));
    }

    @Override
    public DECORATOR setFetchSize(int fetchSize) {
        return decorate(criteria.setFetchSize(fetchSize));
    }

    @Override
    public DECORATOR setFirstResult(int firstResult) {
        return decorate(criteria.setFirstResult(firstResult));
    }

    @Override
    public DECORATOR setFlushMode(FlushMode flushMode) {
        return decorate(criteria.setFlushMode(flushMode));
    }
    
    @Override
    public List list() throws HibernateException {
        return criteria.list();
    }
    
    @Override
    public DECORATOR setLockMode(String alias, LockMode lockMode) {
        return decorate(criteria.setLockMode(alias, lockMode));
    }

    @Override
    public DECORATOR setLockMode(LockMode lockMode) {
        return decorate(criteria.setLockMode(lockMode));
    }

    @Override
    public DECORATOR setMaxResults(int maxResults) {
        return decorate(criteria.setMaxResults(maxResults));
    }

    @Override
    public DECORATOR setProjection(Projection projection) {
        return decorate(criteria.setProjection(projection));
    }

    @Override
    public DECORATOR setReadOnly(boolean readOnly) {
        return decorate(criteria.setReadOnly(readOnly));
    }

    @Override
    public DECORATOR setResultTransformer(ResultTransformer resultTransformer) {
        return decorate(criteria.setResultTransformer(resultTransformer));
    }

    @Override
    public DECORATOR setTimeout(int timeout) {
        return decorate(criteria.setTimeout(timeout));
    }
    
    @Override
    public Object uniqueResult() throws HibernateException {
        return criteria.uniqueResult();
    }
    
}
