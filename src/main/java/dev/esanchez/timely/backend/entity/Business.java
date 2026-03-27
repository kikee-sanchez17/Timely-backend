package dev.esanchez.timely.backend.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @Column(name = "name",nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="category_id", nullable = false)
    private Category category;

    @Column(name = "info")
    private String info;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "country_code", referencedColumnName = "country_code"),
            @JoinColumn(name = "timezone_id", referencedColumnName = "timezone_id")
    })
    private CountryTimezone countryTimezone;


}
