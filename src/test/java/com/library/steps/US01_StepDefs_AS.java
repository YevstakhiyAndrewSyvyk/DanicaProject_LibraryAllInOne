package com.library.steps;

import com.library.utility.ConfigurationReader;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class US01_StepDefs_AS {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;

    @Given("I logged Library api as a {string} AS")
    public void i_logged_library_api_as_a(String userType) {

        givenPart = given().log().uri()
                .header("x-library-token", LibraryAPI_Util.getToken(userType));
    }
    @Given("Accept header is {string} AS")
    public void accept_header_is(String contentType) {
        givenPart.accept(contentType);
    }
    @When("I send GET request to {string} endpoint AS")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = givenPart.when().get(ConfigurationReader.getProperty("library.baseUri") + endpoint).prettyPeek();
        thenPart = response.then();
    }
    @Then("status code should be {int} AS")
    public void status_code_should_be(Integer statusCode) {
        thenPart.statusCode(statusCode);
    }
    @Then("Response Content type is {string} AS")
    public void response_content_type_is(String contentType) {
        thenPart.contentType(contentType);
    }
    @Then("{string} field should not be null AS")
    public void field_should_not_be_null(String path) {
        thenPart.body(path, Matchers.is(Matchers.notNullValue()));
    }

}
