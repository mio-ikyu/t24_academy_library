package jp.co.metateam.library.values;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockStatus implements Values {
    RENT_AVAILABLE(0, "利用可")
    , RENT_NOT_AVAILABLE(1, "利用不可");

    private final Integer value;
    private final String text;  
}
