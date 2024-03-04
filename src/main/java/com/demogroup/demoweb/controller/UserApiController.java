package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    //사용자 회원가입
    @PostMapping("/joinProc")
    public ResponseEntity join(@Valid UserDTO dto){
        userService.join(dto);
        dto.setPassword("");
        return ResponseEntity.ok().body(dto);
    }

    //사용자 회원탈퇴
    @GetMapping("/resignation")
    public ResponseEntity resignation(){
        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User entity = userService.findByUsername(principal.getUsername());
        UserDTO dto = UserDTO.toDTO(entity, "");

        userService.userResignation(principal.getUsername());
        return ResponseEntity.ok().body(dto);
    }


    //회원정보 불러오기
    @GetMapping("/information")
    public ResponseEntity getUserInformation(){
        System.out.println("UserApiController.getUserInformation");
        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.getUsername());
        UserDTO dto = UserDTO.toDTO(user, "");
        return ResponseEntity.ok().body(dto);
    }

    //사용자 정보 수정
    @PostMapping("/modify")
    public ResponseEntity modify(@Valid UserDTO dto){
        System.out.println("UserApiController.modify");
        //사용자 찾기
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.modify(dto,principal.getUsername());
        dto.setPassword("");

        return ResponseEntity.ok().body(dto);
    }

}
