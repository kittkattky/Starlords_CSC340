package views;

/**
 * View that interacts with CuisineListUI.FXML and Restaurant Controller
 *
 * @author Diego Rodriguez Last Updated: 4/4/2020
 */
import controllers.RestaurantController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CuisineView implements Initializable {

    @FXML
    protected Label errorLabel;

    @FXML
    protected ListView<String> listViewCuisineList;

    protected boolean isSearched = false;
    protected boolean isSelected = false;
    private RestaurantController restaurantController = new RestaurantController();
    protected RestaurantListView instanceToSwitchScene = new RestaurantListView();

    protected Map cuisineMap;
    protected Map cuisineIDMap;

    /**
     * When the search button is clicked, this method handles the event.
     *
     * @param event
     */
    @FXML
    private void handleSearchButtonAction(ActionEvent event) {
        this.isSearched = true;
        addCuisinesToList();
    }

    /**
     * This method is called when the user clicks the "Search for Cuisines"
     * button. A call to the api is made through the restaurant model, and the
     * information from the api is returned as a map. Each element from the map
     * is cycled through and added to the listView.
     */
    public void addCuisinesToList() {
        this.cuisineMap = restaurantController.getCuisineMap();
        for (int i = 0; i < this.cuisineMap.size(); i++) {
            this.listViewCuisineList.getItems().add((String) this.cuisineMap.get(i));
        }
    }

    /**
     * This method is called when the user clicks the confirm button.
     * Checks are run to ensure the correct sequence of user inputs is achieved.
     * The cuisine ID that corresponds to the cuisine selected by the user is saved.
     * Then that cuisine ID is used to pre load a information on the next scene.
     * 
     * @param _event
     * @param event
     * @throws java.io.IOException
     */
    @FXML
    public void selectCuisine(ActionEvent _event) throws IOException {
        if (!isSearched) {
            errorLabel.setText("Please search for cuisines near you");
        } else {
            if (!isSelected) {
                errorLabel.setText("Please select a cuisine");
            } else {
                String cuisineSelected = listViewCuisineList.getSelectionModel().getSelectedItem();
                this.restaurantController.setCuisineID((Integer) this.restaurantController.getCuisineIDMap().get(cuisineSelected));

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader ().getResource("fxml/RestaurantList.fxml"));
                //Create parent object based off of loader that knows which fxml file to create a scene of.
                Parent parentUsingFXML = loader.load();

                /**
                 * These next 3 lines are pre loading the restaurant list onto the next scene.
                 * We can create an instance of the view that corresponds with the scene we are switching to by using the loader
                 * From there we can alter items on the next scene before its shown by calling methods from the view instance.
                 * This allows us to use information from this scene to set the next scene, even though the views are different.
                 */
                RestaurantListView view = loader.getController();
                view.addRestaurantsToList(this.restaurantController.getCuisineID());
                view.setCuisineLabel(cuisineSelected);
                
                //create scene using parent
                Scene sceneToSwitchTo = new Scene(parentUsingFXML);

                //create a refernce to the stage that the event is coming from.
                Stage referenceStage = (Stage)((Node) _event.getSource()).getScene().getWindow();

                //set the scene onto our referenced stage and show it.
                referenceStage.setScene(sceneToSwitchTo);
                referenceStage.show();
            }
        }
    }

    /**
     * When an area of list view is clicked, meaning the user selects a cuisine,
     * this method sets isSelected to true.
     */
    public void isSelected() {
        if (!isSearched) {
            errorLabel.setText("Please search for cuisines near you");
        }
        if (isSearched) {
            this.isSelected = true;
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}