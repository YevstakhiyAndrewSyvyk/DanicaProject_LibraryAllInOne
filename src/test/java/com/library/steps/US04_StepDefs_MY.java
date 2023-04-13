package com.library.steps;

import com.library.pages.DashboardPage_MY;
import com.library.pages.LoginPage;
import com.library.utility.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class US04_StepDefs_MY {

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;
    String userId;
    String token;

    Map<String, Object> userMap_API;

    Map<String, Object> bookMap;


    /*

    Library Authorization:
       - send "x-library-token" as header
       - in LibraryAPI_Util we have a method to get token,
            which gonna send post request to /login endpoint
            the request gonna take the email and password of the usertype
            and send it as formParam()

     */

    @Given("I logged Library api as a {string} MY")
    public void iLoggedLibraryApiAsAMY(String userType) {

        token = LibraryAPI_Util.getToken(userType);

        givenPart = given().log().all()
                .header("x-library-token", token);


    }

    @And("Accept header is {string} MY")
    public void acceptHeaderIsMY(String contentType) {
        givenPart.accept(contentType);

    }

    @And("Request Content Type header is {string} MY")
    public void requestContentTypeHeaderIsMY(String contentType) {
        givenPart.header("Content-Type", contentType);
    }

    @And("I create a random {string} as request body MY")
    public void iCreateARandomAsRequestBodyMY(String creatingType) {

        userMap_API = LibraryAPI_Util.getRandomUserMap();

        givenPart.formParams(userMap_API);
        //send body with formParams , since its urlencoded!!! can not use body()


    }

    @When("I send POST request to {string} endpoint MY")
    public void iSendPOSTRequestToEndpointMY(String endpoint) {

        response = givenPart.when().post(ConfigurationReader.getProperty("library.baseUri") + endpoint).prettyPeek();
        thenPart = response.then();
    }

    @Then("status code should be {int} MY")
    public void statusCodeShouldBeMY(int statusCode) {
        thenPart.statusCode(statusCode);

    }

    @And("Response Content type is {string} MY")
    public void responseContentTypeIsMY(String contentType) {
        thenPart.contentType(contentType);

    }

    @And("the field value for {string} path should be equal to {string} MY")
    public void theFieldValueForPathShouldBeEqualToMY(String path, String expectedValue) {
        thenPart.body(path, is(expectedValue));

    }

    @And("{string} field should not be null MY")
    public void fieldShouldNotBeNullMY(String path) {
        thenPart.body(path, is(notNullValue()));

        //get userId from the response for later verification
        userId = thenPart.extract().jsonPath().getString(path);
    }

    @And("created user information should match with Database MY")
    public void createdUserInformationShouldMatchWithDatabaseMY() {

        //1. get user info from API --> userMap_API
        //2. create database connection--> hooks created already
        //3. run query
        String query = "select full_name,email,address from users where id=" + userId + "";
        DB_Util.runQuery(query);
        Map<String, Object> userMap_DB = DB_Util.getRowMap(1);

       // we can not assert password!!

        //4. compare API, DB,
        Assert.assertEquals(userMap_DB.get("full_name"),userMap_API.get("full_name"));
        Assert.assertEquals(userMap_DB.get("email"),userMap_API.get("email"));

    }

    @And("created user should be able to login Library UI MY")
    public void createdUserShouldBeAbleToLoginLibraryUIMY() {
        //API --> UI
        String email = (String) userMap_API.get("email");
        String password = (String) userMap_API.get("password");

        LoginPage loginPage = new LoginPage();
        loginPage.login(email, password);

        //after login successful, user should see dashboard
        BrowserUtil.waitFor(3);
        Assert.assertTrue(Driver.getDriver().getTitle().contains("Library"));


    }

    @And("created user name should appear in Dashboard Page MY")
    public void createdUserNameShouldAppearInDashboardPageMY() {

        DashboardPage_MY dashboardPage_my = new DashboardPage_MY();
        String userName_UI = dashboardPage_my.accountHolderName.getText();
        String userName_API = (String) userMap_API.get("full_name");
        System.out.println(userName_UI);

        Assert.assertEquals(userName_API, userName_UI);


    }
}
