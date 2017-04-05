package com.sun.dao;

import com.sun.model.Question;
import com.sun.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ty on 2017/3/28.
 */
@Repository
@Mapper
public interface QuestionDao {

    String TABLE_NAME = " question ";
    String INSET_FIELDS = " title,content,user_id,created_date,comment_count ";
    String SELECT_FIELDS = " id, " + INSET_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSET_FIELDS, ")",
            "values(#{title},#{content},#{userId},#{createdDate},#{commentCount})"})
    int addQuestion(Question question);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    Question getById(int id);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(int id);

    List<Question> selectQuestion(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"update question set comment_count = #{count} where id=#{id}"})
    //参数只有一个时，可以省略@Param，@Param可以为参数取别名
    int updataCount(@Param("id") int id,@Param("count") int count);
}
