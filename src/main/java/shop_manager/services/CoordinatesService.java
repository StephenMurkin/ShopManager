package shop_manager.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import shop_manager.entities.Shop;
import shop_manager.repos.ShopRepository;

import java.util.List;

/**
 * Created by steve on 14/06/2017.
 *
 * Service for retrieving location data from Google Maps APIs and performing calculations use this data
 *
 */

@Component
public class CoordinatesService {

    @Autowired
    private ShopRepository shopRepository;

    // Use Google API to find the coordinates of a shop, given its postcode
    public Shop getCoordinates(Shop shop) throws Exception {

        String geocodeApiUri = "https://maps.googleapis.com/maps/api/geocode/json";
        String geocodeApiKey = "AIzaSyA_YE-HytnRdbzGpAv4sS1Joa5CHimCetA";

        // Perform GET to Google's Geocode API
        String uri = geocodeApiUri + "?address=" + shop.getAddress().getPostCode() + "&key=" + geocodeApiKey;
        JSONObject json = queryAPI(uri);
        String status = json.getString("status");

        if (status.equals("OK")) { // Traverse response JSON to find latitude and longitude
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            shop.getAddress().setLatitude(location.getDouble("lat"));
            shop.getAddress().setLongitude(location.getDouble("lng"));
            return shop;
        }
        else throw new Exception("Google Geocode API responded with error status " + status + ". Please refer to their documentation at https://developers.google.com/maps/documentation/geocoding/intro" );

    }

    // Use Google API to find nearest shop to the user
    public Shop getNearest(Double latitude, Double longitude) throws Exception {
        List<Shop> shops = shopRepository.findAll();
        Double minimumDistance = Double.MAX_VALUE;
        Shop nearest = new Shop();
        // Loop through all saved shops and keep track of the nearest so far
        for(Shop shop: shops) {
            Double distance = getDistance(latitude,longitude,shop.getAddress().getLatitude(),shop.getAddress().getLongitude());
            if (distance < minimumDistance) {
                minimumDistance = distance;
                nearest = shop;
            }
        }
        if (minimumDistance < Double.MAX_VALUE) {
            return nearest;
        }
        else {
            throw new Exception("No shops available");
        }

    }

    // Use Google API to find distance between two pairs of coordinates
    private Double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) throws Exception {

        String distanceMatrixApiUri = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String distanceMatrixApiKey = "AIzaSyBdTjCiNzMbMhV3saD6NsoYABJS6d9sZxY";

        // Perform GET to Google's Distance Matrix API
        String uri = distanceMatrixApiUri+ "?units=metric&origins=" + lat1 + "," + lng1 + "&destinations=" + lat2 + "," + lng2 + "&key=" + distanceMatrixApiKey;
        JSONObject json = queryAPI(uri);
        String status = json.getString("status");

        if (status.equals("OK")) { // Traverse response JSON to find distance in km
            return json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getDouble("value");
        }
        else throw new Exception("Google Distance Matrix API responded with error status " + status + ". Please refer to their documentation at https://developers.google.com/maps/documentation/distance-matrix/intro");

    }

    // Performs GET to a URI using a RestTemplate
    private JSONObject queryAPI (String uri) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
        return new JSONObject(response);
    }

}
