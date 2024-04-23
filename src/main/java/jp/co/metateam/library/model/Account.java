package jp.co.metateam.library.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * アカウント
 */
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "authorization_type", nullable = false)
    private Integer authorizationType;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    public List<RentalManage> rentalManages;

    /** Getters */

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAuthorizationType() {
        return authorizationType;
    }

    public List<RentalManage> getRentalManages() {
        return rentalManages;
    }

    /** Setters */

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorizationType(Integer authorizationType) {
        this.authorizationType = authorizationType;
    }

    public void setRentalManages(List<RentalManage> rentalManages) {
        this.rentalManages = rentalManages;
    }
}
