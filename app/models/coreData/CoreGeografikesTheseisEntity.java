package models.coreData;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CORE_GEOGRAFIKES_THESEIS", schema = "MIS_2020_TRAINING", catalog = "")
public class CoreGeografikesTheseisEntity {
    private long id;

    private String code;
    private String title;
    private String epipedo;
    private String status;
    private Integer active;

    @Id
    @GeneratedValue(generator = "CoreGeografikesTheseisSeq")
    @SequenceGenerator(name = "CoreGeografikesTheseisSeq", sequenceName = "CORE_GEOGRAFIKES_THESEIS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "EPIPEDO")
    public String getEpipedo() {
        return epipedo;
    }

    public void setEpipedo(String epipedo) {
        this.epipedo = epipedo;
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
    @Column(name = "ACTIVE")
    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreGeografikesTheseisEntity that = (CoreGeografikesTheseisEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(title, that.title) &&
                Objects.equals(epipedo, that.epipedo) &&
                Objects.equals(status, that.status) &&
                Objects.equals(active, that.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, title, epipedo, status, active);
    }
}
