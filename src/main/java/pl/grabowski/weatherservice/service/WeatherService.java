package pl.grabowski.weatherservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.grabowski.weatherservice.controller.dto.Weather;
import pl.grabowski.weatherservice.domain.CityForecast;
import pl.grabowski.weatherservice.config.AppCity;
import pl.grabowski.weatherservice.integration.ForecastResource;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WeatherService {
    private final ForecastResource forecastResource;
    private final ObjectMapper objectMapper;
    private final AppCity cities;
    private final Clock clock;


    @Scheduled(fixedRate = 5000)
    private void SendMessage(){

    }

    private CityForecast jsonParseToObject(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    public List<Weather> getForecast(LocalDate date) throws JsonProcessingException {
        List<CityForecast> citiesForecast = new ArrayList<>();
        for (int i = 0; i < cities.getCities().size(); i++) {
            citiesForecast.add(forecastResource.getCityForecast(i));
        }
        return citiesForecast.stream().map(forecast -> new Weather(
                forecast.getCityName(),
                forecast.getForecastByDate(date).get().getLocalDate(),
                forecast.getForecastByDate(date).get().getWindSpeed(),
                forecast.getForecastByDate(date).get().getTemp()
        )).collect(Collectors.toList());
    }

    public boolean isValidDate(LocalDate date){
        return date.isAfter(LocalDate.now(clock).minusDays(1)) && date.isBefore(LocalDate.now(clock).plusDays(17));
    }
}
