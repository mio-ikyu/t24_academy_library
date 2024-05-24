package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jp.co.metateam.library.values.RentalStatus;
import lombok.Getter;
import lombok.Setter;
import java.util.Optional;
import jp.co.metateam.library.values.RentalStatus;

/**
 * 貸出管理DTO
 */
@Getter
@Setter
public class RentalManageDto {

    private Long id;

    @NotEmpty(message = "在庫管理番号は必須です")
    private String stockId;

    @NotEmpty(message = "社員番号は必須です")
    private String employeeId;

    @NotNull(message = "貸出ステータスは必須です")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "貸出予定日は必須です")
    private Date expectedRentalOn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "返却予定日は必須です")
    public Date expectedReturnOn;

    private Timestamp rentaledAt;

    private Timestamp returnedAt;

    private Timestamp canceledAt;

    private Stock stock;

    private Account account;

    public Date expectedRental;

    // 貸出可否チェック
    public Optional<String> isStatusError(Integer preStatus) {
        if (preStatus == RentalStatus.RENT_WAIT.getValue() && this.status == RentalStatus.RETURNED.getValue()) {
            return Optional.of("「貸出待ち」から「返却済み」は選択できません");
        } else if (preStatus == RentalStatus.RENTALING.getValue() && this.status == RentalStatus.RENT_WAIT.getValue()) {
            return Optional.of("「貸出中」から「貸出待ち」には変更できません");
        } else if (preStatus == RentalStatus.RENTALING.getValue() && this.status == RentalStatus.CANCELED.getValue()) {
            return Optional.of("「貸出中」から「キャンセル」には変更できません");
        } else if (preStatus == RentalStatus.RETURNED.getValue() && this.status != RentalStatus.RETURNED.getValue()) {
            return Optional.of("「返却済み」から変更できません");
        } else if (preStatus == RentalStatus.CANCELED.getValue() && this.status != RentalStatus.CANCELED.getValue()) {
            return Optional.of("「キャンセル」から変更できません");
        }
        return Optional.empty();
    }

    // ステータス変更時の日付チェック
    public String isDateError(RentalManage rentalManage, RentalManageDto rentalManageDto) {
        // 現在の日時
        LocalDate nowDate = LocalDate.now(ZoneId.of("Asia/Tokyo"));

        // preが既に登録されている内容・postが現在入力されている内容
        Integer prestatus = rentalManage.getStatus();
        Integer poststatus = rentalManageDto.getStatus();

        // 貸出日と返却日をLocalDateに変換するためにインスタンス化させる
        LocalDate expectedRentalOn = rentalManageDto.getExpectedRentalOn().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();
        /*LocalDate expectedReturnOn = rentalManageDto.getExpectedReturnOn().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();*/

        // if文貸出待ち→貸出中
        if (prestatus == 0 && poststatus == 1) {
            if (!expectedRentalOn.equals(nowDate)) {
                return "現在の日付を入力してください。";
            }
        }
        /*if文貸出中→返却済み
        if (prestatus == 1 && poststatus == 2) {
            if (!expectedReturnOn.equals(nowDate)) {
                return "現在の日付を入力してください。";
            }
        }*/
        return null;
    }

    public String returnError(){
        if(expectedReturnOn.before(expectedRentalOn)){
        return "貸出日より前に設定してください";
     }
        return null;
    } 

}
