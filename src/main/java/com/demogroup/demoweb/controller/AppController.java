package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.Disease;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.MangoDTO;
import com.demogroup.demoweb.service.AppService;
import com.demogroup.demoweb.service.DiseaseService;
import com.demogroup.demoweb.service.UserService;
import com.demogroup.demoweb.utils.MakeJsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    private final UserService userService;
    private final DiseaseService diseaseService;

    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity test(){
        List<String> lst=new ArrayList<>();
        lst.add("질병1");
        lst.add("질병2");
        lst.add("질병3");

        User user = userService.findByUsername("임시2");
        Disease dieBack = diseaseService.findDisease("Cutting Weevil");

        MangoDTO dto=new MangoDTO(1000l, user, true, "질병1","alsdkjflkjl","지역구1");

        String resultJson = MakeJsonUtil.makeDiagnosisResultJson(lst, dto, dieBack);
        return ResponseEntity.ok().body(resultJson);
    }

}
