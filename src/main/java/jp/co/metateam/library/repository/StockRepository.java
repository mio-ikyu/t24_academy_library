package jp.co.metateam.library.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.Stock;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    List<Stock> findAll();

    List<Stock> findByDeletedAtIsNull();

    List<Stock> findByDeletedAtIsNullAndStatus(Integer status);

	Optional<Stock> findById(String id);
    
    List<Stock> findByBookMstIdAndStatus(Long book_id,Integer status);

    //総利用可能在庫数をカウントするためのSQL（書籍ID）
    @Query("SELECT st FROM Stock st WHERE st.status = 0 AND st.bookMst.id = ?1 AND st.deletedAt IS NULL")
    List<Stock> findAllAvailableStockData(Long book_id);

    //選択した日付が貸出予定日と返却予定日の期間が被っている在庫管理番号を取得
    @Query("SELECT DISTINCT st FROM Stock st LEFT OUTER JOIN RentalManage rm ON st.id = rm.stock.id WHERE ?1 BETWEEN rm.expectedRentalOn AND rm.expectedReturnOn AND st.bookMst.id = ?2 AND st.status = 0 AND deletedAt IS NULL")
    List<Stock> lendableBook(Date choiceDate, Long id);

    //プルダウンに表示するための在庫管理番号
    @Query("SELECT st FROM Stock st WHERE st.status = 0 AND st.bookMst.id = ?1 AND st.deletedAt IS NULL")
    List<Stock> bookStockAvailable(Long id);
}