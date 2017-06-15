package shop_manager;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import shop_manager.entities.Shop;
import shop_manager.repos.ShopRepository;
import shop_manager.services.CoordinatesService;

import java.io.IOException;

/**
 * Created by steve on 14/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CoordinatesServiceTest {

    @Autowired
    private CoordinatesService coordinatesService;

    @Autowired
    private ShopRepository shopRepository;

    @Test
    public void test_get_coordinates() throws Exception {

        Shop shop = new Shop();
        shop.setName("test");
        Shop.Address address = new Shop.Address();
        address.setNumber(1);
        address.setPostCode("E3 2QS");
        shop.setAddress(address);
        coordinatesService.getCoordinates(shop);
        Assert.assertNotNull(shop.getAddress().getLatitude());
        Assert.assertNotNull(shop.getAddress().getLongitude());

    }

    @Test // Save shops in Farnborough (Hampshire) and Bath, use London as the current location, expect Farnborough shop to be nearest
    public void test_get_nearest() throws Exception {

        Shop farnborough = new Shop();
        farnborough.setName("Farnborough");
        Shop.Address fAddress = new Shop.Address();
        fAddress.setLongitude(-0.7874301);
        fAddress.setLatitude(51.2900157);
        farnborough.setAddress(fAddress);
        shopRepository.save(farnborough);
        Shop bath = new Shop();
        Shop.Address bAddress = new Shop.Address();
        bath.setName("Bath");
        bAddress.setLongitude(-2.3724416);
        bAddress.setLatitude(51.3836861);
        bath.setAddress(bAddress);
        shopRepository.save(bath);

        Shop nearest = coordinatesService.getNearest(51.5297,0.0218);
        Assert.assertTrue(nearest.getName().equals(farnborough.getName()));

    }

}
