package jp.co.metateam.library.values;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthorizationTypes implements Values {

    COMMON_USER(0, "一般")
    , AMDIN_USER(1, "管理者");

    private final Integer value;
    private final String text;  
}
