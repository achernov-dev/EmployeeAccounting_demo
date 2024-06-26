package org.empacc.app.model;

public class Employee implements Item{
    private int id;
    private String firstName;
    private String lastName;
    private int deparmentId;
    private int titleId;

    public Employee(int id, String firstName, String lastName, int deparmentId, int titleId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deparmentId = deparmentId;
        this.titleId = titleId;
    }

    public Employee() {

    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
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

    public int getDeparmentId() {
        return deparmentId;
    }

    public void setDeparmentId(int deparmentId) {
        this.deparmentId = deparmentId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", deparmentId=" + deparmentId +
                ", titleId=" + titleId +
                '}';
    }

    public String getFullName() {
        return ( firstName == null ? "" : firstName + " ") + ( lastName == null ? "" : lastName);
    }
    @Override
    public boolean isNew() {
        return this.id <= 0;
    }

}
