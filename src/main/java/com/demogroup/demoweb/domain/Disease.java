package com.demogroup.demoweb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disease extends BaseTimeEntity{
    @Id
    private Long did;
    private String name;

    @Column(name = "name_en")
    private String ename;
    private String reason;
    private String symptom;
    private String handle;
}
