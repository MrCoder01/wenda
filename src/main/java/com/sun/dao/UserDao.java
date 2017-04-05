package com.sun.dao;

import com.sun.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * Created by ty on 2017/3/28.
 */
@Repository
@Mapper
public interface UserDao {

    String TABLE_NAME = " user ";
    String INSET_FIELDS = " name,password,salt,head_url ";
    String SELECT_FIELDS = " id," + INSET_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSET_FIELDS, ")",
            "values(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    User getById(int id);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where name=#{name}"})
    User selectByName(String name);

    @Update({"update ",TABLE_NAME," set password=#{password} where id=#{id}"})
    void updatePassWord(User user);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);
}
