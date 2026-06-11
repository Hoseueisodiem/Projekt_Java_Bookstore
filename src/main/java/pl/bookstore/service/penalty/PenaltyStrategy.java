package pl.bookstore.service.penalty;

import java.math.BigDecimal;

public interface PenaltyStrategy {

    BigDecimal calculate(long overdueDays);
}
