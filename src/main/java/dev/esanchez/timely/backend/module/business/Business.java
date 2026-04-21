package dev.esanchez.timely.backend.module.business;
import dev.esanchez.timely.backend.module.categories.Category;
import dev.esanchez.timely.backend.module.location.CountryTimezone;
import dev.esanchez.timely.backend.module.identity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @Column(name = "name",nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="category_id", nullable = false)
    private Category category;

    @Column(name = "info")
    private String info;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "country_code", referencedColumnName = "country_code", nullable = false),
            @JoinColumn(name = "timezone_id", referencedColumnName = "timezone_id", nullable = false)
    })
    private CountryTimezone countryTimezone;


    public void updateInfo(String info) {
        this.info = info;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
