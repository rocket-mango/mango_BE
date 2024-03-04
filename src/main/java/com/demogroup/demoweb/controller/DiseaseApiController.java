package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.Disease;
import com.demogroup.demoweb.domain.Mango;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.MangoDTO;
import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.service.DiseaseService;
import com.demogroup.demoweb.service.UserService;
import com.demogroup.demoweb.utils.MakeJsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/disease")
@RequiredArgsConstructor
public class DiseaseApiController {
    private final DiseaseService diseaseService;
    private final UserService userService;

    //망고 질병 검색을 진행하는 컨트롤러
    //리턴값 : top 3 결과와 망고 결과 정보를 리턴한다.
    @PostMapping("/diagnosis")
    public ResponseEntity mangoDiagnosis(@RequestParam("mangoImage") MultipartFile mangoImage,
                                         @RequestParam("location") String location) throws Exception{

        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        User user = userService.findByUsername(username);

        //String location="임시";
        System.out.println("DiseaseApiController.mangoDiagnosis");

        //이미지를 s3에 저장하고, 망고 검사를 진행하는 페이지
        String s3Url = diseaseService.saveToS3(mangoImage);
        List<String> resultList = diseaseService.diagnosis(s3Url);

        //망고 결과 저장하기
        boolean is_disease=true;
        String diseaseName="";
        Disease disease=null;

        if(resultList.get(0).equals("Healthy")){
            is_disease=false;
        }
        else {
            diseaseName=resultList.get(0);
            disease=diseaseService.findDisease(diseaseName);
        }
        MangoDTO dto =new MangoDTO(user,is_disease,diseaseName, s3Url, location);
        diseaseService.saveMango(dto);


        //결과를 반환하는 json 만드는 콛
        String resultJson=MakeJsonUtil.makeDiagnosisResultJson(resultList,dto,disease);


        return ResponseEntity.ok().body(resultJson);
    }

    //망고 리스트 가져오기
    @GetMapping("/my-mango-list")
    public ResponseEntity myMangoList(){
        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();

        //해당 사용자의 망고 리스트 반환
        System.out.println("DiseaseController.myMangoList");
        List<Mango> mangoList=diseaseService.mangoList(username);

        String jsonStr="{";
        jsonStr += MakeJsonUtil.makeMyMangoListJson(mangoList);
        jsonStr+="}";

        return ResponseEntity.ok().body(jsonStr);
    }

    //프런트엔드에서 location을 보내면 location에 맞는 리스트만 뽑아 보내주는 컨트롤러
    @GetMapping("/lists/byLocation")
    public ResponseEntity listByLocation(String location){
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username=principal.getUsername();
        List<Mango> mangoList = diseaseService.mangoListByLocation(location,username);

        String jsonStr="{";
        jsonStr += MakeJsonUtil.makeMyMangoListJson(mangoList);
        jsonStr+="}";

        return ResponseEntity.ok().body(jsonStr);
    }

    //망고 리스트에서 해당 망고 객체를 삭제한다.
    @GetMapping("/lists/delete/{mid}")
    public ResponseEntity listDelete(@PathVariable("mid") Long mid){
        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("DiseaseApiController.listDelete");
        try{
            diseaseService.deleteMango(mid);
            return ResponseEntity.ok().body("successful delete mango");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail delete mango");
        }
    }


}
