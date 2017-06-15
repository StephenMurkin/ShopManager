/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shop_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import shop_manager.entities.Shop;
import shop_manager.repos.ShopRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ShopControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShopRepository shopRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private final String baseURL = "/shops";

    @Before
    public void setUp() {
        shopRepository.deleteAll();
        this.mockMvc= webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test_get_one() throws Exception {

        Shop shop = constructShop();
        Shop saved = shopRepository.save(shop);

        Assert.assertTrue(shopRepository.count()==1);

        this.mockMvc.perform(get(baseURL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(shop.getName()));

    }

    @Test
    public void test_get_all() throws Exception {

        Shop shop1 = constructShop();
        shopRepository.save(shop1);
        Shop shop2 = constructShop();
        shop2.setName("Asda");
        shopRepository.save(shop2);

        Assert.assertTrue(shopRepository.count()==2);

        this.mockMvc.perform(get(baseURL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.shops[1].name").value(shop2.getName()));

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
        bath.setName("Bath");
        Shop.Address bAddress = new Shop.Address();
        bAddress.setLongitude(-2.3724416);
        bAddress.setLatitude(51.3836861);
        bath.setAddress(bAddress);
        double currentLatitude = 51.5297;
        double currentLongitude = 0.0218;
        this.mockMvc.perform(get(baseURL + "?lat=" + currentLatitude +"&lng=" + currentLongitude))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(farnborough.getName()));

    }

    @Test
    public void test_post() throws Exception {

        Shop shop = constructShop();
        String json = objectMapper.writeValueAsString(shop);

        this.mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(shop.getName()))
                .andExpect(jsonPath("$.address.latitude").isNotEmpty())
                .andExpect(jsonPath("$.address.longitude").isNotEmpty());

        Assert.assertTrue(shopRepository.count()==1);

    }

//    @Ignore // TODO - don't understand why this test doesn't pass - expected behaviour observed through Postman
    @Test
    public void test_put() throws Exception {

        Shop original = constructShop();
        shopRepository.save(original);
        Shop updated = constructShop();
        updated.getAddress().setNumber(2);
        String json = objectMapper.writeValueAsString(updated);

        this.mockMvc.perform(put(baseURL + "/" + original.getId())
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressNumber").value(2));

        Assert.assertTrue(shopRepository.count()==1);

    }

    @Test
    public void test_delete() throws Exception {

        Shop shop = constructShop();
        Shop saved = shopRepository.save(shop);

        Assert.assertTrue(shopRepository.count()==1);

        this.mockMvc.perform(delete(baseURL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assert.assertTrue(shopRepository.count()==0);

    }

    @Test // Save one shop and then POST a shop of the same name, expect to get back an updated shop and the previous version
    public void test_post_existing_name() throws Exception {

        Shop original = constructShop();
        shopRepository.save(original);
        Shop updated = constructShop();
        updated.getAddress().setNumber(2);
        String json = objectMapper.writeValueAsString(updated);

        this.mockMvc.perform(post(baseURL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.current.address.number").value(2))
                .andExpect(jsonPath("$.content.previous.address.number").value(1));

        Assert.assertTrue(shopRepository.count()==1);

    }

    private Shop constructShop() {
        Shop shop = new Shop();
        shop.setName("Tesco");
        Shop.Address address = new Shop.Address();
        address.setNumber(1);
        address.setPostCode("E3 2QS");
        shop.setAddress(address);
        return shop;
    }

}
