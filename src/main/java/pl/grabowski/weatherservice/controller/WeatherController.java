package pl.grabowski.weatherservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.grabowski.weatherservice.service.BestWeatherSelector;
import pl.grabowski.weatherservice.service.ForecastResource;
import pl.grabowski.weatherservice.service.WeatherService;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping(path= "/weather")
public class WeatherController {

    private final ForecastResource parseService;
    private final WeatherService weatherService;
    private final BestWeatherSelector bestWeatherSelector;
    private final Clock clock;

    public WeatherController(ForecastResource parseService, WeatherService weatherService, BestWeatherSelector bestWeatherSelector, Clock clock) {
        this.parseService = parseService;
        this.weatherService = weatherService;
        this.bestWeatherSelector = bestWeatherSelector;
        this.clock = clock;
    }

    @GetMapping
    ResponseEntity<?> getBestWeatherFromApiByDay(@RequestParam("date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) throws JsonProcessingException {
        if(date.isAfter(LocalDate.now(clock).minusDays(1)) && date.isBefore(LocalDate.now(clock).plusDays(17))){
            var forecast = weatherService.getForecast(date);
            var bestWeatherResponse = bestWeatherSelector.getBestCity(forecast);
            return bestWeatherResponse.map(weather -> new ResponseEntity<>(weather, HttpStatus.OK)).orElseGet(() -> ResponseEntity.noContent().build());
        }
        return ResponseEntity.badRequest().body("Date is wrong!");
    }
}
