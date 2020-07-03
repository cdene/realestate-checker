package com.cdeneuve.realestate.infrastructure.source.postgres.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class TagEntity {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "tags_id_seq", sequenceName = "tags.tags_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_id_seq")
    private Long id;

    @Column(name = "tag")
    private String tag;
}
