package shop_manager.entities;

import org.springframework.data.domain.Persistable;
import org.springframework.hateoas.Identifiable;

/**
 * Created by steve on 15/06/2017.
 *
 * Interface for any persistable entity with any ID generation method
 *
 */

public interface IBaseEntity extends Identifiable<Long>, Persistable<Long> {

    void setId(Long id);

}
