package pl.bookstore.service.penalty;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("standard")
public class StandardPenaltyStrategy implements PenaltyStrategy {

    private static final BigDecimal RATE_PER_DAY = new BigDecimal("1.00");

    @Override
    public BigDecimal calculate(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return RATE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays));
    }
}
