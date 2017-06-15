package shop_manager.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import shop_manager.entities.Shop;

/**
 * Created by steve on 14/06/2017.
 */

@RepositoryRestResource(collectionResourceRel = "shops", path = "shops")
public interface ShopRepository extends PagingAndSortingRepository<Shop, Long>{

    Optional<Shop> findByName(@Param("name") String name);

    List<Shop> findAll();

}
