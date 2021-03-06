package org.awesome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.awesome.models.User;
import org.awesome.vo.UserBasicInfoVo;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    @Update("update t_user set nickname = #{nickname},sex = #{sex},city = #{city},description = #{description},email = #{email} WHERE username = #{username}")
    int updateUserBasicInfo(UserBasicInfoVo userBasicInfoVo);


    @Update("update t_user set password = #{param2} WHERE username = #{param1}")
    int updateUserPassword(String username,String newPassword);

    @Update("update t_user set head_portrait_url = #{param2} WHERE username = #{param1}")
    int updateUserHeadImg(String username, String imgUrl);

    @Select("select * from t_user order by create_date desc")
    List<User> queryAllUser();

    @Update("update t_user set user_status = #{param2} WHERE username = #{param1}")
    int updateUserStatus(String username,String status);
}
