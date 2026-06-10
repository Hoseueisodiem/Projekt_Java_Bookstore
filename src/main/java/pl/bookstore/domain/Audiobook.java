package pl.bookstore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("AUDIOBOOK")
@Getter
@Setter
public class Audiobook extends Book {

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    private String narrator;

    @Override
    public int loanPeriodDays() {
        return 21;
    }
}
