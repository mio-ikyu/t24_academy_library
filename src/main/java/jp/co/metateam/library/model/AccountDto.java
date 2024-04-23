package jp.co.metateam.library.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * アカウントDTO
 * ※デフォルトのバリデーションメッセージの日本語化は、src\main\resources\messages.propertiesに記載。
 */
@Getter
@Setter
public class AccountDto {

    @NotEmpty(message = "社員番号は必須です")
    @Size(max = 50)
    private String employeeId;

    @NotEmpty(message = "氏名は必須です")
    @Size(max = 255)
    private String name;

    @NotEmpty(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が不正です")
    private String email;

    @NotEmpty(message = "パスワードは必須です")
    @Size(min = 5, message="パスワードは5文字以上で入力してください")
    private String password;

    @NotNull(message = "権限区分は必須です")
    @Min(0)
    @Max(1)
    private Integer authorizationType;
}
