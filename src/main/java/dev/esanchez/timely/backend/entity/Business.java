package dev.esanchez.timely.backend.entity;
import dev.esanchez.timely.backend.util.ValidationUtils;
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

    public Business(){}

    public Business(User user,
                    String name,
                    Category category,
                    String city,
                    CountryTimezone countryTimezone) {

        this.user = ValidationUtils.requireNonNull(user, "User cannot be null");
        this.name = ValidationUtils.validateText(name, "Name");
        this.category = ValidationUtils.requireNonNull(category, "Category cannot be null");
        this.city = ValidationUtils.validateText(city, "City");
        this.countryTimezone = ValidationUtils.requireNonNull(countryTimezone, "CountryTimezone cannot be null");
        this.isActive = true;
    }


    //Getters

    public Long getBusinessId() {
        return businessId;
    }

    public User getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getInfo() {
        return info;
    }

    public Boolean isActive() {
        return isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public CountryTimezone getCountryTimezone() {
        return countryTimezone;
    }

    //Setters

    public void updateName(String name) {
        this.name = ValidationUtils.validateText(name, "Name");

    }

    public void updateCity(String city) {
        this.city = ValidationUtils.validateText(city, "City");

    }

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
