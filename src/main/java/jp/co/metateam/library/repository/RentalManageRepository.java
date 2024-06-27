package jp.co.metateam.library.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.values.RentalStatus;

@Repository
public interface RentalManageRepository extends JpaRepository<RentalManage, Long> {
    List<RentalManage> findAll();

    Optional<RentalManage> findById(Long id);

    // 貸出編集
    @Query("SELECT COUNT(rm) FROM RentalManage rm " +
            " WHERE rm.stock.id = ?1 AND rm.id != ?2 AND rm.status IN (0, 1)")
    long countByStockIdAndStatusIn(String stockid, Long id);

    @Query("SELECT COUNT(rm) FROM RentalManage rm " +
            " WHERE rm.stock.id = ?1 AND rm.status IN (0, 1) AND rm.id != ?2 " +
            " AND (rm.expectedRentalOn > ?3 AND rm.expectedReturnOn < ?4)")
    long countByStockIdAndStatusAndDateIn(String stockid, Long id, Date expectedReturnOn, Date expectedRentalOn);

    // 貸出登録
    @Query("SELECT COUNT(rm) FROM RentalManage rm " +
            " WHERE rm.stock.id = ?1 AND rm.status IN (0, 1)")
    long countByIdAndStatusIn(String stockid);

    @Query("SELECT COUNT(rm) FROM RentalManage rm " +
            " WHERE rm.stock.id = ?1 AND rm.status IN (0, 1) " +
            " AND (rm.expectedRentalOn > ?2 AND rm.expectedReturnOn < ?3)")
    long countByIdAndStatusAndDateIn(String stockid, Date expectedReturnOn, Date expectedRentalOn);

    //在庫カレンダーで使用
    //貸出待ちの貸出情報(貸出ステータス・選択した日付・在庫管理番号)
    @Query("SELECT COUNT (rm) FROM RentalManage rm WHERE rm.status = 0 AND (rm.expectedRentalOn <= ?1 AND rm.expectedReturnOn >= ?1) AND rm.stock.id IN ?2")
    long scheduledRentaWaitData(Date day, List<String>stock_id);

    //貸出中の貸出情報(貸出ステータス・選択した日付・在庫管理番号)
    @Query("SELECT COUNT (rm) FROM RentalManage rm WHERE rm.status = 1 AND (rm.rentaledAt <= ?1 AND rm.expectedReturnOn >= ?1) AND rm.stock.id IN ?2")
    long scheduledRentalingData(Date day, List<String>stock_id);

}
