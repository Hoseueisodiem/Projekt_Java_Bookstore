package pl.bookstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.bookstore.service.penalty.PenaltyStrategy;
import pl.bookstore.service.penalty.ProgressivePenaltyStrategy;
import pl.bookstore.service.penalty.StandardPenaltyStrategy;

import java.util.Map;

@Configuration
public class PenaltyConfig {

    // Aktywna strategia wybierana wlasciwoscia app.penalty.strategy (domyslnie standard)
    @Bean
    @Primary
    public PenaltyStrategy penaltyStrategy(
            @Value("${app.penalty.strategy:standard}") String selected,
            StandardPenaltyStrategy standard,
            ProgressivePenaltyStrategy progressive) {

        Map<String, PenaltyStrategy> available = Map.of(
                "standard", standard,
                "progressive", progressive
        );
        return available.getOrDefault(selected.toLowerCase(), standard);
    }
}
