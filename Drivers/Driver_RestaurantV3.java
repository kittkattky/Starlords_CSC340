package Drivers;

import models.RestaurantModel;

/**
 * Basic test driver to show that methods work, and information flow is correct.
 *
 * @author Diego Rodriguez
 */
public class Driver_RestaurantV3 {
    public static void main(String[] args) throws Exception {
        double lat = 36.066984;
        double lon = -79.800178;
        int cuisineID = 1;
        
        
       
        RestaurantModel modelA = new RestaurantModel();
        RestaurantModel modelB = new RestaurantModel();

        //modelA = modelA.loadCuisinesByLocation();
        System.out.println(modelA.getCuisineMap());

        //modelB = modelA.loadRestaurantsByID(1);
        System.out.println(modelB.getRestaurantNameMap());
        System.out.println(modelB.getRestaurantUrlMap());
        System.out.println(modelB.getRestaurantAddressMap());
        System.out.println(modelB.getRestaurantRatingMap());

    }

}