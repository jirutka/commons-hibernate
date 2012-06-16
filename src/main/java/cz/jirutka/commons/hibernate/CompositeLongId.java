package cz.jirutka.commons.hibernate;

import java.io.Serializable;
import javax.persistence.Embeddable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Embeddable class for composite identifier of two Long types.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Embeddable
public class CompositeLongId implements Serializable {
    
    private Long firstId;
    private Long secondId;

    
    /**
     * Default non-parametric constructor.
     */
    protected CompositeLongId() {
    }
    /**
     * Create a new composite identifier.
     * 
     * @param firstId 
     * @param secondId 
     */
    public CompositeLongId(Long firstId, Long secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }
    
    
    @Override
    public String toString() {
        return "(" + firstId + "," + secondId + ")";
    }        

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        CompositeLongId other = (CompositeLongId) obj;
        return new EqualsBuilder()
                .append(firstId, other.firstId)
                .append(secondId, other.secondId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 83)
                .append(firstId)
                .append(secondId)
                .toHashCode();
    }
    
}
