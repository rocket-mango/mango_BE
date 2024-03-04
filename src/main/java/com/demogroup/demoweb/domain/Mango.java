package com.demogroup.demoweb.domain;

import com.demogroup.demoweb.domain.dto.MangoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Mango extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;

    @ManyToOne
    @JoinColumn(name="uid")
    private User user;

    @Column(columnDefinition = "tinyint(1)")
    private boolean is_disease;

    @Column(length = 50)
    private String disease;

    private String img_url;

    @Column(length = 50)
    private String location;

    public static Mango toEntity(MangoDTO dto){
        return Mango.builder()
                .user(dto.getUser())
                .is_disease(dto.is_disease())
                .disease(dto.getDisease())
                .img_url(dto.getImg_url())
                .location(dto.getLocation())
                .build();
    }

}

