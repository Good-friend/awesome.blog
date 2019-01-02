package org.awesome.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.awesome.models.Comment;
import org.awesome.models.GuestReply;
import org.awesome.models.OperationFlow;
import org.awesome.models.UpdateBlog;
import org.awesome.vo.UserCommentVo;

import java.util.List;

public interface IMongoService {

    List<UpdateBlog> queryUpdateBlogList();

    void saveUpdateBlog(UpdateBlog updateBlog);

    void saveOperationFlow(OperationFlow operationFlow);

    List<Comment> queryCommentBySerialNumber(String serialNumber);

    void saveComment(Comment comment);

    UpdateResult updateThumbUp(String id);

    List<OperationFlow> queryOperationFlow(String action,String target,String who,String ip);

    DeleteResult delComment(String id);

    List<Comment> queryUserCommentInfo(String username);

    void saveGuestReply(GuestReply guestReply);

    List<GuestReply> queryGuestReplyList(String dealStatus);
}
