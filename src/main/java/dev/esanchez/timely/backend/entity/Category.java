package dev.esanchez.timely.backend.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id ", nullable = false)
    private Long categoryId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    public Category() {

    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
