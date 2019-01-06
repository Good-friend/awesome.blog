package org.awesome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.awesome.mapper.FavoriteMapper;
import org.awesome.models.Favorite;
import org.awesome.service.IFavoriteService;
import org.awesome.vo.CatalogueVo;
import org.awesome.vo.FavoriteVo;
import org.awesome.vo.RestResultVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FavoriteService implements IFavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public RestResultVo addFavorites(List<Favorite> favorites) {
        for (Favorite favorite : favorites) {
            favoriteMapper.insert(favorite);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", "");
    }

    @Override
    public RestResultVo deleteFavorites(List<Integer> ids) {
        favoriteMapper.deleteBatchIds(ids);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", "");
    }

    @Override
    public RestResultVo getFavoritesByUsername(int pageIndex, int pageSize, String username) {
        Page page = new Page(pageIndex, pageSize);
        List<Favorite> favorites = favoriteMapper.selectPage(page, new QueryWrapper<Favorite>().eq("username", username)).getRecords();
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", favorites);
    }

    @Override
    public void saveFavorite(Favorite favorite) throws Exception{
        if(favoriteMapper.insert(favorite) < 1){
            throw new Exception("添加收藏失败");
        }
    }

    @Override
    public void delFavorite(String id)throws Exception{
       if(favoriteMapper.deleteById(id)< 1){
            throw new Exception("取消收藏失败");
        }
    }

    @Override
    public List<FavoriteVo> getFavoritesByUsername(String username){
        return favoriteMapper.getFavoritesByUsername(username);
    }

    @Override
    public Favorite queryFavorite(String serialNumber,String username){
        return favoriteMapper.queryFavorite(serialNumber,username);
    }

}
