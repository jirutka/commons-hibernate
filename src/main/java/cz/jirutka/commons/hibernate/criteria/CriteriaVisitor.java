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
package cz.jirutka.commons.hibernate.criteria;

import org.hibernate.impl.CriteriaImpl;

/**
 * Interface of the {@linkplain Criteria} visitor.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @version 2012-06-14
 * @since 1.1
 */
public interface CriteriaVisitor {
    
    /**
     * Visit the given criteria (the root of criteria chain) and do whatever you
     * want with it...
     * 
     * @param rootCriteria a root criteria instance
     */
    void visit(CriteriaImpl rootCriteria);

}
