package pl.bookstore.service.penalty;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("progressive")
public class ProgressivePenaltyStrategy implements PenaltyStrategy {

    private static final BigDecimal BASE_RATE = new BigDecimal("1.00");
    private static final BigDecimal HIGH_RATE = new BigDecimal("2.00");
    private static final int THRESHOLD_DAYS = 7;

    @Override
    public BigDecimal calculate(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        if (overdueDays <= THRESHOLD_DAYS) {
            return BASE_RATE.multiply(BigDecimal.valueOf(overdueDays));
        }
        BigDecimal base = BASE_RATE.multiply(BigDecimal.valueOf(THRESHOLD_DAYS));
        BigDecimal extra = HIGH_RATE.multiply(BigDecimal.valueOf(overdueDays - THRESHOLD_DAYS));
        return base.add(extra);
    }
}
