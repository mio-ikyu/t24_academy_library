package jp.co.metateam.library.model;

import java.security.Timestamp;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * 在庫管理DTO
 */
@Getter
@Setter
public class StockDto {

    @NotEmpty(message = "在庫管理番号は必須です")
    private String id;

    @NotNull(message = "書籍名は必須です")
    private Long bookId;

    @NotNull(message = "在庫ステータスは必須です")
    private Integer status;

    @Positive
    private Integer price;
    
    private Timestamp deletedAt;

    private BookMst bookMst;

    private List<RentalManage> rentalManages;

}
