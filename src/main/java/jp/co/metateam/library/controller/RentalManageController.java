package jp.co.metateam.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.service.StockService;
import jp.co.metateam.library.service.BookMstService;
import jp.co.metateam.library.values.RentalStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PathVariable;
import jp.co.metateam.library.constants.Constants;
import jp.co.metateam.library.repository.RentalManageRepository;
import java.util.Optional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


/**
 * 貸出管理関連クラスß
 */
@Log4j2
@Controller
public class RentalManageController {

    private final BookMstService bookMstService;
    private final StockService stockService;
    private final AccountService accountService;
    private final RentalManageService rentalManageService;

    @Autowired
    public RentalManageController(
            AccountService accountService,
            RentalManageService rentalManageService,
            StockService stockService,
            BookMstService bookMstService) {
        this.accountService = accountService;
        this.rentalManageService = rentalManageService;
        this.stockService = stockService;
        this.bookMstService = bookMstService;
    }

    /**
     * 貸出一覧画面初期表示
     * 
     * @param model
     * @return
     */
    @GetMapping("/rental/index")
    public String index(Model model) {
        // 貸出管理テーブルから全件取得
        List<RentalManage> rentalManageList = this.rentalManageService.findAll();

        // 貸出一覧画面に渡すデータをmodelに追加
        model.addAttribute("rentalManageList", rentalManageList);

        // 貸出一覧画面に遷移
        return "rental/index";
    }

    @GetMapping("/rental/add")
    public String add(Model model, @ModelAttribute RentalManageDto rentalManageDto,
            @RequestParam(value = "expectedRentalOn", required = false) LocalDate expectedRentalOn,
            @RequestParam(value = "bookId", required = false) Long bookId) {
           // String rentalOn = expectedRentalOn.toString();
             // 社員の情報をすべて取得
        List<Account> accounts = this.accountService.findAll();

        // 在庫カレンダーから貸出登録画面に遷移
        if ( expectedRentalOn != null && bookId != null) {
            //LocalDate localDate = LocalDate.of(year, month, day);
           java.sql.Date choiceDate = java.sql.Date.valueOf(expectedRentalOn);
            // 選択した日付とタイトルをリストに格納
            List<Stock> availableStock = this.stockService.availableStockValues(choiceDate, bookId);
            // 在庫カレンダーリストに関する情報をHTMLのstockListに表示される
            model.addAttribute("stockList", availableStock);

            // 貸出登録画面に遷移後の情報
            rentalManageDto.setId(null);
            rentalManageDto.setEmployeeId(null);
            rentalManageDto.setExpectedRentalOn(choiceDate);
            rentalManageDto.setExpectedReturnOn(null);
            rentalManageDto.setStockId(null);
            rentalManageDto.setStatus(null);
            // 貸出予定日のみ表示
            //rentalManageDto.setExpectedRentalOn(rentalOn);

            // 貸出予定日と在庫管理番号のプルダウン表示
            model.addAttribute("rentalManageDto", rentalManageDto);
            
        } else {
        // 総利用可能在庫数
        List<Stock> stockList = this.stockService.findStockAvailableAll();

            // 貸出予定日と在庫管理番号のプルダウン表示
            model.addAttribute("stockList", stockList);
            if (!model.containsAttribute("rentalManageDto")) {

                model.addAttribute("rentalManageDto", new RentalManageDto());
    
            }    

        }
        // 社員番号と貸出ステータスのプルダウン表示
        model.addAttribute("accounts", accounts);
        model.addAttribute("rentalStatus", RentalStatus.values());

        return "rental/add";
    }

    @PostMapping("/rental/add")
    public String save(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result,
            RedirectAttributes ra, Model model) {
        // バリデーションチェック
        try {
            // 貸出登録のリポジトリ→サービスの内容を呼び出す
            String stockid = rentalManageDto.getStockId();

            // SQL①を呼び出す
            Long rentalSum = this.rentalManageService.countByIdAndStatusIn(stockid);

            // 上の内容をif文に通す
            if (rentalManageDto.getStatus() == 0 || rentalManageDto.getStatus() == 1) {
                if (rentalSum > 0) {
                    Date expectedRentalOn = rentalManageDto.getExpectedRentalOn();
                    Date expectedReturnOn = rentalManageDto.getExpectedReturnOn();
                    Long rentalNum = this.rentalManageService.countByIdAndStatusAndDateIn(stockid, expectedRentalOn,
                            expectedReturnOn);

                    if (rentalSum > rentalNum) {
                        String rentalError = "貸出できません。";
                        result.addError(new FieldError("rentalmanageDto", "expectedRentalOn", rentalError));
                        result.addError(new FieldError("rentalmanageDto", "expectedReturnOn", rentalError));
                    }
                }
            }

            if (result.hasErrors()) {
                throw new Exception("Validation error.");
            }

            String Error = rentalManageDto.returnError();
            if (Error != null) {
                result.addError(new FieldError("rentalManageDto", "expectedReturnOn", Error));
                throw new RuntimeException(Error);
            }
            // 登録処理
            this.rentalManageService.save(rentalManageDto);

            return "redirect:/rental/index";
        } catch (Exception e) {
            log.error(e.getMessage());

            // エラーが出た際の他の項目のプルダウン表示内容
            List<Account> accounts = this.accountService.findAll();
            List<Stock> stockList = this.stockService.findStockAvailableAll();

            model.addAttribute("accounts", accounts);
            model.addAttribute("stockList", stockList);
            model.addAttribute("rentalStatus", RentalStatus.values());

            ra.addFlashAttribute("rentalManageDto", rentalManageDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);

            return "rental/add";
        }
    }

    @GetMapping("/rental/{id}/edit")
    public String edit(@PathVariable("id") String id, Model model) {

        List<Account> accounts = this.accountService.findAll();
        List<Stock> stockList = this.stockService.findStockAvailableAll();

        model.addAttribute("accounts", accounts);
        model.addAttribute("stockList", stockList);
        model.addAttribute("rentalStatus", RentalStatus.values());

        if (!model.containsAttribute("rentalManageDto")) {

            // DBから編集する貸出管理番号のレコードを取得する
            RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));

            // レコードのデータを挿入する箱を作る
            RentalManageDto rentalManageDto = new RentalManageDto();

            // 箱にデータを移す
            rentalManageDto.setId(rentalManage.getId());
            rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
            rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
            rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());
            rentalManageDto.setStatus(rentalManage.getStatus());
            rentalManageDto.setStockId(rentalManage.getStock().getId());

            model.addAttribute("rentalManageDto", rentalManageDto);
        }

        return "rental/edit";

    }

    @PostMapping("/rental/{id}/edit")
    public String update(@PathVariable("id") String id, @Valid @ModelAttribute RentalManageDto rentalManageDto,
            BindingResult result, Model model, RedirectAttributes ra) {
        try {

            // 貸出編集のリポジトリ→サービスの内容を呼び出す
            String stockid = rentalManageDto.getStockId();

            // SQL①を呼び出す
            Long rentalSum = this.rentalManageService.countByStockIdAndStatusIn(stockid, Long.parseLong(id));

            // 上の内容をif文に通す
            if (rentalManageDto.getStatus() == 0 || rentalManageDto.getStatus() == 1) {
                if (!(rentalSum == 0)) {
                    Date expectedRentalOn = rentalManageDto.getExpectedRentalOn();
                    Date expectedReturnOn = rentalManageDto.getExpectedReturnOn();
                    Long rentalNum = this.rentalManageService.countByStockIdAndStatusAndDateIn(stockid,
                            Long.parseLong(id), expectedRentalOn, expectedReturnOn);

                    if (!(rentalSum == rentalNum)) {
                        String rentalError = "貸出できません。";
                        result.addError(new FieldError("rentalmanageDto", "expectedRentalOn", rentalError));
                        result.addError(new FieldError("rentalmanageDto", "expectedReturnOn", rentalError));
                    }
                }
            }
            // 変更前の貸出情報を取得
            RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
            // ステータス変更時の日付チェック
            String DateError = rentalManageDto.isDateError(rentalManage, rentalManageDto);
            if (DateError != null) {
                result.addError(new FieldError("rentalmanageDto", "expectedRentalOn", DateError));
                // result.addError(new FieldError("rentalManageDto","expectedReturnOn",
                // DateError));
                throw new RuntimeException(DateError);
            }

            if (result.hasErrors()) {
                throw new Exception("Validation error.");
            }

            // 変更前と変更後の貸出ステータスを取得
            Optional<String> validErrorOptional = rentalManageDto.isStatusError(rentalManage.getStatus());
            // Optionalが空でない場合のみエラーを処理する
            validErrorOptional.ifPresent(validError -> {
                if (!validError.isEmpty()) {
                    result.addError(new FieldError("rentalmanageDto", "status", validError));
                    throw new RuntimeException(validError);
                }
            });

            // 返却予定日が貸出予定日より前の日付になっているかチェック
            String Error = rentalManageDto.returnError();
            if (Error != null) {
                result.addError(new FieldError("rentalManageDto", "expectedReturnOn", Error));
                throw new RuntimeException(Error);
            }

            // 更新処理
            this.rentalManageService.update(Long.valueOf(id), rentalManageDto);

            return "redirect:/rental/index";

        } catch (Exception e) {
            log.error(e.getMessage());

            // 変更前の貸出情報を取得
            RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));

            rentalManageDto.setId(rentalManage.getId());
            rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
            rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
            rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());
            rentalManageDto.setStatus(rentalManage.getStatus());
            rentalManageDto.setStockId(rentalManage.getStock().getId());

            ra.addFlashAttribute("rentalManageDto", rentalManageDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);

            return String.format("redirect:/rental/%s/edit", id);
        }
    }

}