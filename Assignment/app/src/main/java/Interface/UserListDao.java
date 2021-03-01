package Interface;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import Model.ProfileDetails;

@Dao
public interface UserListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserList(ProfileDetails subjectListModel);

    @Query("DELETE FROM User_details")
    void deleteAll();

    @Query("SELECT * from User_details")
    List<ProfileDetails> getAllUserList();

    @Query("SELECT * from User_details")
    LiveData<List<ProfileDetails>> getAllByLiveDataList();

    @Query("SELECT * FROM User_details ORDER BY user_id DESC LIMIT 1")
    //@Query("SELECT Max(user_id),total_Pages,pageNo from User_details")
    List<ProfileDetails> getLastUserList();
}
