package org.awesome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.awesome.models.Favorite;
import org.awesome.sql.QuerySql;
import org.awesome.vo.FavoriteVo;

import java.util.List;

public interface FavoriteMapper extends BaseMapper<Favorite> {

    @SelectProvider(type=QuerySql.class,method="getFavoritesByUsername")
    List<FavoriteVo> getFavoritesByUsername(String username);

    @Select("select * from t_favorite where serial_number = #{param1} and username = #{param2}")
    Favorite queryFavorite(String serialNumber,String username);

}
