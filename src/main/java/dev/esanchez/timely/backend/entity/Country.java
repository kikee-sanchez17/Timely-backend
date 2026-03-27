package dev.esanchez.timely.backend.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "code", nullable = false ,length = 2)
    private String code;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "flag_emoji")
    private String flagEmoji;

    public Country() {
    }

    public String getCode() {
        return code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getFlagEmoji() {
        return flagEmoji;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setFlagEmoji(String flagEmoji) {
        this.flagEmoji = flagEmoji;
    }
}
