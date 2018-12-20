package org.awesome.service;

import org.awesome.models.UpdateBlog;

import java.util.List;

public interface IMongoService {

    public List<UpdateBlog> queryUpdateBlogList();

    public void saveUpdateBlog(UpdateBlog updateBlog);

}
