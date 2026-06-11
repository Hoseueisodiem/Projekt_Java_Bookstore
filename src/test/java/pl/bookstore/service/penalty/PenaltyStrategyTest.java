package pl.bookstore.service.penalty;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PenaltyStrategyTest {

    private final StandardPenaltyStrategy standard = new StandardPenaltyStrategy();
    private final ProgressivePenaltyStrategy progressive = new ProgressivePenaltyStrategy();

    @Test
    void standardNoPenaltyWhenNotOverdue() {
        assertEquals(0, standard.calculate(0).compareTo(BigDecimal.ZERO));
        assertEquals(0, standard.calculate(-3).compareTo(BigDecimal.ZERO));
    }

    @Test
    void standardChargesOnePerDay() {
        assertEquals(0, standard.calculate(5).compareTo(new BigDecimal("5")));
    }

    @Test
    void progressiveNoPenaltyWhenNotOverdue() {
        assertEquals(0, progressive.calculate(0).compareTo(BigDecimal.ZERO));
    }

    @Test
    void progressiveFlatRateUpToThreshold() {
        assertEquals(0, progressive.calculate(7).compareTo(new BigDecimal("7")));
    }

    @Test
    void progressiveHigherRateBeyondThreshold() {
        // 7 dni * 1 + 3 dni * 2 = 13
        assertEquals(0, progressive.calculate(10).compareTo(new BigDecimal("13")));
    }
}
