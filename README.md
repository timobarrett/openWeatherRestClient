# openWeatherRestClient
OpenWeatherMap API Rest Client integrated with Apache's couchdb

Rest client integration with openWeatherMap records are stored in noSql couchdb database.  SimpleJson library used to parse data received from openWeatherMap requests.  All couchdb intergration is done using rest calls to couchdb.  CouchDb rest calls include:
  1. Database creation if database is not present
  2. Database inserts
  3. Database record retrival - all records for a single day
  4. Database record deletion
  5. Database deletion
  
Code was developed using IntellijIDEA communit edition version 14. A Free developer key from OpenWeatherMap is needed to run application.
