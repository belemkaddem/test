package net.digimart.parmaGeo.ParmaGeo;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.web.client.RestClientException; 
import org.springframework.web.client.RestTemplate; 
 
import java.io.UnsupportedEncodingException; 
import java.util.List; 
import java.util.Map; 
 
import static java.net.URLEncoder.encode; 
 
/**
 * A location service that using Google geocoding API return the location coordinates. 
 * 
 * @author Carlo Micieli 
 */ 
@Service("locationService") 
public class LocationService { 
 
    public static final String STATUS_OK = "OK"; 
    public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS"; 
    public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT"; 
    public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED"; 
    public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST"; 
 
    // google maps api endpoint 
    public static final String GOOGLE_MAPS_API_ENDPOINT = "http://maps.googleapis.com/maps/api/geocode/json?address={address}&sensor=false"; 
 
    private RestTemplate restTemplate; 
 
    @Autowired 
    public LocationService(RestTemplate restTemplate) { 
        this.restTemplate = restTemplate; 
    } 
 
    public LocationService() { 
    }
    
    private String buildMessage(String status) { 
        if (status == STATUS_ZERO_RESULTS) 
            return "No result is found"; 
        else if (status == STATUS_OVER_QUERY_LIMIT) 
            return "You are over your quota"; 
        else if (status == STATUS_REQUEST_DENIED) 
            return "Your request was denied"; 
        else if (status == STATUS_INVALID_REQUEST) 
            return "The query is missing"; 
 
        return ""; 
    } 
 
    /**
     * Perform a geocode request to find the coordinates for a valid address. 
     * 
     * @param address the address that you want to geocode. 
     * @return the coordinates. 
     * @throws RestClientException 
     * @throws UnsupportedEncodingException 
     */ 
    public double[] findLocation(String address) throws RestClientException, UnsupportedEncodingException { 
        Map<?, ?> obj = restTemplate.getForObject(GOOGLE_MAPS_API_ENDPOINT, Map.class, encode(address, "UTF-8")); 
 
        // check the response status 
        String status = (String) obj.get("status"); 
        if (!status.equals(STATUS_OK)) { 
            throw new RuntimeException(buildMessage(status)); 
        } 
 
        List<?> results = (List<?>) obj.get("results"); 
        Map<?, ?> result = (Map<?, ?>) results.get(0); 
        Map<?, ?> geometry = (Map<?, ?>) result.get("geometry"); 
        Map<?, ?> location = (Map<?, ?>) geometry.get("location"); 
 
        return new double[]{ 
                (Double) location.get("lat"), 
                (Double) location.get("lng") 
        }; 
    } 
}