package pl.bookstore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("PRINTED")
@Getter
@Setter
public class PrintedBook extends Book {

    private Integer pages;

    @Column(name = "cover_type")
    private String coverType;

    @Override
    public int loanPeriodDays() {
        return 30;
    }

    @Override
    public BookFormat format() {
        return BookFormat.PRINTED;
    }
}
