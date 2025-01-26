package com.example.paf_ws7.controllers;

import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.paf_ws7.model.Review;
import com.example.paf_ws7.repo.ReviewRepo;
import com.mongodb.client.result.UpdateResult;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewRepo reviewRepo;

    @PostMapping
    public ResponseEntity<String> postReview(@RequestBody MultiValueMap<String, String> form) {
        try {
            // validate form
            String name = form.getFirst("name");
            String ratingStr = form.getFirst("rating");
            String gameId = form.getFirst("gameId");
            String comment = form.getFirst("comment");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }

            if (ratingStr == null || ratingStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Rating is required");
            }

            if (gameId == null || gameId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Game ID is required");
            }

            // parse and validate rating
            int rating;
            try {
                rating = Integer.parseInt(ratingStr);
                if (rating < 0 || rating > 10) {
                    return ResponseEntity.badRequest().body("Rating must be between 0 and 10");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Rating must be a number");
            }

            // create review object
            Review review = new Review();
            review.setUser(name);
            review.setRating(rating);
            review.setGameId(gameId);
            review.setComment(comment);

            // insert review
            Document result = reviewRepo.insertReview(review);
            return ResponseEntity.ok(result.getObjectId("_id").toHexString());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing review: " + e.getMessage());
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable String reviewId,
            @RequestBody Map<String, Object> payload) {
        try {
            // validate payload
            if (!payload.containsKey("rating")) {
                return ResponseEntity.badRequest().body("Rating is required");
            }

            // parse and validate rating
            int rating;
            try {
                rating = Integer.parseInt(payload.get("rating").toString());
                if (rating < 0 || rating > 10) {
                    return ResponseEntity.badRequest().body("Rating must be between 0 and 10");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Rating must be a number");
            }

            String comment = payload.containsKey("comment") ? payload.get("comment").toString() : null;

            // update
            UpdateResult result = reviewRepo.updateReview(reviewId, comment, rating);
            return ResponseEntity.ok(
                    String.format("Updated %d review(s)", result.getModifiedCount()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating review: " + e.getMessage());
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<String> getReview(@PathVariable String reviewId) {
        try {
            Document doc = reviewRepo.getReview(reviewId);
            return ResponseEntity.ok(doc.toJson());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving review: " + e.getMessage());
        }
    }

    @GetMapping("/{reviewId}/history")
    public ResponseEntity<String> getReviewHistory(@PathVariable String reviewId) {
        try {
            Document doc = reviewRepo.getReviewHistory(reviewId);
            return ResponseEntity.ok(doc.toJson());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving review history: " + e.getMessage());
        }
    }
}
