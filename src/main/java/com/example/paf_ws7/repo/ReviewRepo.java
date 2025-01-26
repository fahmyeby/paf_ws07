package com.example.paf_ws7.repo;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.example.paf_ws7.model.Review;
import com.mongodb.client.result.UpdateResult;

@Repository
public class ReviewRepo {
    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * POST /review
     * db.reviews.insert({
     * user:"<name>",
     * rating:<rating>,
     * comment:"<comment>",
     * ID:"<game_id>",
     * posted:<date>,
     * name:"<game_name>"
     * })
     */
    public Document insertReview(Review review) {
        // validation
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        if (review.getUser() == null || review.getUser().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (review.getRating() == null || review.getRating() < 0 || review.getRating() > 10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        if (review.getGameId() == null || review.getGameId().trim().isEmpty()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty");
        }

        // convert game id to int and query
        int gameId;
        try {
            gameId = Integer.parseInt(review.getGameId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid game ID format: " + review.getGameId());
        }

        // criteria for query
        Query gameQuery = Query.query(Criteria.where("gid").is(gameId));
        Document gameDoc = mongoTemplate.findOne(gameQuery, Document.class, "games");

        if (gameDoc == null) {
            Document rawQuery = new Document("gid", gameId);
            gameDoc = mongoTemplate.getCollection("games").find(rawQuery).first();

            if (gameDoc == null) {
                throw new IllegalArgumentException("Game not found with ID: " + gameId);
            }
        }

        // create review doc
        Document reviewDoc = new Document();
        reviewDoc.put("user", review.getUser());
        reviewDoc.put("rating", review.getRating());
        reviewDoc.put("gameId", String.valueOf(gameId));
        reviewDoc.put("posted", new Date());
        reviewDoc.put("name", gameDoc.getString("name"));

        // add comment if present
        if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
            reviewDoc.put("comment", review.getComment());
        }
        return mongoTemplate.insert(reviewDoc, "reviews");
    }

    /*
     * PUT /review/<reviewId> -- use path var
     * db.reviews.update(
     * { "_id": ObjectId("<review_id>") },
     * {
     * $push: {
     * edited: {
     * comment: "<comment>",
     * rating: <rating>,
     * posted: <date>
     * }
     * }
     * }
     * )
     */
    public UpdateResult updateReview(String reviewId, String comment, Integer rating) {
        // Validate inputs
        if (reviewId == null || reviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Review ID cannot be null or empty");
        }
        if (rating == null || rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }

        // Check if review exists
        ObjectId objectId;
        try {
            objectId = new ObjectId(reviewId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid review ID format");
        }

        Query reviewQuery = new Query(Criteria.where("_id").is(objectId));
        Document existingReview = mongoTemplate.findOne(reviewQuery, Document.class, "reviews");
        if (existingReview == null) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }

        // Create edit document
        Document editDoc = new Document();
        editDoc.put("rating", rating);
        editDoc.put("posted", new Date());
        if (comment != null && !comment.trim().isEmpty()) {
            editDoc.put("comment", comment);
        }

        // Update review
        Update updateOps = new Update().push("edited", editDoc);
        return mongoTemplate.updateFirst(reviewQuery, updateOps, "reviews");
    }

    /*
     * GET /review/<review_id>
     * db.reviews.findOne(
     * {"_id":ObjectId("<review_id>")}
     * )
     */

    public Document getReview(String reviewId) {
        // validate review id
        if (reviewId == null || reviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Review ID cannot be null or empty");
        }

        // get review
        ObjectId objectId;
        try {
            objectId = new ObjectId(reviewId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid review ID format");
        }

        Query query = new Query(Criteria.where("_id").is(objectId));
        Document reviewDoc = mongoTemplate.findOne(query, Document.class, "reviews");
        if (reviewDoc == null) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }

        // create response doc
        Document result = new Document();
        result.put("user", reviewDoc.getString("user"));
        result.put("gameId", reviewDoc.getString("gameId"));
        result.put("name", reviewDoc.getString("name"));
        result.put("posted", reviewDoc.getDate("posted"));
        result.put("timestamp", new Date());

        // edit
        List<Document> edits = reviewDoc.getList("edited", Document.class);
        if (edits != null && !edits.isEmpty()) {
            Document latestEdit = edits.get(edits.size() - 1);
            result.put("rating", latestEdit.getInteger("rating"));
            result.put("comment", latestEdit.getString("comment"));
            result.put("edited", true);
        } else {
            result.put("rating", reviewDoc.getInteger("rating"));
            result.put("comment", reviewDoc.getString("comment"));
            result.put("edited", false);
        }

        return result;
    }

    /*
     * GET /review/<review_id>/history
     * db.reviews.findOne(
     * {"_id:ObjectId("<review_id)"}
     * )
     */
    public Document getReviewHistory(String reviewId) {
        // validate review id
        if (reviewId == null || reviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Review ID cannot be null or empty");
        }

        // find review
        ObjectId objectId;
        try {
            objectId = new ObjectId(reviewId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid review ID format");
        }

        Query query = new Query(Criteria.where("_id").is(objectId));
        Document reviewDoc = mongoTemplate.findOne(query, Document.class, "reviews");
        if (reviewDoc == null) {
            throw new IllegalArgumentException("Review not found with ID: " + reviewId);
        }

        // add timestamp
        reviewDoc.put("timestamp", new Date());
        return reviewDoc;
    }
}
