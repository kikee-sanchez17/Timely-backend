package dev.esanchez.timely.backend.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRoleId implements Serializable {

    private long userId;
    private long roleId;

    public UserRoleId(){}

    public UserRoleId(long userId, long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
    public long getUserId() {
        return userId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleId)) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }



}
