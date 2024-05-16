package jp.co.metateam.library.service;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.repository.AccountRepository;
import jp.co.metateam.library.repository.RentalManageRepository;
import jp.co.metateam.library.repository.StockRepository;
import jp.co.metateam.library.values.RentalStatus;

@Service
public class RentalManageService {

    private final AccountRepository accountRepository;
    private final RentalManageRepository rentalManageRepository;
    private final StockRepository stockRepository;

    @Autowired
    public RentalManageService(
            AccountRepository accountRepository,
            RentalManageRepository rentalManageRepository,
            StockRepository stockRepository) {
        this.accountRepository = accountRepository;
        this.rentalManageRepository = rentalManageRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public List<RentalManage> findAll() {
        List<RentalManage> rentalManageList = this.rentalManageRepository.findAll();

        return rentalManageList;
    }

    @Transactional
    public RentalManage findById(Long id) {
        return this.rentalManageRepository.findById(id).orElse(null);
    }

    // 貸出編集のリポジトリの内容を呼び出す
    @Transactional
    public long countByStockIdAndStatusIn(String stockid, Long id) {
        return this.rentalManageRepository.countByStockIdAndStatusIn(stockid, id);
    }

    @Transactional
    public long countByStockIdAndStatusAndDateIn(String stockid, Long id, Date expectedRentalOn,
            Date expectedReturnOn) {
        return this.rentalManageRepository.countByStockIdAndStatusAndDateIn(stockid, id, expectedRentalOn,
                expectedReturnOn);
    }

    // 貸出登録のリポジトリの内容を呼び出す
    @Transactional
    public long countByIdAndStatusIn(String stockid) {
        return this.rentalManageRepository.countByIdAndStatusIn(stockid);
    }

    @Transactional
    public long countByIdAndStatusAndDateIn(String stockid, Date expectedRentalOn, Date expectedReturnOn) {
        return this.rentalManageRepository.countByIdAndStatusAndDateIn(stockid, expectedRentalOn, expectedReturnOn);
    }

    @Transactional
    public void save(RentalManageDto rentalManageDto) throws Exception {
        try {
            Account account = this.accountRepository.findByEmployeeId(rentalManageDto.getEmployeeId()).orElse(null);
            if (account == null) {
                throw new Exception("Account not found.");
            }

            Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);
            if (stock == null) {
                throw new Exception("Stock not found.");
            }

            RentalManage rentalManage = new RentalManage();
            rentalManage = setRentalStatusDate(rentalManage, rentalManageDto.getStatus());

            rentalManage.setAccount(account);
            rentalManage.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
            rentalManage.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
            rentalManage.setStatus(rentalManageDto.getStatus());
            rentalManage.setStock(stock);

            // データベースへの保存
            this.rentalManageRepository.save(rentalManage);
        } catch (Exception e) {
            throw e;
        }
    }

    private RentalManage setRentalStatusDate(RentalManage rentalManage, Integer status) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (status == RentalStatus.RENTALING.getValue()) {
            rentalManage.setRentaledAt(timestamp);
        } else if (status == RentalStatus.RETURNED.getValue()) {
            rentalManage.setReturnedAt(timestamp);
        } else if (status == RentalStatus.CANCELED.getValue()) {
            rentalManage.setCanceledAt(timestamp);
        }

        return rentalManage;
    }

    @Transactional
    public void update(Long id, RentalManageDto rentalManageDto) throws Exception {
        try {
            // 既存レコード取得
            RentalManage updateTargetRental = this.rentalManageRepository.findById(id).orElse(null);
            if (updateTargetRental == null) {
                throw new Exception("RentalManage record not found.");
            }

            Account account = this.accountRepository.findByEmployeeId(rentalManageDto.getEmployeeId()).orElse(null);
            if (account == null) {
                throw new Exception("Account not found.");
            }

            Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);
            if (stock == null) {
                throw new Exception("Stock not found.");
            }

            updateTargetRental.setId(rentalManageDto.getId());
            updateTargetRental.setAccount(account);
            updateTargetRental.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
            updateTargetRental.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
            updateTargetRental.setStatus(rentalManageDto.getStatus());
            updateTargetRental.setStock(stock);

            // データベースへの保存
            this.rentalManageRepository.save(updateTargetRental);
        } catch (Exception e) {
            throw e;
        }
    }
}