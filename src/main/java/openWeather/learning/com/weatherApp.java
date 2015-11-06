package openWeather.learning.com;


import java.net.URISyntaxException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by tim_barrett on 9/2015.
 *   get the daily weather using latitude and longitude
 * http://api.openweathermap.org/data/2.5/weather?lat=42.831&lon=-71.569&units=imperial - not fine grained enough
 *    get the daily weather forecast using zipcode / post code
 * http://api.openweathermap.org/data/2.5/forecast?zip=03031 - returns forecasted data
 *    get the weather for cast for 7 days using zip code
 * http://api.openweathermap.org/data/2.5/forecast/daily?q=03031&mode=json&units=metric&cnt=7
 */
public class weatherApp {

    public static String LON ="lon";
    public static String ZIP = "zip";
    public static String LAT = "lat";
    public static String APPID = "appid";
    public static String APPID_VALUE = ""; //get a free key from http://home.openweathermap.org/users/sign_up

    public static String TAG_NAME = "main";
    public static restClientFunc restFunction;
    public static String OPEN_WEATHER_URL = "api.openweathermap.org";
    public static String OPEN_WEATHER_PATH = "/data/2.5/forecast/daily";
    public static String OPEN_WEATHER_PATH_FRCAST = "/data/2.5/weather";
    public static String MODE = "mode";
    public static String MODE_VALUE = "json";
    public static String UNIT = "units";
    public static String UNIT_VALUE = "imperial";
    public static String CNT = "cnt";
    public static String CNT_VALUE = "7";

    public enum forecast { dailyZip, dailyLonLat, sevenDay}
        forecast mForecast;

  //  public static String dbName = "http://127.0.0.1:5984/weather";
    //http://api.openweathermap.org/data/2.5/forecast/daily?appid={get your own}&zip=03031&mode=json&units=imperial&cnt=7

    /**
     * Entry point and a basic means to test all methods.
     *
     * @param args - zip code is passed in as parameter
     */
    public static void main(String[] args) {

        weatherApp weather = new weatherApp();
        restFunction = new restClientFunc();
     //   weather.deleteWeatherDb();
        if (!weather.verifyDbExists()){
            weather.createWeatherDb();
        }
     //   weather.getDbRecord("21092015");
     //     weather.deleteDateWeatherDb();
     //   weather.getDbRecord("21092015");
        //WEATHER_URL + location + formatInfo
     //   String weatherURL = WEATHER_URL +args[0]+FORMAT_INFO;
        String weatherResult = weather.getWeatherForecast(args[0], args[1],args[2]);
    //    weather.getWeatherForecast(args[0]);
        System.out.println("RESULTS HERE = " + weatherResult);
        weather.loadReportWeather(weatherResult);
        System.out.println("RESULTS = " + weatherResult);
    }

    /**
     * make the rest call to openweathermap and return the json output
     *
     * @params
     *      zip code,
     *      longitude
     *      latitude
     * @return
     */
    public String getWeatherForecast(String... params) {

        ArrayList<String> results;
        // build URI and get daily forecast using zipcode
        URI zipUri = buildWeatherForecastUrl(forecast.dailyZip, params[0]);
        results = restFunction.restGet(zipUri.toString());
        for (String result : results) {
            System.out.println("\n GET WEATHER INFO RESULT(ZIP) = " + result);
            populateDb2(result);
        }
        results.clear();
        //build URI and get 7 day forecast
        URI zip7Uri = buildWeatherForecastUrl(forecast.sevenDay, params[0]);
        results = restFunction.restGet(zip7Uri.toString());
        for (String result : results) {
            System.out.println("\n GET WEATHER INFO RESULT(ZIP) = " + result);
            populateDb2(result);
        }
        results.clear();
        //build URI and get daily forecast using longitude and latitude
        URI lonLatUri = buildWeatherForecastUrl(forecast.dailyLonLat, params[1], params[2]);
        results = restFunction.restGet(lonLatUri.toString());
        for (String result : results){
            System.out.println ("GET WEATHER INFO RESULT(LON LAT) = " + result);
            populateDb2(result);
        }
        return results.get(0);


    }

    /**
     *  get daily weather and 7 day forecast
     *
     * @param forecastType
     * @param param
     * @return
     */
    public URI buildWeatherForecastUrl(forecast forecastType,String... param){
        ArrayList<String> results;
        URIBuilder builtUri;
        URI uri = null;
        switch (forecastType) {
            case dailyLonLat:
                try {
                    builtUri = new URIBuilder()
                            .setScheme("http")
                            .setHost(OPEN_WEATHER_URL)
                            .setPath(OPEN_WEATHER_PATH_FRCAST)
                            .addParameter(LAT, param[0])
                            .addParameter(LON, param[1])
                            .addParameter(APPID, APPID_VALUE)
                            .addParameter(MODE, MODE_VALUE)
                            .addParameter(UNIT, UNIT_VALUE);
                    uri = builtUri.build();
                } catch (URISyntaxException u) {
                    System.out.println("ERROR - URI EXCEPTION");
                }
                break;
            case dailyZip:
                try {
                    builtUri = new URIBuilder()
                            .setScheme("http")
                            .setHost(OPEN_WEATHER_URL)
                            .setPath(OPEN_WEATHER_PATH_FRCAST)
                            .addParameter(ZIP, param[0])
                            .addParameter(APPID, APPID_VALUE)
                            .addParameter(MODE, MODE_VALUE)
                            .addParameter(UNIT, UNIT_VALUE);
                    uri = builtUri.build();
                } catch (URISyntaxException u) {
                    System.out.println("ERROR - URI EXCEPTION");
                }
                break;
            case sevenDay:
                try {
                    builtUri = new URIBuilder()
                            .setScheme("http")
                            .setHost(OPEN_WEATHER_URL)
                            .setPath(OPEN_WEATHER_PATH)
                            .addParameter(ZIP, param[0])
                            .addParameter(APPID, APPID_VALUE)
                            .addParameter(MODE, MODE_VALUE)
                            .addParameter(UNIT, UNIT_VALUE)
                            .addParameter(CNT, CNT_VALUE);
                    uri = builtUri.build();
                } catch (URISyntaxException u) {
                    System.out.println("ERROR - URI EXCEPTION");
                }
                break;
        }
        if (uri != null) {
            System.out.println("BUILT URI = " + uri.toString());
        }
        return uri;
    }
    /**
     * loadReportWeather - parse the json string for data
     * shows json array and sub key handling
     *
     * @param jsonBuf
     * @return
     */
    public String loadReportWeather(String jsonBuf) {
        JSONParser parser = new JSONParser();
        System.out.println("loadReportWeather = " + jsonBuf);
        Object obj = null;
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        weatherDataBean weatherData = new weatherDataBean(Integer.parseInt(sdf.format(new Date()).toString())," ");

        try {
            obj = parser.parse(jsonBuf);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject weObj = (JSONObject) jsonObject.get(TAG_NAME);
            weatherData.setHumidity(weObj.get("humidity").toString());
            weatherData.setTemperature((weObj.get("temp")).toString());
            System.out.println("Temp = " + weObj.get("temp") + " Humidity = " + weObj.get("humidity") + "%");
            JSONObject locObj = (JSONObject) jsonObject.get("coord");
            System.out.println("LONG/LAT = " + locObj.get("lon") + " / " + locObj.get("lat"));
            JSONArray conf = (JSONArray) jsonObject.get("weather");
            JSONObject weaObj = (JSONObject) conf.get(0);

            JSONObject windObj = (JSONObject) jsonObject.get("wind");
            weatherData.setWindSpeed(windObj.get("speed").toString());
            System.out.println("Wind speed = " + windObj.get("speed"));
            JSONObject location = (JSONObject) jsonObject.get("sys");
            weatherData.setLocation(jsonObject.get("name").toString());
            System.out.println("Run from " + jsonObject.get("name") + " " + location.get("country"));
            weatherData.setCondition(weaObj.get("main").toString());
            weatherData.setConditionDescription(weaObj.get("description").toString());
            System.out.println("Condition = " + weaObj.get("main") + " Detail = " + weaObj.get("description"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (" ");
    }

    /**
     * populateDb2 - write JSON record to database
     *
     * @param buffer
     */
    public void populateDb2(String buffer) {

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        StringBuffer buffer2 = new StringBuffer(buffer);
        buffer2.insert(1, "\"key\":\"" + sdf.format(new Date()) + "\"," + "\"_id\":\""+sdf.format(new Date())+ "\",");
        System.out.println("BUFFER2 = " + buffer2);
        restFunction.restPost(buffer2.toString());
    }

    /**
     *  gets the records from couchDB for a single day.
     *  TODO: add ability to generate startand end key based on information passed in
     *  TODO: add ability to return all records - remove start and end keys
     * @param dateKey
     */
    public void getDbRecord(String dateKey) {
        int rCnt = 1;
        ArrayList<String> results = restFunction.restGet("http://127.0.0.1:5984/weather/_all_docs?startkey=%2224092015000000%22&endkey=%2224092015999999%22&include_docs=true");

        for (String recResult : results){
            System.out.println("RECRESULT = " + recResult);
            if(recResult.contains("}}")) {
                rCnt = 1;
                if (recResult.endsWith("}},")) {
                    rCnt = 2;
                }
                loadReportWeather("{" + recResult.substring(recResult.indexOf("coord") - 1, recResult.length() - rCnt));
            }
        }
    }

    /**
     * check the databases in couchDb verify that our database exists.
     * @return
     */
    public boolean verifyDbExists () {
        boolean exists = false;

        ArrayList<String> results = restFunction.restGet("http://127.0.0.1:5984/_all_dbs");
        for (String result: results){
            if (result.contains("weather")) {
                exists = true;
            }
        }
        return exists;
    }

    /**
     * creates the weather db
     * @return
     */
    public boolean createWeatherDb (){
        System.out.println("creating weather DB");
        boolean exists = false;
        int result = restFunction.restPut("http://127.0.0.1:5984/weather/");
        if (result == 1 || result == 412){
            exists = true;
        }
        return exists;
    }

    /**
     * Deletes the entire weather database
     */
    public void deleteWeatherDb() {
        System.out.println("Deleting weather DB");
        restFunction.restDelete("http://127.0.0.1:5984/weather/");
    }

    /**
     * delete all database entries for the day
     * startkey and endkey are hard coded to begin date & time
     *
     * TODO: address startkey and endkey compilation
     */
    public void deleteDateWeatherDb() {
        System.out.println("deleteDateWeatherDB");
        int rCnt = 1;
        String[] keyVal;
        ArrayList<String> results = restFunction.restGet("http://127.0.0.1:5984/weather/_all_docs?startkey=%2224092015000000%22&endkey=%2224092015999999%22&include_docs=true");

        for (String result:results) {
            System.out.println("RESULTS = " + result);
            if (result.contains("}}")) {
                rCnt = 0;
                if (result.endsWith("}},"))
                    rCnt = 1;
                String doo = "{" + result.substring(result.indexOf("value") - 1, result.length() - rCnt);
                keyVal = parseDbRecord(doo);
                if (keyVal != null) {
                    nowDeleteRecord(keyVal[0], keyVal[1]);
                }
            }
        }
    }

    /**
     * Tags db documents as deleted
     *      have to specify rev of document to complete
     *      did not find any records with date _id when checking with futon after run
     * @param keyVal
     * @param rev
     *
     */
    private void nowDeleteRecord(String keyVal, String rev){
        System.out.println("nowDeleteRecord");
        restFunction.restDelete("http://127.0.0.1:5984/weather/"+keyVal+"?rev="+rev);
        }

    /**
     * parse the db record and return the id and rev - needed to delete record.
     * @param dbRec
     * @return
     */
    public String[] parseDbRecord(String dbRec) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        String[] idVal = new String[2];
        String parsedVals;
        System.out.println("PARSE DB REC = " + dbRec);

        try {
            obj = parser.parse(dbRec);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject jID = (JSONObject) jsonObject.get("doc");
            idVal[0] = String.valueOf(jID.get("_id"));
            JSONObject jRev = (JSONObject) jsonObject.get("value");
            idVal[1] = String.valueOf(jRev.get("rev"));
            System.out.println("KEY VAL = "+idVal[0] + "  " + idVal[1]);
        } catch (ParseException e) {
         //   System.out.println("Parser exception - parseDbRecord");
            System.out.println(e.getStackTrace());
        }
        return idVal;
    }
}