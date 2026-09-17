package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {
    private int id;
    private String memberNumber;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate membershipDate;
    private boolean active;
    private LocalDateTime createdAt;

    public Member() {}

    public Member(String memberNumber, String fullName, String email,
                  String phone, String address) {
        this.memberNumber = memberNumber;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.membershipDate = LocalDate.now();
        this.active = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getMembershipDate() { return membershipDate; }
    public void setMembershipDate(LocalDate membershipDate) { this.membershipDate = membershipDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return fullName + " [" + memberNumber + "]";
    }
}
