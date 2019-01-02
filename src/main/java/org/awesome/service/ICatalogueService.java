package org.awesome.service;

import org.awesome.models.Catalogue;
import org.awesome.models.OperationFlow;
import org.awesome.vo.CatalogueVo;
import org.awesome.vo.RestResultVo;

import java.util.List;
import java.util.Map;

public interface ICatalogueService {

    List<CatalogueVo> queryCatalogue(Map<String,String> params);

    CatalogueVo queryConnotationDetail(String serialNumber);

    List<Catalogue> queryCatalogueByParams(Map<String,String> params);

    int updateSeenTimes(String serialNumber);

    int updateCommentTimes(String serialNumber);

    int deleteCatalogueDetail(String serialNumber);

    int deleteConnotation(String serialNumber);

    int updateCataloguePublicity(String serialNumber,boolean publicity);

    int updateCatalogueBest(String serialNumber,boolean best);

    int updateCatalogueStick(String serialNumber,boolean stick);

    Catalogue queryCatalogueBySerialNumber(String serialNumber);
}
