package org.awesome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.SelectProvider;
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


}
