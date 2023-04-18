@US03_ROA
Feature: As a librarian, I want to create a new book


  @US03_1_ROA @db @ui
  Scenario: Create a new book API
    Given I logged Library api as a "librarian" ROA
    And Accept header is "application/json" ROA
    And Request Content Type header is "application/x-www-form-urlencoded" ROA
    And I create a random "book" as request body ROA
    When I send POST request to "/add_book" endpoint ROA
    Then status code should be 200 ROA
    And Response Content type is "application/json; charset=utf-8" ROA
    And the field value for "message" path should be equal to "The book has been created." ROA
    And "book_id" field should not be null ROA


  @US03_2_ROA @ui @db
  Scenario: Create a new book all layers
    Given I logged Library api as a "librarian" ROA
    And Accept header is "application/json" ROA
    And Request Content Type header is "application/x-www-form-urlencoded" ROA
    And I create a random "book" as request body ROA
    And I logged in Library UI as "librarian" ROA
    And I navigate to "Books" page ROA
    When I send POST request to "/add_book" endpoint ROA
    Then status code should be 200 ROA
    And Response Content type is "application/json; charset=utf-8" ROA
    And the field value for "message" path should be equal to "The book has been created." ROA
    And "book_id" field should not be null ROA
    And UI, Database and API created book information must match ROA