@tag
Feature:

  Background:
    Given the user is on the version page

  Scenario: Version and runtime information should be showed for the Webapp and microservices
    Then The user should be able to see version and runtime information for the webapp
    Then The user should be able to see version and runtime information for the microservices
