package ru.yandex.practicum.config.conventer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConversionConfig {

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new ScenarioConditionAvroToConditionConverter());
        conversionService.addConverter(new DeviceActionAvroToAction());

        return conversionService;
    }
}
