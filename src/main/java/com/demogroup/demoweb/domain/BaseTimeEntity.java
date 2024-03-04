package com.demogroup.demoweb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
/*모든 엔티티에 공통으로 가져가야 하는
* 속성들을 이렇게 상위 클래스에 모아놓는 것이다.
*/
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
abstract class BaseTimeEntity {
    @Column(name = "created_date")
    @CreatedDate
    private String createdDate;

    @Column(name="modified_date")
    @LastModifiedDate
    private String modifiedDate;

    @PrePersist
    public void onPrePersis(){
        this.createdDate= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        this.modifiedDate=this.createdDate;
    }

    @PreUpdate
    public void onPreUpdate(){
        this.modifiedDate=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
