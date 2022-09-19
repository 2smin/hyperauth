package com.tmax.hyperauth.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


@Entity
@Table(name = "USER_PROFILE")
public class UserProfile {

    @Id
    @Column(name = "ID")
    private String id;

    private String userId;
    private String sex;
    private String age;
    private String phoneNumber;
    private String job;

    public String getId(){return id;}

    public String getAge() {
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

    public String getUserId() {
        return userId;
    }

    public void setAge(String age) {
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
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
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(sex, that.sex) && Objects.equals(age, that.age) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(job, that.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, sex, age, phoneNumber, job);
    }
}
