package openWeather.learning.com;


import java.io.Serializable;
/**
 * Created by tim_barrett on 9/2015.
 * bean to store data parsed from JSON weather string
 *
 */
public class weatherDataBean implements Serializable {

    private int id;
    private String location;
    private String temperature;
    private String wind;
    private String windSpeed;
    private String humidity;
    private String condition;
    private String conditionDescription;

    public weatherDataBean (int id, String location){
        this.id = id ;
        this.location = location;
    }
    private weatherDataBean() { }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public String getTemperature() {return temperature;}

    public void setTemperature(String temperature) {this.temperature = temperature;}

    public String getWind() {return wind;}

    public void setWind(String wind) {this.wind = wind;}

    public String getWindSpeed() {return windSpeed;}

    public void setWindSpeed(String windSpeed) {this.windSpeed = windSpeed;}

    public String getHumidity() {return humidity;}

    public void setHumidity(String humidity) {this.humidity = humidity;}

    public String getCondition() {return condition;}

    public void setCondition(String condition) {this.condition = condition;}

    public String getConditionDescription() {return conditionDescription;}

    public void setConditionDescription(String conditionDescription) {this.conditionDescription = conditionDescription;}

}
