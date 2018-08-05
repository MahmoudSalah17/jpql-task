package com.sumerge.grad.program.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import static com.sumerge.grad.program.jpa.constants.Constants.SCHEMA_NAME;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "STUDENTS", schema = SCHEMA_NAME)
public class Student implements Serializable {

    private static final long serialVersionUID = -1125663317159921569L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ROLL_NUMBER")
    private String rollNumber;

    @Column(name = "GENDER")
    private Integer gender;

    @Column(name = "dob")
    private Date dob;

    @JoinColumn(name = "STUDENT_ID")
    @OneToMany(fetch = LAZY, cascade = ALL)
    private Collection<Address> addresses;

    @JsonIgnore
    @ManyToMany(fetch = LAZY, targetEntity = Course.class, mappedBy = "students")
    private Collection<Course> courses;

    public Collection<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }

    public Collection<Course> getCourses() {
        return courses;
    }

    public void setCourses(Collection<Course> courses) {
        this.courses = courses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void addAddress(Address address){addresses.add(address);}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
