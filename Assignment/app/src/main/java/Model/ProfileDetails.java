package Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="User_details")
public class ProfileDetails {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_Name() {
        return first_Name;
    }

    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    public String getLast_Name() {
        return last_Name;
    }

    public void setLast_Name(String last_Name) {
        this.last_Name = last_Name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    @PrimaryKey
    @ColumnInfo(name="user_id")
    int id;
    @ColumnInfo(name="email")
    String email;
    @ColumnInfo(name="first_Name")
    String first_Name;
    @ColumnInfo(name="last_Name")
    String last_Name;
    @ColumnInfo(name="avatar")
    String avatar;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotal_Pages() {
        return total_Pages;
    }

    public void setTotal_Pages(int total_Pages) {
        this.total_Pages = total_Pages;
    }

    @ColumnInfo(name="pageNo")
    int pageNo;

    @ColumnInfo(name="total_Pages")
    int total_Pages;
}
