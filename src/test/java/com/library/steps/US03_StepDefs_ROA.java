package com.library.steps;

import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.DashboardPage_ROA;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class US03_StepDefs_ROA {

    LoginPage loginPage = new LoginPage();
    BasePage basePage;
    public WebElement book_UI;
    String bookID;
    RequestSpecification createdBook;

    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;
    Map<String, Object> BookMap_API;

    @Given("I logged Library api as a {string} ROA")
    public void i_logged_library_api_as_a_roa(String userType) {
        givenPart = given().log().uri()
                .header("x-library-token", LibraryAPI_Util.getToken(userType));

    }
    @Given("Accept header is {string} ROA")
    public void accept_header_is_roa(String contentType) {
        givenPart.accept(contentType);
    }
    @Given("Request Content Type header is {string} ROA")
    public void request_content_type_header_is_roa(String contentTypeHeader) {

        givenPart.contentType(contentTypeHeader);
    }
    @Given("I create a random {string} as request body ROA")
    public void i_create_a_random_as_request_body_roa(String createBook) {
        BookMap_API = LibraryAPI_Util.getRandomBookMap();
        givenPart.formParams(BookMap_API);
    }
    @When("I send POST request to {string} endpoint ROA")
    public void i_send_post_request_to_endpoint_roa(String endpoint) {
        response = givenPart.when().post(ConfigurationReader.getProperty("library.baseUri") + endpoint).prettyPeek();
        thenPart = response.then();
    }
    @Then("status code should be {int} ROA")
    public void status_code_should_be_roa(Integer statusCode) {
        thenPart.statusCode(statusCode);
    }
    @Then("Response Content type is {string} ROA")
    public void response_content_type_is_roa(String contentType) {
        thenPart.contentType(contentType);
    }
    @Then("the field value for {string} path should be equal to {string} ROA")
    public void the_field_value_for_path_should_be_equal_to_roa(String message, String bookCreated) {
        thenPart.body(message, equalTo(bookCreated));
    }
    @Then("{string} field should not be null ROA")
    public void field_should_not_be_null_roa(String notNull) {
        thenPart.body(notNull, is(notNullValue()));
    }

    @Given("I logged in Library UI as {string} ROA")
    public void i_logged_in_library_ui_as_roa(String userTypeUI) {
        loginPage.login(userTypeUI);
    }
    @Given("I navigate to {string} page ROA")
    public void i_navigate_to_page_roa(String books) {
        BrowserUtil.waitFor(5);
        DashboardPage_ROA dashboardPage_roa = new DashboardPage_ROA();
        dashboardPage_roa.navigateModule(books);
    }
    @Then("UI, Database and API created book information must match ROA")
    public void ui_database_and_api_created_book_information_must_match_roa() {
        bookID =  response.path("book_id");

        String queryBook = "select name from books where books.id = " + bookID ;

        DB_Util.runQuery(queryBook);

        Map<String, Object> bookMap_DB = DB_Util.getRowMap(1);

        System.out.println("bookMap_DB.get(\"name\") = " + bookMap_DB.get("name"));

        String bookName = (String) BookMap_API.get("name");

        BookPage bookPage = new BookPage();
        bookPage.search.sendKeys(bookName);

        BrowserUtil.waitFor(5);
        bookPage.editBook(bookName).click();
        BrowserUtil.waitFor(5);

        String book_UI = bookPage.bookName.getAttribute("value");

        Assert.assertEquals(BookMap_API.get("name"),bookMap_DB.get("name"));
        Assert.assertEquals(bookName, book_UI);
    }

}
