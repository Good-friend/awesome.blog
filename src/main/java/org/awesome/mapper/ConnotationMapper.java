package org.awesome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.awesome.models.Connotation;
public interface ConnotationMapper extends BaseMapper<Connotation> {

    @Update("update t_connotation set content = #{param2} WHERE serial_number = #{param1}")
    int updateConnotation(String serialNumber,String content);

}
