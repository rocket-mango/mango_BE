package com.demogroup.demoweb.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.demogroup.demoweb.config.S3Config;
import com.demogroup.demoweb.domain.Disease;
import com.demogroup.demoweb.domain.Mango;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.MangoDTO;
import com.demogroup.demoweb.exception.AppException;
import com.demogroup.demoweb.exception.ErrorCode;
import com.demogroup.demoweb.repository.DiseaseRepository;
import com.demogroup.demoweb.repository.MangoRepository;
import com.demogroup.demoweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiseaseService {
    private final UserRepository userRepository;
    private final MangoRepository mangoRepository;
    private final DiseaseRepository diseaseRepository;
    private final S3Config s3Config;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String localLocation="C:\\Users\\yujin\\Downloads\\temp";

    //S3에 이미지를 저장하는 메소드 입니다.
    public String saveToS3(MultipartFile mangoImage) {
        //mangoImage를 일시적으로 로컬에 저장
        //확장자를 추출한다.
        String originalFilename = mangoImage.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.indexOf("."));

        //이미지 이름을 생성한다.
        String uuidFileName = UUID.randomUUID() + ext;
        String localPath=localLocation+uuidFileName;

        //일시적으로 저장할 로컬 저장소를 생성한다.
        File localFile=new File(localPath);
        try {
            mangoImage.transferTo(localFile);
            s3Config.amazonS3Client()
                    .putObject(new PutObjectRequest(bucket,uuidFileName,localFile).withCannedAcl(CannedAccessControlList.PublicRead));
            String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();
            localFile.delete();
            return s3Url;
        }catch (IOException e){
            System.out.println(e.getStackTrace());
            return null;
        }
    }

    public List<String> diagnosis(String s3Url) {
        List<String> resultList=new ArrayList<String>();
        WebClient webClient= WebClient.create("http://localhost:8083");
        resultList = webClient.post()
                .uri("/first")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"s3url\": \"" + s3Url + "\"}")
                .retrieve()
                .bodyToMono(List.class)
                .block();

        return resultList;
    }

    public void saveMango(MangoDTO dto) {
        Mango mango = Mango.toEntity(dto);
        mangoRepository.save(mango);
    }

    public List<Mango> mangoList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->(new AppException(ErrorCode.USERNAME_NOT_FOUND,"확인되지 않은 사용자입니다.")));
        List<Mango> mangoList = mangoRepository.findAllByUser_Uid(user.getUid());
        return mangoList;
    }

    @Transactional
    public void deleteMango(Long mid) throws NoSuchElementException {
        /*
         repository 함수를 사용 후 Optional 객체로 받아 조건문을 사용하여 번거롭게 작업하지 않아도
         곧바로 .orElseThrow 함수를 사용하면, 없을 경우 오류를, 있을 경우 바로 Mango라는 객체로 받을 수 있다.
         orElseThrow 함수의 매개변수로는 람다식을 사용해 NoSuchElementException 객체를 새로 생성하여 사용할 수 있다.
         */
        Mango deleteM = mangoRepository.findById(mid)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 망고 객체를 찾을 수 없습니다."));
        //s3버킷에서 해당 url 삭제
        /*
         aws에서 AmazonS3Client를 사용해서 deleteObject 함수를 실행할 때는,
         url 전체를 넣는 것이 아니라
         폴더명/파일명(확장자 포함) 이라는 파일명, 즉 key를 넣어야 한다.
         그래서 나는 url을 저장해놨기 때문에 이렇게 String 객체의 lastIndexOf("/") 함수를 이용해서
         substring으로 key를 추출했지만,
         더 체계적인 관리가 필요할 경우 이렇게 하기보다는
         저장할 때 key를 저장하여 관리하는 것이 좋겠다는 생각이 들었다.
         */
        int i = deleteM.getImg_url().lastIndexOf("/");
        String key=deleteM.getImg_url().substring(i+1);

        try{
            s3Config.amazonS3Client().deleteObject(bucket,key);
            /* 계속 이 부분에서 Access Denied라는 오류가 났었는데 그 이유는
             IAM의 엑세스 키와 비밀번호를 깃허브에 올려서였다..
             깃허브에서 그걸 인식하고 나에게 이메일을 보냈고, 심지어 AWS도 이메일을 보냈다..
             다행히 AWS에서 인지하고 AWS에서 ACCESS를 안전하게 막아놨기 때문에
             ACCESS DENIED가 되었던 거고, 다른 도용의 문제는 없었지만
             정말 큰일날 뻔 했다...
             다음부터는 .gitignore 에 반드시 application.properties를  추가할것!
             */
            mangoRepository.delete(deleteM);
            System.out.println("DiseaseService.deleteMango");
            //이렇게 AmazonS3Exception을 출력하면 어떤 오류인지 알 수 있다.
        }catch (AmazonS3Exception e){
            System.out.println(e.getErrorMessage());
        }
    }


    @Transactional
    // location 별로 망고를 찾는 법
    public List<Mango> mangoListByLocation(String location, String username) {
        List<Mango> mangoList=new ArrayList<>();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "가입되지 않은 아이디입니다. 로그인을 진행합니다."));
        System.out.println("UserService.modify");
        Long uid = user.getUid();
        mangoList = mangoRepository.findMangoByUidAndLocation(uid, location);

        return mangoList;
    }

    public Disease findDisease(String diseaseName) {
        Disease disease = diseaseRepository.findByEname(diseaseName)
                .orElseThrow(()->new AppException(ErrorCode.DISEASE_NOT_FOUND,"질병을 찾지 못했습니다. ORM 또는 DB에 오류가 발생했을 가능성이 있습니다."));
        return disease;
    }
}
