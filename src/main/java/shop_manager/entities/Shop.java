package shop_manager.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by steve on 14/06/2017.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
public class Shop extends AbstractBaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Embedded
    private Address address;

    public Shop() {}

    @Data
    @Embeddable
    public static class Address implements Serializable {

        private int number;
        private String postCode;
        private double latitude;
        private double longitude;

        public Address() {}

    }

}
