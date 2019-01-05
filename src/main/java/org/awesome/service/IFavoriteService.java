package org.awesome.service;

import org.awesome.models.Favorite;
import org.awesome.vo.RestResultVo;

import java.util.List;

public interface IFavoriteService {

    /**
     * 批量添加收藏
     *
     * @param favorites
     */
    RestResultVo addFavorites(List<Favorite> favorites);

    /**
     * 批量删除收藏
     *
     * @param ids
     */
    RestResultVo deleteFavorites(List<Integer> ids);

    /**
     * 分页查询用户收藏
     *
     * @param pageIndex
     * @param pageSize
     * @param username
     * @return
     */
    RestResultVo getFavoritesByUsername(int pageIndex, int pageSize, String username);
}
