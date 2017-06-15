package shop_manager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import shop_manager.entities.Shop;
import shop_manager.repos.ShopRepository;
import shop_manager.services.CoordinatesService;

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
        address.setPostCode("E14 5GH");
        shop.setAddress(address);
        coordinatesService.getCoordinates(shop);
        Assert.assertNotNull(shop.getAddress().getLatitude());
        Assert.assertNotNull(shop.getAddress().getLongitude());

    }

    @Test // Save shops in Cardiff and Edinburgh, use London as the current location, expect Cardiff shop to be nearest
    public void test_get_nearest() throws Exception {

        Shop shop1 = constructShop("Cardiff",51.4782085,-3.1848281);
        shopRepository.save(shop1);
        Shop shop2 = constructShop("Edinburgh",55.940304,-3.241316);
        shopRepository.save(shop2);
        Shop nearest = coordinatesService.getNearest(51.4558185,-0.3434558);
        Assert.assertTrue(nearest.getName().equals(shop1.getName()));

    }

    private Shop constructShop(String name, Double latitude, Double longitude) {
        Shop shop = new Shop();
        shop.setName(name);
        Shop.Address address = new Shop.Address();
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        shop.setAddress(address);
        return shop;
    }

}
