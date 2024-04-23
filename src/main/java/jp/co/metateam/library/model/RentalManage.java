package jp.co.metateam.library.model;

import java.util.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 貸出管理
 */
@Entity
@Table(name = "RentalManage")
public class RentalManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 貸出ステータス */
    @Column(name = "status", nullable = false)
    private Integer status;

    /** 貸出予定日 */
    @Column(name = "expected_rental_on")
    private Date expectedRentalOn;

    /** 返却予定日 */
    @Column(name = "expected_return_on")
    private Date expectedReturnOn;

    /** 貸出日 */
    @Column(name = "rentaled_at")
    private Timestamp rentaledAt;

    /** 返却日 */
    @Column(name = "returned_at")
    private Timestamp returnedAt;

    /** キャンセル日 */
    @Column(name = "canceled_at")
    private Timestamp canceledAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private Account account;

    /** Getters */

    public Long getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getExpectedRentalOn() {
        return expectedRentalOn;
    }

    public Date getExpectedReturnOn() {
        return expectedReturnOn;
    }

    public Timestamp getRentaledAt() {
        return rentaledAt;
    }

    public Timestamp getReturnedAt() {
        return returnedAt;
    }

    public Timestamp getCanceledAt() {
        return canceledAt;
    }

    public Stock getStock() {
        return stock;
    }

    public Account getAccount() {
        return account;
    }
    
    /** Setters */

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setExpectedRentalOn(Date expectedRentalOn) {
        this.expectedRentalOn = expectedRentalOn;
    }

    public void setExpectedReturnOn(Date expectedReturnOn) {
        this.expectedReturnOn = expectedReturnOn;
    }

    public void setRentaledAt(Timestamp rentaledAt) {
        this.rentaledAt = rentaledAt;
    }

    public void setReturnedAt(Timestamp returnedAt) {
        this.returnedAt = returnedAt;
    }

    public void setCanceledAt(Timestamp canceledAt) {
        this.canceledAt = canceledAt;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
