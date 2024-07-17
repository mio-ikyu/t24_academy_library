package jp.co.metateam.library.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.constants.Constants;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockByDayDto;
import jp.co.metateam.library.model.StockDto;
import jp.co.metateam.library.repository.BookMstRepository;
import jp.co.metateam.library.repository.StockRepository;
import jp.co.metateam.library.repository.RentalManageRepository;
import jp.co.metateam.library.model.CalendarDto;
import java.util.Date;

@Service
public class StockService {
    private final BookMstRepository bookMstRepository;
    private final StockRepository stockRepository;
    private final RentalManageRepository rentalManageRepository;

    @Autowired
    public StockService(BookMstRepository bookMstRepository, StockRepository stockRepository,
            RentalManageRepository rentalManageRepository) {
        this.bookMstRepository = bookMstRepository;
        this.stockRepository = stockRepository;
        this.rentalManageRepository = rentalManageRepository;
    }

    @Transactional
    public List<Stock> findAll() {
        List<Stock> stocks = this.stockRepository.findByDeletedAtIsNull();

        return stocks;
    }

    @Transactional
    public List<Stock> findStockAvailableAll() {
        List<Stock> stocks = this.stockRepository.findByDeletedAtIsNullAndStatus(Constants.STOCK_AVAILABLE);

        return stocks;
    }

    @Transactional
    public Stock findById(String id) {
        return this.stockRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<BookMst> findAllBookData() {
        List<BookMst> findAllBookData = this.bookMstRepository.findAllBookData(); // データ量が多いためListに一時的に保存
        return findAllBookData;
    }

    @Transactional
    public Long scheduledRentaWaitData(Date day, List<String> stock_id) {
        return this.rentalManageRepository.scheduledRentaWaitData(day, stock_id);
    }

    @Transactional
    public Long scheduledRentalingData(Date day, List<String> stock_id) {
        return this.rentalManageRepository.scheduledRentalingData(day, stock_id);
    }

    @Transactional
    public List<Stock> lendableBook(java.sql.Date choiceDate, Long id) {
        return this.stockRepository.lendableBook(choiceDate, id);
    }

    @Transactional
    public List<Stock> findAllAvailableStockData(Long book_id) {
        return this.stockRepository.findAllAvailableStockData(book_id);
    }

    @Transactional
    public List<Stock> bookStockAvailable(Long id) {
        return this.stockRepository.bookStockAvailable(id);
    }

    @Transactional
    public List<BookMst> bookSearch(String searchTitle) {
        return this.bookMstRepository.bookSearch(searchTitle);
    }

    @Transactional
    public void save(StockDto stockDto) throws Exception {
        try {
            Stock stock = new Stock();
            BookMst bookMst = this.bookMstRepository.findById(stockDto.getBookId()).orElse(null);
            if (bookMst == null) {
                throw new Exception("BookMst record not found.");
            }

            stock.setBookMst(bookMst);
            stock.setId(stockDto.getId());
            stock.setStatus(stockDto.getStatus());
            stock.setPrice(stockDto.getPrice());

            // データベースへの保存
            this.stockRepository.save(stock);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public void update(String id, StockDto stockDto) throws Exception {
        try {
            Stock stock = findById(id);
            if (stock == null) {
                throw new Exception("Stock record not found.");
            }

            BookMst bookMst = stock.getBookMst();
            if (bookMst == null) {
                throw new Exception("BookMst record not found.");
            }

            stock.setId(stockDto.getId());
            stock.setBookMst(bookMst);
            stock.setStatus(stockDto.getStatus());
            stock.setPrice(stockDto.getPrice());

            // データベースへの保存
            this.stockRepository.save(stock);
        } catch (Exception e) {
            throw e;
        }
    }

    // 日・曜日ごとのカレンダーリストの作成
    public List<Object> generateDaysOfWeek(int year, int month, LocalDate startDate, int daysInMonth) {
        List<Object> daysOfWeek = new ArrayList<>();
        for (int dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
            LocalDate date = LocalDate.of(year, month, dayOfMonth);
            DateTimeFormatter formmater = DateTimeFormatter.ofPattern("dd(E)", Locale.JAPANESE);
            daysOfWeek.add(date.format(formmater));
        }

        return daysOfWeek;
    }

    public List<CalendarDto> generateValues(Integer year, Integer month, Integer daysInMonth, String searchTitle)
            throws ParseException {
        List<CalendarDto> calendarList = new ArrayList<CalendarDto>();

        // 検索機能
        List<BookMst> bookData = new ArrayList<>();
        if (searchTitle != null) {
            bookData = bookSearch(searchTitle);
        } else {
            bookData = findAllBookData();
        }

        // 書籍分拡張ループ→順番に書籍名を呼び出して一つずつ内容を確認していく
        for (BookMst bookLoop : bookData) {
            // 書籍名と総利用可能在庫数と日付分ループで取得した日ごとの利用可能在庫数を格納するリストを作成
            CalendarDto calendarValue = new CalendarDto();
            // 書籍ループで取得したタイトルをリストに格納
            calendarValue.setTitle(bookLoop.getTitle());
            calendarValue.setBookId(bookLoop.getId());

            // StockのSQLを呼び出す(総利用可能在庫数)
            List<Stock> availableList = this.stockRepository.findAllAvailableStockData(bookLoop.getId());
            // Long型をString型に変換する
            String availableStockCount = String.valueOf(availableList.size());
            calendarValue.setAvailableStockCount(availableStockCount);

            // カウントして取得した在庫数をリストに格納
            List<String> stockIdList = new ArrayList<>();
            // 在庫数をカウントするためのループ
            for (Stock stock : availableList) {
                stockIdList.add(stock.getId());
            }

            // 現在日付の取得
            LocalDate today = LocalDate.now();

            List<StockByDayDto> stockCountByDay = new ArrayList<StockByDayDto>();

            // 日付分ループ
            for (int dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
                StockByDayDto stockByDayDto = new StockByDayDto();
                // 日付の作成
                LocalDate currentDateOfMonth = LocalDate.of(year, month, dayOfMonth);
                stockByDayDto.setExpectedRentalOn(currentDateOfMonth);
                // 過去日だった場合×を表示
                if (today != null && currentDateOfMonth.isBefore(today)) {
                    stockByDayDto.setStockCount("×");
                    stockCountByDay.add(stockByDayDto);
                    continue; // 次の日付へ
                }

                // 対象の日付を取得
                LocalDate localDate = LocalDate.of(year, month, dayOfMonth);

                // LocalDate型をDate型に変換する（Date型は時刻も含める）
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                // 日ごとの利用可能在庫数を定義する
                Long scheduledRentaWaitDataCount = scheduledRentaWaitData(date, stockIdList);
                Long scheduledRentalingDataCount = scheduledRentalingData(date, stockIdList);

                // 総利用在庫数から貸出待ちと貸出予定日を引く
                Long total = availableList.size() - (scheduledRentaWaitDataCount + scheduledRentalingDataCount);
                // 計算してtotalに入れたデータをString型のtotalValueに変換するかつ結果が0以下だった場合×にする
                String totalValue = (total <= 0) ? "×" : Long.toString(total);
                stockByDayDto.setStockCount(totalValue);
                stockCountByDay.add(stockByDayDto);

            }
            calendarValue.setStockCountByDay(stockCountByDay);
            calendarList.add(calendarValue);

        }
        return calendarList;
    }

    // 遷移後
    public List<Stock> availableStockValues(java.sql.Date choiceDate, Long bookId) {

        // 選択された日付とその在庫管理番号のリスト
        List<Stock> availableList = lendableBook(choiceDate, bookId);
        // 在庫管理番号によって総利用可能在庫数をまとめたリスト
        List<Stock> StockAvailable = this.stockRepository.bookStockAvailable(bookId);
        // 総利用可能在庫数から選択された日付とその在庫管理番号が重複しているデータを削除
        StockAvailable.removeAll(availableList);

        // 利用可能在庫数に置き換える
        return StockAvailable;
    }
}
