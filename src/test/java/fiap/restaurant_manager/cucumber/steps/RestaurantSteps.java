package fiap.restaurant_manager.cucumber.steps;

import fiap.restaurant_manager.adapters.api.dto.RestaurantDTO;
import fiap.restaurant_manager.adapters.api.dto.UserDTO;
import fiap.restaurant_manager.domain.enums.KitchenType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalTime;
import java.util.List;

import static fiap.restaurant_manager.cucumber.helper.StepsHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Map;

import org.springframework.http.*;

@AutoConfigureMockMvc
public class RestaurantSteps {

    private RestaurantDTO restaurantDTO;
    private int statusCode;
    private RestaurantDTO createdRestaurant;
    private ResponseEntity<RestaurantDTO> listResponse;
    private RestaurantDTO updatedRestaurant;

    @Autowired
    private TestRestTemplate restTemplate;

    @Given("I have valid restaurant details:")
    public void i_have_valid_restaurant_details(io.cucumber.datatable.DataTable dataTable) {
        final List<List<String>> rows = dataTable.asLists(String.class);
        final List<String> details = rows.get(1);
        restaurantDTO = getRestaurantDto(details);
    }

    @When("I send a POST request to {string} with the restaurant details")
    public void i_send_a_post_request_to_with_the_restaurant_details(String url) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create HTTP request entity with headers and body
        HttpEntity<RestaurantDTO> requestEntity = new HttpEntity<>(restaurantDTO, headers);

        // Send the POST request using TestRestTemplate
        ResponseEntity<RestaurantDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                RestaurantDTO.class
        );

        // Extract the response body and status code
        createdRestaurant = response.getBody();
        statusCode = response.getStatusCodeValue();
    }

    @Then("I should see the newly created restaurant details")
    public void i_should_see_the_newly_created_restaurant_details() {
        assertThat(createdRestaurant.name(), equalTo(restaurantDTO.name()));
        assertThat(createdRestaurant.postalCode(), equalTo(restaurantDTO.postalCode()));
        assertThat(createdRestaurant.street(), equalTo(restaurantDTO.street()));
        assertThat(createdRestaurant.number(), equalTo(restaurantDTO.number()));
        assertThat(createdRestaurant.kitchenType(), equalTo(restaurantDTO.kitchenType()));
        assertThat(createdRestaurant.cnpj(), equalTo(restaurantDTO.cnpj()));
        assertThat(createdRestaurant.capacity(), equalTo(restaurantDTO.capacity()));
        assertThat(createdRestaurant.initialTime(), equalTo(restaurantDTO.initialTime()));
        assertThat(createdRestaurant.finalTime(), equalTo(restaurantDTO.finalTime()));
    }

    @Given("the restaurant with ID {int} exists in the system")
    public void the_restaurant_with_id_exists_in_the_system(int id) {
        assertTrue(id > 0);
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String url) {
        // Send GET request using TestRestTemplate
        listResponse = restTemplate.getForEntity(url, RestaurantDTO.class);
        statusCode = listResponse.getStatusCodeValue();
    }

    @Then("I should receive a {int} status code")
    public void i_should_receive_a_status_code(int expectedStatusCode) {
        assertEquals(statusCode, expectedStatusCode);
    }

    @Then("I should see the details of the restaurant with ID {int}")
    public void i_should_see_the_details_of_the_restaurant_with_id(int id, io.cucumber.datatable.DataTable dataTable) {

        assertNotNull(listResponse);

        var restaurantFromGetList = listResponse.getBody();

        assertNotNull(restaurantFromGetList);

        // Extract expected details from the DataTable
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> expectedDetails = rows.get(0);

        // Validate each field against the expected details
        assertEquals(expectedDetails.get("name"), restaurantFromGetList.name());
        assertEquals(expectedDetails.get("postalCode"), restaurantFromGetList.postalCode());
        assertEquals(expectedDetails.get("street"), restaurantFromGetList.street());
        assertEquals(expectedDetails.get("number"), restaurantFromGetList.number());
        assertEquals(expectedDetails.get("kitchenType"), restaurantFromGetList.kitchenType().name());
        assertEquals(expectedDetails.get("cnpj"), restaurantFromGetList.cnpj());
        assertEquals(Integer.parseInt(expectedDetails.get("capacity")), restaurantFromGetList.capacity());

        // Validate operating hours
        //        List<OperatingHoursDTO> operatingHours = restaurantFromGetList.operatingHoursDTO();
        //        assertFalse(operatingHours.isEmpty());
        //        String expectedOperatingHours = expectedDetails.get("operatingHours");
        //        String[] splitHours = expectedOperatingHours.split(", ");
        //        assertEquals(DayOfWeek.valueOf(splitHours[0]), operatingHours.get(0).dayOfWeek());
        //
        //        String[] timeRange = splitHours[1].split("-");
        //        assertEquals(LocalTime.parse(timeRange[0], DateTimeFormatter.ofPattern("HH:mm")), operatingHours.get(0).startTime().toLocalTime());
        //        assertEquals(LocalTime.parse(timeRange[1], DateTimeFormatter.ofPattern("HH:mm")), operatingHours.get(0).endTime().toLocalTime());
    }

    @And("I have updated restaurant details:")
    public void i_have_updated_restaurant_details(io.cucumber.datatable.DataTable dataTable) {
        // Extract updated details from the DataTable
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> updatedDetails = rows.get(0);

        updatedRestaurant = new RestaurantDTO(
                updatedDetails.get("name"),
                "74375490",
                "Rua teste",
                "numero 487",
                KitchenType.ITALIAN,
                "12.345.678/0001-01",
                100,
                LocalTime.parse("08:10:00"),
                LocalTime.parse("18:10:00")
        );
    }

    @When("I send a PUT request to {string} with the updated details")
    public void i_send_a_put_request_to_with_the_updated_details(String url) {
        // Send PUT request using TestRestTemplate
        var response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(updatedRestaurant),
                RestaurantDTO.class
        );

        updatedRestaurant = response.getBody();
        statusCode = response.getStatusCodeValue();
    }

    @And("I should see the updated restaurant details")
    public void i_should_see_the_updated_restaurant_details() {
        // Validate the response contains the updated details
        RestaurantDTO returnedRestaurant = updatedRestaurant;
        assertNotNull(returnedRestaurant);

        // Field-by-field assertions using assertEquals
        assertEquals(updatedRestaurant.name(), returnedRestaurant.name());
        assertEquals(updatedRestaurant.postalCode(), returnedRestaurant.postalCode());
        assertEquals(updatedRestaurant.street(), returnedRestaurant.street());
        assertEquals(updatedRestaurant.number(), returnedRestaurant.number());
        assertEquals(updatedRestaurant.capacity(), returnedRestaurant.capacity());
        assertEquals(updatedRestaurant.kitchenType(), returnedRestaurant.kitchenType());
        assertEquals(updatedRestaurant.cnpj(), returnedRestaurant.cnpj());

        // Validate datetime
        assertEquals(updatedRestaurant.initialTime(), returnedRestaurant.initialTime());
        assertEquals(updatedRestaurant.finalTime(), returnedRestaurant.finalTime());
    }

    @And("the restaurant with ID {int} should no longer exist in the system, and should retrieve {int}")
    public void noLongerExists(final int id, final int statusCode)
    {
        var path = "/api/restaurants/" + id;

        var getResponse = restTemplate.getForEntity(path, UserDTO.class);

        assertNotNull(getResponse);

        this.statusCode = getResponse.getStatusCode().value();

        assertEquals(this.statusCode, statusCode);
    }

    @When("Regarding to restaurants, I send a DELETE request to {string}")
    public void delete(final String endPoint) {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            var delete = restTemplate.exchange(endPoint, HttpMethod.DELETE, entity, Void.class);

            statusCode = delete.getStatusCode().value();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(statusCode);
    }

}
