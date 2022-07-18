package models.coreData;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "PRJ_TDE_ATTACHMENTS", schema = "MIS_2020_TRAINING", catalog = "")
public class PrjTdeAttachmentsEntity {
    private long id;

    private String title;
    private String comments;
    private long core_attachments_details_id;
    private long prj_tde_id;


    @Id
    @GeneratedValue(generator = "PrjTdeAttachementsSeq")
    @SequenceGenerator(name = "PrjTdeAttachementsSeq", sequenceName = "PRJ_TDE_ATTACHMENTS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "COMMENTS")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Basic
    @Column(name = "CORE_ATTACHMENTS_DETAILS_ID")
    public long getCore_attachments_details_id() {
        return core_attachments_details_id;
    }

    public void setCore_attachments_details_id(long core_attachments_details_id) {
        this.core_attachments_details_id = core_attachments_details_id;
    }

    @Basic
    @Column(name = "PRJ_TDE_ID")
    public long getPrj_tde_id() {
        return prj_tde_id;
    }

    public void setPrj_tde_id(long prj_tde_id) {
        this.prj_tde_id = prj_tde_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrjTdeAttachmentsEntity that = (PrjTdeAttachmentsEntity) o;
        return id == that.id &&
                Objects.equals(comments, that.comments) &&
                Objects.equals(title, that.title) &&
                Objects.equals(core_attachments_details_id, that.core_attachments_details_id) &&
                Objects.equals(prj_tde_id, that.prj_tde_id);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comments, title, core_attachments_details_id, prj_tde_id);
    }
}

