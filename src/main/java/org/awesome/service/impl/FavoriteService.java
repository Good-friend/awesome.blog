package org.awesome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.awesome.mapper.FavoriteMapper;
import org.awesome.models.Favorite;
import org.awesome.service.IFavoriteService;
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
}
