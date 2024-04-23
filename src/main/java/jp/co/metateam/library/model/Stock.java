package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 在庫管理
 */
@Entity
@Table(name = "Stocks")
public class Stock {

    /** 在庫管理番号 */
    @Id
    @Column(name = "id")
    private String id;

    /** 在庫ステータス */
    @Column(name = "status", nullable = false)
    private Integer status;

    /** 購入金額 */
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private BookMst bookMst;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RentalManage> rentalManages = new ArrayList<>();

    /** Getters */

    public String getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getPrice() {
        return price;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public BookMst getBookMst() {
        return bookMst;
    }

    public List<RentalManage> getRentalManages() {
        return rentalManages;
    }

    /** Setters */

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setBookMst(BookMst bookMst) {
        this.bookMst = bookMst;
    }

    public void setRentalManages(List<RentalManage> rentalManages) {
        this.rentalManages = rentalManages;
    }
}
