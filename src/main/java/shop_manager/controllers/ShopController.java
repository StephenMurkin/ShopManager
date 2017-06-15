package shop_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import shop_manager.entities.Shop;
import shop_manager.repos.ShopRepository;
import shop_manager.services.CoordinatesService;

import java.util.*;

/**
 * Created by steve on 14/06/2017.
 *
 * Defines custom handler methods in addition to the vanilla out of the box ones provided by Spring
 *
 */

@RepositoryRestController
@ExposesResourceFor(Shop.class)
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CoordinatesService coordinatesService;

    @Autowired
    private ShopResourceAssembler shopResourceAssembler;

    @Transactional
    @RequestMapping(path="/shops", method = RequestMethod.POST, produces = "application/hal+json;charset=UTF-8")
    public ResponseEntity<?> post(@RequestBody Shop shop) throws Exception {

        Optional<Shop> existing = shopRepository.findByName(shop.getName());

        if (!existing.isPresent()) { // Retrieve the coordinates from Google and POST as normal
            shop = coordinatesService.getCoordinates(shop);
            Shop saved = shopRepository.save(shop);
            return new ResponseEntity<>(shopResourceAssembler.toResource(saved), HttpStatus.CREATED);
        }
        else { // Perform a PUT on the existing entity and return the current and previous version in the response body

            // Store previous values in new shop object
            Shop previous = new Shop();
            Shop.Address address = new Shop.Address();
            previous.setName(existing.get().getName());
            address.setNumber(existing.get().getAddress().getNumber());
            address.setPostCode(existing.get().getAddress().getPostCode());
            address.setLatitude(existing.get().getAddress().getLatitude());
            address.setLongitude(existing.get().getAddress().getLongitude());
            previous.setAddress(address);

            // Overwrite persisted shop with new values
            shop.setId(existing.get().getId());
            Shop current = shopRepository.save(shop);

            // Place current and previous shop objects in map for returning in JSON response body
            Map<String,Shop> shops = new HashMap<>();
            shops.put("current",current);
            shops.put("previous",previous);
            return new ResponseEntity<>(shopResourceAssembler.toResource(shops,current), HttpStatus.OK);
        }
    }

    @RequestMapping(path="/shops",method = RequestMethod.GET, produces = "application/hal+json;charset=UTF-8")
    public ResponseEntity<?> get(@RequestParam(name = "lat", required = false) Double latitude,
                                 @RequestParam(name = "lng", required = false) Double longitude)
                                 throws Exception {

        if(latitude == null && longitude == null) { // Perform a standard GET all
            return new ResponseEntity<>(shopResourceAssembler.toResources(shopRepository.findAll()),HttpStatus.OK);
        }
        else { // Find and return the nearest shop
            Shop nearest = coordinatesService.getNearest(latitude,longitude);
            return new ResponseEntity<>(shopResourceAssembler.toResource(nearest),HttpStatus.OK);
        }
    }

}
