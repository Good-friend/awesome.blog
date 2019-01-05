package org.awesome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.awesome.mapper.FavoriteMapper;
import org.awesome.models.Favorite;
import org.awesome.service.IFavoriteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FavoriteService implements IFavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public void addFavorites(List<Favorite> favorites) {
        for (Favorite favorite : favorites) {
            favoriteMapper.insert(favorite);
        }
    }

    @Override
    public void deleteFavorites(List<Integer> ids) {
        favoriteMapper.deleteBatchIds(ids);
    }

    @Override
    public List<Favorite> getFavoritesByUsername(int pageIndex, int pageSize, String username) {
        Page page = new Page(pageIndex, pageSize);
        return favoriteMapper.selectPage(page, new QueryWrapper<Favorite>().eq("username", username)).getRecords();
    }
}
