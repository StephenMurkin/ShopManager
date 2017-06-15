package shop_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import shop_manager.entities.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 15/06/2017.
 */

@Component
public class ShopResourceAssembler implements ResourceAssembler<Shop,Resource<Shop>> {

    @Autowired
    private EntityLinks entityLinks;

    @Override
    public Resource<Shop> toResource(Shop shop) {
        Resource<Shop> resource = new Resource<>(shop);
        resource.add(entityLinks.linkToSingleResource(shop));
        return resource;
    }

    public Resource<Map<String,Shop>> toResource(Map<String,Shop> map, Shop shop) {
        Resource<Map<String,Shop>> resource = new Resource<>(map);
        resource.add(entityLinks.linkToSingleResource(shop));
        return resource;
    }

    public Resources toResources(List<Shop> shops) {
        List<Resource> result = new ArrayList<>();
        for (Shop shop: shops) {
            result.add(toResource(shop));
        }
        return new Resources<>(result);
    }
}
