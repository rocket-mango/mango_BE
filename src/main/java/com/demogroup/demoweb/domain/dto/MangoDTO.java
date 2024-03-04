package com.demogroup.demoweb.domain.dto;

import com.demogroup.demoweb.domain.Mango;
import com.demogroup.demoweb.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MangoDTO {
    private Long id;

    private User user;
    private boolean is_disease;
    private String disease;
    private String img_url;
    private String location;

    public MangoDTO(User user, boolean is_disease, String disease, String img_url, String location){
        this.user=user;
        this.is_disease=is_disease;
        this.disease=disease;
        this.img_url=img_url;
        this.location=location;
    }

    public static MangoDTO toDTO(Mango e){
        return MangoDTO.builder()
                .id(e.getMid())
                .user(e.getUser())
                .is_disease(e.is_disease())
                .disease(e.getDisease())
                .img_url(e.getImg_url())
                .location(e.getLocation())
                .build();
    }


}