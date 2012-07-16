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

import cz.jirutka.commons.persistence.Persistable;
import java.io.Serializable;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;

/**
 * Various helper utilities for Hibernate.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @version 2012-06-10
 * @since 1.1
 */
public class HibernateUtils {
    
    /**
     * Return identifier of the given instance, without actually initializing it
     * in case of {@linkplain HibernateProxy proxy object}.
     * 
     * @param entity persistent instance or its proxy object
     * @return identifier
     */
    public static Serializable proxySafeGetIdentifier(Persistable entity) {
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
        } else {
            return entity.getId();
        }
    }
    
    /**
     * Return entity class of the given instance even when 
     * {@linkplain HibernateProxy proxy object}.
     *
     * @param entity persistent instance or its proxy object
     * @return entity class
     */
    public static Class<?> proxySafeGetEntityClass(Object entity) {
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer().getPersistentClass();
        } else {
            return entity.getClass();
        }
    }
    
    /**
     * Return names of properties holding NaturalId of the given entity's 
     * metadata.
     * 
     * @param metadata entity class metadata
     * @return names of properties holding NaturalId
     */
    public static String[] getNaturalIdentifierNames(ClassMetadata metadata) {
        if (! metadata.hasNaturalIdentifier()) {
            return new String[0];
        }
        
        int[] naturalProps = metadata.getNaturalIdentifierProperties();
        String[] propNames = metadata.getPropertyNames();
        String[] naturalNames = new String[naturalProps.length];
        
        for (int i = 0; i < naturalProps.length; i++) {
            naturalNames[i] = propNames[naturalProps[i]];
        }
        return naturalNames;
    }

    /**
     * If the given object is a {@linkplain HibernateProxy proxy}, then return
     * the underlying persistent object (initializing if necessary). Otherwise
     * just return the given object.
     *
     * @param entity persistent instance or its proxy object
     * @return persistent instance
     */
    public static <E> E deproxy(E entity) {
        if (entity instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) entity;
            return (E) proxy.getHibernateLazyInitializer().getImplementation();
        } else {
            return entity;
        }
    }
    
}
