package pl.bookstore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("EBOOK")
@Getter
@Setter
public class Ebook extends Book {

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "file_size_mb", precision = 6, scale = 2)
    private BigDecimal fileSizeMb;

    @Override
    public int loanPeriodDays() {
        return 14;
    }

    @Override
    public BookFormat format() {
        return BookFormat.EBOOK;
    }
}
