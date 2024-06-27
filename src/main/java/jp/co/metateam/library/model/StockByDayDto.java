package jp.co.metateam.library.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockByDayDto {

    private String stockCount;

    private LocalDate expectedRentalOn;

}
