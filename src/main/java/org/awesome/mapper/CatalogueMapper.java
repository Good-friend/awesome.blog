package org.awesome.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.awesome.models.Catalogue;
import org.awesome.sql.QuerySql;
import org.awesome.vo.CatalogueVo;

import java.util.List;
import java.util.Map;

public interface CatalogueMapper extends BaseMapper<Catalogue> {



    @SelectProvider(type=QuerySql.class,method="queryFirstPageSql")
    List<CatalogueVo> queryFirstPage(Map<String,String> params);

    @SelectProvider(type=QuerySql.class,method="queryConnotationDetailSql")
    CatalogueVo queryConnotationDetail(String serialNumber);

    @SelectProvider(type=QuerySql.class,method="queryCatalogueByParams")
    List<Catalogue> queryCatalogueByParams(Map<String,String> params);

    @Select("select * from t_catalogue where serial_number = #{serialNumber}")
    Catalogue queryCatalogueBySerialNumber(String serialNumber);

    @Update("update t_catalogue set seen_times = seen_times+1 WHERE serial_number = #{serialNumber}")
    int updateSeenTimes(String serialNumber);

    @Update("update t_catalogue set comment_times = comment_times+1 WHERE serial_number = #{serialNumber}")
    int updateCommentTimes(String serialNumber);

    @Update("update t_catalogue set publicity = #{param2} WHERE serial_number = #{param1}")
    int updateCataloguePublicity(String serialNumber,boolean publicity);

    @Update("update t_catalogue set best = #{param2} WHERE serial_number = #{param1}")
    int updateCatalogueBest(String serialNumber,boolean best);

    @Update("update t_catalogue set stick = #{param2} WHERE serial_number = #{param1}")
    int updateCatalogueStick(String serialNumber,boolean stick);

    @Update("update t_catalogue set status = #{param2} WHERE serial_number = #{param1}")
    int updateCatalogueStatus(String serialNumber,boolean status);

    @Update("update t_catalogue set type = #{param2},title = #{param3} WHERE serial_number = #{param1}")
    int updateCatalogueTitle(String serialNumber,String type,String title);

    @SelectProvider(type=QuerySql.class,method="countCatalogueAuthor")
    List<JSONObject> countCatalogueAuthor(String username,String publicity);
}
