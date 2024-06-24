package jp.co.metateam.library.model;

import java.security.Timestamp;
import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 書籍マスタDTO
 */
@Getter
@Setter

public class CalendarDto {

    private Long id;
    private String title;
    private long stockCount;
    private BookMst bookMst;
    private Date expectedRentalOn;
    private Date expectedReturnOn;

    /** Getters */

    public Long getId() {
        return id;
    }

    public Date getExpectedRentalOn() {
        return expectedRentalOn;
    }

    public Date getExpectedReturnOn() {
        return expectedReturnOn;
    }


    /** Setters */

 public void setId(Long id) {
    this.id = id;
}

public void setExpectedRentalOn(Date expectedRentalOn) {
    this.expectedRentalOn = expectedRentalOn;
}

public void setExpectedReturnOn(Date expectedReturnOn) {
    this.expectedReturnOn = expectedReturnOn;
}

    
}