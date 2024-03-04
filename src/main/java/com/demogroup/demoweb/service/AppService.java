package com.demogroup.demoweb.service;

import com.demogroup.demoweb.exception.AppException;
import com.demogroup.demoweb.exception.ErrorCode;
import com.demogroup.demoweb.utils.MakeJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class AppService {

    //기상청 API에 연결하여 현재 기온을 가져오는 메소드입니다.
    public String getWeather() throws Exception{
        //날짜와 시간을 각각 yyyyMMdd, HHmm 형태로 변환하여 api url에서 사용할 수 있는 형태로 만듭니다.
        LocalDate predate=LocalDate.now();
        LocalTime pretime=LocalTime.now();
        pretime=pretime.minusMinutes(20);

        DateTimeFormatter formatter_date=DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter_time=DateTimeFormatter.ofPattern("HHmm");

        String date=predate.format(formatter_date);
        String time=pretime.format(formatter_time);

        //API URL을 생성합니다.
        String apiUrl="http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?serviceKey=BfA%2FSVzcfbHnBlj1JBKH0LDU2MxepQ3no4LKdLDIYNbk47IjAA0qFi4qu%2BZKV%2B%2BpwPUVEKVrf90QTET9WHsxbw%3D%3D&numOfRows=10&dataType=JSON&base_date="
                +date+"&base_time="+time+"&pageNo=1&nx=55&ny=127";
        URL url=new URL(apiUrl);

        //HttpURLConnection을 생성합니다.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        //요청을 수행합니다.
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type","application/json");

        /*응답을 받아 문자열 result 에 저장합니다.
        * bufferedReader를 사용해서 입력을 한 줄씩 입력받고,
        * UTF-8로 디코드하여 StringBuffer에 저장합니다.
        * 이후 sb에 저장한 내용을 문자열 result에 저장합니다.
         */
        BufferedReader br=new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine="";
        StringBuffer sb = new StringBuffer();

        while( (inputLine=br.readLine())!=null){
            sb.append(new String(URLDecoder.decode(inputLine,"UTF-8")));
        }

        br.close();
        String result = sb.toString();
        String resultJson = weatherJson(result);

        return resultJson;
    }

    private String weatherJson(String result) {

        try {
            //Result를 json 형식으로 변환하여 temperature 정보를 가져옵니다.
            JSONParser jsonParser = new JSONParser();
            JSONObject response = (JSONObject) jsonParser.parse(result);
            JSONObject body = (JSONObject) response.get("response");
            body=(JSONObject) body.get("body");
            JSONObject items = (JSONObject) body.get("items");
            JSONArray itemList = (JSONArray) items.get("item");

            //습도
            JSONObject REH = (JSONObject) itemList.get(1);
            String humidity = (String) REH.get("obsrValue");

            //1시간 강수량
            JSONObject RN1 = (JSONObject) itemList.get(2);
            String precipitation = (String) RN1.get("obsrValue");

            //기온
            JSONObject T1H = (JSONObject) itemList.get(3);
            String temperature = (String) T1H.get("obsrValue");

            //풍향
            JSONObject VEC = (JSONObject) itemList.get(5);
            String wind = (String) VEC.get("obsrValue");

            //풍속
            JSONObject WSD = (JSONObject) itemList.get(7);
            String wind_velocity = (String) WSD.get("obsrValue");

            String jsonResult = MakeJsonUtil.makeWeatherJson(humidity, precipitation, temperature, wind, wind_velocity);

            return jsonResult;

        }catch (Exception e){
            throw new AppException(ErrorCode.JSON_PARSE_FAILED,"JSON PARSE 시 오류가 발생했습니다");
        }
    }
}
