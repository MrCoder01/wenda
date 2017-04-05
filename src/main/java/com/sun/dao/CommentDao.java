package com.sun.dao;

import com.sun.model.Comment;
import com.sun.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ty on 2017/3/28.
 */
@Repository
@Mapper
public interface CommentDao {

    String TABLE_NAME = " comment ";
    String INSET_FIELDS = " user_id,content,status,created_date,entity_id,entity_type ";
    String SELECT_FIELDS = " id, " + INSET_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSET_FIELDS, ")",
            "values(#{userId},#{content},#{status},#{createdDate},#{entityId},#{entityType})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}",
            " order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    Comment getById(int id);


    @Select({"select count(id) from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCountByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ",TABLE_NAME," where user_id=#{userId}"})
    int getUserCommentCount(int userId);

    @Select({"update ",TABLE_NAME," set status=#{status} where id=#{id}"})
    int updateCommentStatus(@Param("id") int id,@Param("status") int status);
}
