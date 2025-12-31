Feature: SpaceX API - launches

Scenario: GET /launches should return an array
  Given url 'https://api.spacexdata.com/v4/launches'
  When method get
  Then status 200
  And match responseType == 'json'
  And match response == '#[]'

