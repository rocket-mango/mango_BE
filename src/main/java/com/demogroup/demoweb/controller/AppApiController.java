package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.Mango;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.service.AppService;
import com.demogroup.demoweb.service.DiseaseService;
import com.demogroup.demoweb.service.UserService;
import com.demogroup.demoweb.utils.MakeJsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppApiController {

    private final DiseaseService diseaseService;
    private final UserService userService;

    private final AppService appService;

    //홈 화면에 띄울 정보를 보내는 컨트롤러. 기상청 정보와 사용자의 마이망고리스트를 보내야 한다.
    @GetMapping("/api/home")
    public ResponseEntity<String> homePage(){
        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        System.out.println(username);

        try {
            //기상청 날씨 정보
            StringBuilder sb=new StringBuilder("{");
            String weatherJson = appService.getWeather();
            sb.append(weatherJson);

            //사용자의 마이망고리스트
            List<Mango> mangoList = diseaseService.mangoList(username);
            String mangolistjson = MakeJsonUtil.makeMyMangoListJson(mangoList);
            sb.append(mangolistjson);
            sb.append("}");

            String jsonStr=sb.toString();

            return ResponseEntity.ok().body(jsonStr);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail get weather information");
        }
    }
}
