package models;
public class User {
    private String user_id;
    private String password;
    private String first_name;
    private String second_name;
    private String phone_number;
    private Float fines;
    public User(String user_id, String first_name, String second_name,String phone_number,  Float fines) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.second_name = second_name;
        this.phone_number=phone_number;
        this.fines=fines;
    }
    @Override
    public String toString() {
        return "\n~~~User INFO~~~\n\nUser ID: "+ user_id + "\nFirst name: "+first_name+"\nSecond name: "+second_name+"\nPhone number: "+phone_number;
    }

    public String getUserId() {
        return user_id;
    }

}
