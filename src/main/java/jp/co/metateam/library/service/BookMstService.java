package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.constants.Constants;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.repository.BookMstRepository;
import jp.co.metateam.library.repository.StockRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;
    private final StockRepository stockRepository;
    
    @Autowired
    public BookMstService(BookMstRepository bookMstRepository, StockRepository stockRepository){
        this.bookMstRepository = bookMstRepository;
        this.stockRepository = stockRepository;
    }

    public List<BookMst> findAll() {
        return this.bookMstRepository.findAll();
    }

    public Optional<BookMst> findById(Long id) {
        return this.bookMstRepository.findById(id);
    }
    
    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findAll();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            List<Stock> stockCount = this.stockRepository.findByBookMstIdAndStatus(book.getId(), Constants.STOCK_AVAILABLE);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDto.setStockCount(stockCount.size());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }
    
    @Transactional
    public void save(BookMstDto bookMstDto) {
        try {
            BookMst book = new BookMst();

            book.setTitle(bookMstDto.getTitle());
            book.setIsbn(bookMstDto.getIsbn());

            // データベースへの保存
            this.bookMstRepository.save(book);
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Transactional
    public void update(Long id, BookMstDto bookMstDto) throws Exception {
        try {
            // 既存レコード取得
            BookMst updateTargetBook = this.bookMstRepository.findById(id).orElse(null);
            if (updateTargetBook == null) {
                throw new Exception("BookMst record not found.");
            }

            updateTargetBook.setTitle(bookMstDto.getTitle());
            updateTargetBook.setIsbn(bookMstDto.getIsbn());

            // データベースへの保存
            this.bookMstRepository.save(updateTargetBook);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean isValidTitle(String title, Model model) {
        if (StringUtils.isEmpty(title)) {
            model.addAttribute("errTitle", "書籍タイトルは必須");
            return true;
        }
        return false;
    }

    public boolean isValidIsbn(String isbn, Model model) {
        if (StringUtils.isEmpty(isbn) || isbn.length() != 13) {
            model.addAttribute("errISBN", "ISBNは13文字で入力してください");
            return true;
        }
        return false;
    }

    // public boolean isValidTitle(String title, RedirectAttributes ra) {
    //     if (StringUtils.isEmpty(title)) {
    //         ra.addFlashAttribute("errTitle", "書籍タイトルは必須");
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean isValidIsbn(String isbn, RedirectAttributes ra) {
    //     if (StringUtils.isEmpty(isbn) || isbn.length() != 13) {
    //         ra.addFlashAttribute("errISBN", "ISBNは13文字で入力してください");
    //         return true;
    //     }
    //     return false;
    // }
}



