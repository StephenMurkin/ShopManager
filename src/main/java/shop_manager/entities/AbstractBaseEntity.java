package shop_manager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by steve on 15/06/2017.
 *
 * Base class for a persistable entity with an auto-generated ID
 *
 */

@MappedSuperclass
@Data
public abstract class AbstractBaseEntity implements IBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //  For optimistically locking entity - TODO - not sure how to test this
    @Version
    private Long version = 0L;

    protected AbstractBaseEntity() {}

    @JsonIgnore
    @Transient
    public boolean isNew() { return null == getId(); }

}
