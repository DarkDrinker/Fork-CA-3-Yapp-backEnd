package restSecrurity.DOA.databaseDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import restSecrurity.DTO.PostDTO;
import restSecrurity.DTO.ReplyDTO;
import restSecrurity.exceptions.ApiException;
import restSecrurity.persistance.Category;
import restSecrurity.persistance.Post;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostDAO extends DAO<Post,Integer> {

    private static PostDAO instance;

    public PostDAO(boolean isTest) {
        super(Post.class,false);
    }
    public static PostDAO getInstance(boolean isTest){
        if(instance == null){
            instance = new PostDAO(isTest);
        }
        return instance;
    }

    public Set<PostDTO> getAllPostsByThreadId(int id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Set<PostDTO> result = new HashSet<>();
            TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p WHERE p.thread.id = :threadId", Post.class);
            query.setParameter("threadId", id);
            List<Post> mainPosts = query.getResultList();

            for (Post post : mainPosts) {
                PostDTO postDTO = new PostDTO(post);
                Set<ReplyDTO> replies = ReplyDAO.getAllRepliesByPostId(post.getId());
                postDTO.setReplies(replies);
                result.add(postDTO);
            }
            return result;
        }catch (Exception e){
            throw new ApiException(500,"No posts was found");
        }
    }
}
