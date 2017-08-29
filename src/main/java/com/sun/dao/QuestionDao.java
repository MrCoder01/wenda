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
//此接口名必须与xml对应的namespace命名空间一致
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

    //对应xml中id为selectQuestion的查询标签，此二者必须一致才能确定相应的sql
    List<Question> selectQuestion(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"update question set comment_count = #{count} where id=#{id}"})
    //参数只有一个时，可以省略@Param，@Param可以为参数取别名
    int updataCount(@Param("id") int id,@Param("count") int count);
}
