package com.example.campusevent;

public class StudentRegistration {
    public final long id;
    public final String name;
    public final String registrationNumber;
    public final String email;
    public final String phone;
    public final String programme;
    public final String eventName;

    public StudentRegistration(long id, String name, String registrationNumber, String email,
                               String phone, String programme, String eventName) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.phone = phone;
        this.programme = programme;
        this.eventName = eventName;
    }
}
