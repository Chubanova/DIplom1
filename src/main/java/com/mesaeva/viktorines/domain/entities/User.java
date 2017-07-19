package com.mesaeva.viktorines.domain.entities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(unique = true, nullable = false)
    private String login;

    private String hashpass;

    private String username;

    private Integer enabled = 1;

    private int roleId;

    private Integer ownerId;

    public User() {
        //Default constructor
    }

    public Integer getOwnerId() {
        return ownerId == null ? 1 : ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer id) {
        this.idUser = id;
    }

    public String getHashpass() {
        return hashpass;
    }

    public void setHashpass(String password) {
        this.hashpass = new BCryptPasswordEncoder().encode(password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String name) {
        this.login = name;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

}