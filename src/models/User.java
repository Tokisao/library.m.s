package models;
public class User {
    private String user_id;
    private String password;
    private String first_name;
    private String second_name;
    private String phone_number;
    private Float fines;
    private String role;
    public User(String user_id, String first_name, String second_name,String phone_number,  Float fines, String role) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.second_name = second_name;
        this.phone_number=phone_number;
        this.fines=fines;
        this.role=role;
    }
    @Override
    public String toString() {
        return "\n\nUser ID: "+ user_id + "\nFirst name: "+first_name+"\nSecond name: "+second_name+"\nPhone number: "+phone_number;
    }

    public String getUserId() {
        return user_id;
    }
    public String getRole() {
        return role;
    }
    public String getFirstName(){
        return first_name;
    }
    public String getLastName(){
        return second_name;
    }
    public String getPhoneNumber(){
        return phone_number;
    }
    public Float getFines(){
        return fines;
    }
}
