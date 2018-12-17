package org.awesome.service;

import org.awesome.models.Catalogue;
import org.awesome.vo.CatalogueVo;

import java.util.List;
import java.util.Map;

public interface ICatalogueService {

    List<CatalogueVo> queryCatalogue(Map<String,String> params);

    CatalogueVo queryConnotationDetail(String serialNumber);


}
