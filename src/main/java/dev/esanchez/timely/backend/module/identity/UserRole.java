package dev.esanchez.timely.backend.module.identity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public UserRole(){}

    public UserRole(User user, Role role){
        this.user = user;
        this.role = role;
        this.id = new UserRoleId(user.getUserId(), role.getRoleId());

    }
    public UserRoleId getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public void updateUser(User user) {
        this.user = user;
    }
    public Role getRole() {
        return role;
    }
    public void updateRole(Role role) {
        this.role = role;
    }

}
