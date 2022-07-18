package models.coreData;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CORE_FYLO_EKSETASHS_SYMVASHS", schema = "MIS_2020_TRAINING", catalog = "")
public class CoreFyloEksetasisSymvashsEntity {
    long id;
    private Date verification_date;
    private Integer verified_user;
    private Integer active;
    private String code;
    private String shmeio;
    private String status;
    private String category;

    @Id
    @GeneratedValue(generator = "CoreFyloEksetasisSymvashsSeq")
    @SequenceGenerator(name = "CoreFyloEksetasisSymvashsSeq", sequenceName = "CORE_FYLO_EKSETASIS_SYMVAS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "VERIFICATION_DATE")
    public Date getVerification_date() {
        return verification_date;
    }

    public void setVerification_date(Date verification_date) {
        this.verification_date = verification_date;
    }
    @Basic
    @Column(name = "VERIFIED_USER")
    public Integer getVerified_user() {
        return verified_user;
    }

    public void setVerified_user(Integer verified_user) {
        this.verified_user = verified_user;
    }
    @Basic
    @Column(name = "ACTIVE")
    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    @Basic
    @Column(name = "SHMEIO")
    public String getShmeio() {
        return shmeio;
    }

    public void setShmeio(String shmeio) {
        this.shmeio = shmeio;
    }
    @Basic
    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Basic
    @Column(name = "CATEGORY")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}




