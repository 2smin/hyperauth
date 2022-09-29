package com.tmax.hyperauth.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Objects;

@Entity(name = "USER_PROFILE")
@Table(name = "USER_PROFILE")
@NamedQueries({ @NamedQuery(name = "findByUserId", query = "select m from USER_PROFILE m where m.id = :id"),
        @NamedQuery(name = "updateUserProfile", query = "update USER_PROFILE set age = :age, sex = :sex, phoneNumber = :phoneNumber, job = :job where id = :id"),
        @NamedQuery(name = "updatePhoneNumber", query = "update USER_PROFILE set phoneNumber = :phoneNumber where id = :id")})
public class UserProfile {

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "email")
    private String email;
    @Column(name = "sex")
    private String sex;
    @Column(name = "age")
    private int age;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "job")
    private String job;

    public String getId(){return id;}

    public int getAge() {
        return age;
    }

    public String getJob() {
        return job;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", userId='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", job='" + job + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(sex, that.sex) && Objects.equals(age, that.age) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(job, that.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, sex, age, phoneNumber, job);
    }
}
