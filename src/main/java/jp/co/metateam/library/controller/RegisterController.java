package jp.co.metateam.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.values.AuthorizationTypes;
import lombok.extern.slf4j.Slf4j;

/**
 * アカウント登録関連クラス
 */
@Slf4j
@Controller
public class RegisterController {

    private final AccountService accountService;

    @Autowired
    public RegisterController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("authorizationTypes", AuthorizationTypes.values());

        if (!model.containsAttribute("accountDto")) {
            model.addAttribute("accountDto", new AccountDto());
        }

        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute AccountDto accountDto, BindingResult result, RedirectAttributes ra) {
        try {
            Account emailExist = this.accountService.findByEmail(accountDto.getEmail());
            if(emailExist != null){
                result.rejectValue("email", "error.value", "登録済みのメールアドレスです");
                throw new Exception("Account already exists.");
            }

            Account employeeExist = this.accountService.findByEmployeeId(accountDto.getEmployeeId());
            if(employeeExist != null){
                result.rejectValue("employeeId", "error.value", "登録済みの社員番号です");
                throw new Exception("Account already exists.");
            }

            accountService.save(accountDto);

            return "redirect:login";
        } catch (Exception e) {
            log.error(e.getMessage());

            ra.addFlashAttribute("accountDto", accountDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.accountDto", result);

            return "redirect:register";
        }
    }
}