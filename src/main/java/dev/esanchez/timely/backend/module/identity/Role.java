package dev.esanchez.timely.backend.module.identity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "code", nullable = false)
    private String code;
}
