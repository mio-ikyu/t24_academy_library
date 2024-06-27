package jp.co.metateam.library.model;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 書籍マスタDTO
 */
@Getter
@Setter
public class CalendarDto {

    private String title;

    private Long bookId;

    private String availableStockCount;
 
    private List<StockByDayDto> stockCountByDay;

    private List<String> stockIdList;

    
}