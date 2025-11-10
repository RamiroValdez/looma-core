package com.amool.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double rating;

    public RatingEntity(Long workId, Long userId, Double rating) {
        this.workId = workId;
        this.userId = userId;
        this.rating = rating;
    }
}
