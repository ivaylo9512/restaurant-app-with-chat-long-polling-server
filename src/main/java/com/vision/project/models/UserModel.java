package com.vision.project.models;

import com.vision.project.models.specs.RegisterSpec;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant")
    private Restaurant restaurant;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File profileImage;

    @Column(name = "is_enabled")
    private boolean isEnabled = false;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String role;
    private String password;
    private String firstName;
    private String lastName;
    private int age;
    private String country;

    public UserModel(){

    }

    public UserModel(String username, String email, String password, String firstName,
                     String lastName, int age, String country, String role, Restaurant restaurant) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
        this.role = role;
        this.restaurant = restaurant;
    }

    public UserModel(long id, String username, String email, String password, String role, String firstName,
                     String lastName, int age, String country, Restaurant restaurant) {
        this(username, email, password, firstName, lastName, age, country, role, restaurant);
        this.id = id;
    }

    public UserModel(RegisterSpec registerSpec, File profileImage, Restaurant restaurant, String role) {
        this(registerSpec.getUsername(), registerSpec.getEmail(), registerSpec.getPassword(),
                registerSpec.getFirstName(), registerSpec.getLastName(), registerSpec.getAge(), registerSpec.getCountry(), role, restaurant);
        setProfileImage(profileImage);
    }

    public UserModel(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserModel(String username, String email, String password, String role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserModel(long id, String username, String password, String role, Restaurant restaurant){
        this(username, password, role);
        this.id = id;
        this.restaurant = restaurant;
    }

    public UserModel(long id, String username, String password, String role){
        this(username, password, role);
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public File getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(File profileImage) {
        if(profileImage != null){
            this.profileImage = profileImage;
            profileImage.setOwner(this);
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
