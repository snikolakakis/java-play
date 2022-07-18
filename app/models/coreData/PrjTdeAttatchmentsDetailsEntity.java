package models.coreData;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "PRJ_ATTATCHMENTS_DETAILS", schema = "MIS_2020_TRAINING", catalog = "")
public class PrjTdeAttatchmentsDetailsEntity {
    private long id;

    private long tde_att_id;
    private int category;
    private String label_el;
    private String label_en;
    private Date creation_date;


    @Id
    @GeneratedValue(generator = "PrjAttachementsDetSeq")
    @SequenceGenerator(name = "PrjAttachementsDetSeq", sequenceName = "PRJ_ATTACHMENTS_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    @Basic
    @JoinColumn(name = "TDE_ATT_ID")
    public long getTde_att_id() {
        return tde_att_id;
    }

    public void setTde_att_id(long tde_att_id) {
        this.tde_att_id = tde_att_id;
    }
    @Basic
    @Column(name = "CATEGORY")
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Basic
    @Column(name = "LABEL_EL")
    public String getLabel_el() {
        return label_el;
    }

    public void setLabel_el(String label_el) {
        this.label_el = label_el;
    }

    @Basic
    @Column(name = "LABEL_EN")
    public String getLabel_en() {
        return label_en;
    }

    public void setLabel_en(String label_en) {
        this.label_en = label_en;
    }

    @Basic
    @Column(name = "CREATION_DATE")
    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }
}
