package org.empacc.app.model;

import java.util.Objects;

public class Department implements Item{
    private int id;
    private String name;
    private int bossId;
    private String phone;
    private String email;

    public Department(int id, String name, int bossId, String phone, String email) {
        this.id = id;
        this.name = name;
        this.bossId = bossId;
        this.phone = phone;
        this.email = email;
    }

    public Department() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBossId() {
        return bossId;
    }

    public void setBossId(int bossId) {
        this.bossId = bossId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email != null ? email.trim() : email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return id == that.id && bossId == that.bossId && Objects.equals(name, that.name) && Objects.equals(phone, that.phone) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bossId, phone, email);
    }

    @Override
    public boolean isNew() {
        return this.id <= 0;
    }
}
