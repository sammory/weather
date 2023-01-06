package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiaryService {

    // value 에 있는 openweathermap.key 값을 apiKey 에 넣어줌
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;


    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public void createDiary(LocalDate date, String text) {
        // open weather map 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 파싱된 데이터  + 일기 값 dB 에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setWeather(parsedWeather.get("main").toString());
        nowDiary.setIcon(parsedWeather.get("icon").toString());
        nowDiary.setTemperature((Double) parsedWeather.get("temp"));
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);


    }

    private String getWeatherString() {

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        // url 값이 오류가 발생하였을때
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");                                              // GET 으로 요청
            int responseCode = connection.getResponseCode();                                 // 응답을 int 로 받음
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 응답을 받아 br 에 넣음
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {                      // br에 담긴 값이 null 이 될때까지 response 에 담는다
                response.append(inputLine);
            }
            br.close();

            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }
}
