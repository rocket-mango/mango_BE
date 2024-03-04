package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.Mango;
import com.demogroup.demoweb.repository.UserRepository;
import com.demogroup.demoweb.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/disease")
@RequiredArgsConstructor
public class DiseaseController {
    private final DiseaseService diseaseService;


    //질병 식별 시작 페이지 화면을 반환하는 컨트롤러 입니다.
    @GetMapping("/home")
    public String homePage(){
        return "disease/home";
    }

    @GetMapping("/my-mango-list")
    public String myMangoList(Model model){
        System.out.println("DiseaseController.myMangoList");
        List<Mango> myList=diseaseService.mangoList("임시2");
        model.addAttribute("mangoList",myList);
        model.addAttribute("nickname","임시2");
        return "disease/mango_list";
    }

    @GetMapping("/my-mango-list-j")
    @ResponseBody
    public ResponseEntity myMangoList_j(){
        System.out.println("DiseaseController.myMangoList");
        List<Mango> myList=diseaseService.mangoList("임시2");
//        model.addAttribute("mangoList",myList);
//        model.addAttribute("nickname","임시2");

        return ResponseEntity.ok().body("{\"mangoList\":"+myList+",\"nickname\": \"임시2\"}");
    }
}
