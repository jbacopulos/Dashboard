package org.dashboard.dashboard;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DashboardController {

    private final String mode = "xml";
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm");
    private final DateTimeFormatter secondsFormatter = DateTimeFormatter.ofPattern("ss");
    private final DateTimeFormatter periodFormatter = DateTimeFormatter.ofPattern("a");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd");
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder builder = factory.newDocumentBuilder();
    Dotenv dotenv = Dotenv.load();
    private final String zip = dotenv.get("ZIP");
    private final String units = dotenv.get("UNIT");
    private final String owApiKey = dotenv.get("OW_API_KEY");
    private final String wApiKey = dotenv.get("W_API_KEY");
    @FXML
    private Label timeText;
    @FXML
    private Label secondsText;
    @FXML
    private Label periodText;
    @FXML
    private Label dateText;
    @FXML
    private GridPane newsGrid;
    @FXML
    private Label currentTempText;
    @FXML
    private Label feelsLikeText;
    @FXML
    private Label weatherText;
    @FXML
    private Label todayPrecipitationText;
    @FXML
    private ImageView todayWeatherIcon;
    @FXML
    private Label todayHighTempText;
    @FXML
    private Label todayLowTempText;
    @FXML
    private Label tomorrowText;
    @FXML
    private Label tomorrowPrecipitationText;
    @FXML
    private ImageView tomorrowWeatherIcon;
    @FXML
    private Label tomorrowHighTempText;
    @FXML
    private Label tomorrowLowTempText;
    @FXML
    private Label overmorrowText;
    @FXML
    private Label overmorrowPrecipitationText;
    @FXML
    private ImageView overmorrowWeatherIcon;
    @FXML
    private Label overmorrowHighTempText;
    @FXML
    private Label overmorrowLowTempText;

    public DashboardController() throws ParserConfigurationException {
    }

    @FXML
    public void initialize() {
        // Create a Timeline to update the timeText label every second
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateCurrentTime()),
                new KeyFrame(Duration.seconds(1))
        );

        Timeline weatherTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateNewsAndWeather()),
                new KeyFrame(Duration.seconds(600))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        weatherTimeline.setCycleCount(Timeline.INDEFINITE);
        weatherTimeline.play();
    }

    // Utility methods
    private String getValue(Document doc, String tag, String attribute) {
        return doc.getElementsByTagName(tag).item(0).getAttributes().getNamedItem(attribute).getNodeValue();
    }

    private String doubleStringToIntString(String s) {
        return Integer.toString((int) Math.round(Double.parseDouble(s)));
    }

    private Document fetchAndParseXML(String urlString) throws SAXException, IOException {
        URL url = new URL(urlString);
        InputStream stream = url.openStream();
        Document document = builder.parse(stream);
        document.getDocumentElement().normalize();
        return document;
    }

    private String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String formatPercentage(String value) {
        return doubleStringToIntString(value) + "%";
    }

    // Update methods
    private void updateCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();

        String time = currentTime.format(timeFormatter);
        String seconds = currentTime.format(secondsFormatter);
        String period = currentTime.format(periodFormatter).toUpperCase().replace(".", "");
        String date = currentTime.format(dateFormatter);

        timeText.setText(time);
        secondsText.setText(seconds);
        periodText.setText(period);
        dateText.setText(date);
    }

    private void updateNewsAndWeather() {
        updateNews();
        updateWeather();
    }

    private void updateNews() {
        try {
            // Fetch and parse news data
            Document news = fetchAndParseXML("https://feeds.bbci.co.uk/news/world/rss.xml");

            for (int i = 0; i < 8; i++) {
                Label newsText = new Label();
                newsGrid.add(newsText, 0, i);
                newsText.getStyleClass().add("newsText");
                newsText.setWrapText(true);
                newsText.setText(news.getElementsByTagName("item").item(i).getChildNodes().item(1).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWeather() {
        try {
            // Fetch and parse current weather data
            Document weather = fetchAndParseXML(String.format("https://api.openweathermap.org/data/2.5/weather?zip=%s&mode=%s&units=%s&appid=%s", zip, mode, units, owApiKey));

            String currentTemp = doubleStringToIntString(getValue(weather, "temperature", "value"));
            String feelsLike = doubleStringToIntString(getValue(weather, "feels_like", "value"));
            String weatherName = capitalizeFirstLetter(getValue(weather, "weather", "value"));

            currentTempText.setText(String.format("%s°", currentTemp));
            feelsLikeText.setText(String.format("Feels like %s°", feelsLike));
            weatherText.setText(weatherName);

            // Fetch and parse forecast data
            Document forecast = fetchAndParseXML(String.format("https://api.weatherapi.com/v1/forecast.xml?key=%s&q=%s&days=3", wApiKey, zip.split(",")[0]));

            updateForecast(forecast, 0, todayPrecipitationText, todayHighTempText, todayLowTempText, todayWeatherIcon);
            updateForecast(forecast, 1, tomorrowPrecipitationText, tomorrowHighTempText, tomorrowLowTempText, tomorrowWeatherIcon);
            updateForecast(forecast, 2, overmorrowPrecipitationText, overmorrowHighTempText, overmorrowLowTempText, overmorrowWeatherIcon);

            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
            tomorrowText.setText(tomorrow.format(DateTimeFormatter.ofPattern("EEE.")));

            LocalDateTime overmorrow = LocalDateTime.now().plusDays(2);
            overmorrowText.setText(overmorrow.format(DateTimeFormatter.ofPattern("EEE.")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateForecast(Document forecast, int dayIndex, Label precipitationText, Label highTempText, Label lowTempText, ImageView weatherIcon) {
        Node day = forecast.getElementsByTagName("forecast").item(0).getChildNodes().item(dayIndex);
        Node dayDetails = day.getChildNodes().item(2);

        precipitationText.setText(formatPercentage(dayDetails.getChildNodes().item(15).getTextContent()));
        highTempText.setText(doubleStringToIntString(dayDetails.getChildNodes().item(0).getTextContent()) + "°");
        lowTempText.setText(doubleStringToIntString(dayDetails.getChildNodes().item(2).getTextContent()) + "°");

        try {
            BufferedImage weatherImage = ImageIO.read(new URL("http:" + dayDetails.getChildNodes().item(18).getChildNodes().item(1).getTextContent()));
            weatherIcon.setImage(SwingFXUtils.toFXImage(weatherImage, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
