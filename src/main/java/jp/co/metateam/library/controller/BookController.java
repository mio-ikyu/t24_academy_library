package jp.co.metateam.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {
    
    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
        
        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }
    
    @PostMapping("/book/add")
    public String register(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra, Model model) {
        try {

            boolean validTitle = bookMstService.isValidTitle(bookMstDto.getTitle(), model);
            boolean validIsbn = bookMstService.isValidIsbn(bookMstDto.getIsbn(), model);
            
            if (validTitle || validIsbn) {
                model.addAttribute("bookMstDto", bookMstDto);
                return "book/add";
            }

            if (result.hasErrors()) {
                throw new Exception("Validation error.");
            }

            // 登録処理
            this.bookMstService.save(bookMstDto);
            
            return "redirect:/book/index";
        } catch (Exception e) {
            log.error(e.getMessage());

            ra.addFlashAttribute("bookMstDto", bookMstDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.bookMstDto", result);

            return "redirect:/book/add";
        }
    }

    @GetMapping("/book/{id}/edit")
    public String edit(@PathVariable("id") String id, Model model) {
        BookMst bookMst = this.bookMstService.findById(Long.valueOf(id)).orElse(null);
        BookMstDto bookMstDto = new BookMstDto();

        bookMstDto.setId(bookMst.getId());
        bookMstDto.setIsbn(bookMst.getIsbn());
        bookMstDto.setTitle(bookMst.getTitle());

        model.addAttribute("bookMstDto", bookMstDto);
        
        return "book/edit";
    }
    
    @PostMapping("/book/{id}/edit")
    public String update(@PathVariable("id") String id, @Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, Model model) {
        try {

            boolean validTitle = bookMstService.isValidTitle(bookMstDto.getTitle(), model);
            boolean validIsbn = bookMstService.isValidIsbn(bookMstDto.getIsbn(), model);

            if (validTitle || validIsbn) {
                model.addAttribute("bookMstDto", bookMstDto);
                return "book/edit";
            }

            if (result.hasErrors()) {
                throw new Exception("Validation error.");
            }

            // 更新処理
            this.bookMstService.update(Long.valueOf(id), bookMstDto);
            
            return "redirect:/book/index";
        } catch (Exception e) {
            log.error(e.getMessage());

            return "book/edit";
        }
    }
}
