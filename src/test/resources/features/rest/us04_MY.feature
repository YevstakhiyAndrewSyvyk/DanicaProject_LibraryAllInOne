@us04
Feature: As a librarian, I want to create a new user

  Scenario: Create a new user API
    Given I logged Library api as a "librarian" MY
    And Accept header is "application/json" MY
    And Request Content Type header is "application/x-www-form-urlencoded" MY
    And I create a random "user" as request body MY
    When I send POST request to "/add_user" endpoint MY
    Then status code should be 200 MY
    And Response Content type is "application/json; charset=utf-8" MY
    And the field value for "message" path should be equal to "The user has been created." MY
    And "user_id" field should not be null MY


  @ui@db
  Scenario: Create a new user all layers
    Given I logged Library api as a "librarian" MY
    And Accept header is "application/json" MY
    And Request Content Type header is "application/x-www-form-urlencoded" MY
    And I create a random "user" as request body MY
    When I send POST request to "/add_user" endpoint MY
    Then status code should be 200 MY
    And Response Content type is "application/json; charset=utf-8" MY
    And the field value for "message" path should be equal to "The user has been created." MY
    And "user_id" field should not be null MY
    And created user information should match with Database MY
    And created user should be able to login Library UI MY
    And created user name should appear in Dashboard Page MY