package com.demogroup.demoweb.utils;

import com.demogroup.demoweb.domain.Disease;
import com.demogroup.demoweb.domain.Mango;
import com.demogroup.demoweb.domain.dto.MangoDTO;
import com.demogroup.demoweb.domain.dto.UserDTO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.List;

public class MakeJsonUtil{
    public static JSONObject makeJoinJson(String username, String password) throws ParseException {
        String jsonStr="{\"username\":\""+username+"\", \"password\":\""+password+"\"}";
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);

        return jsonObject;

    }

    public static String makeMangoJson(Mango mango) {
        String jsonStr = "{\"mid\":\""+mango.getMid()
                +"\",\"is_disease\":\""+mango.is_disease()
                +"\", \"disease\":\""+mango.getDisease()
                +"\", \"img_url\":\""+mango.getImg_url()
                +"\", \"location\":\""+mango.getLocation()
                +"\"},";

        return jsonStr;
    }

    public static String makeWeatherJson(String humidity,
                                         String precipitation,
                                         String temperature,
                                         String wind,
                                         String wind_velocity){
        String jsonStr="\"weather\" : {\"humidity\":\""+humidity
                +"\",\"precipitation\":\""+precipitation
                +"\", \"temperature\":\""+temperature
                +"\", \"wind\":\""+wind
                +"\", \"wind_velocity\":\""+wind_velocity
                +"\"},";
        return jsonStr;
    }

    public static String makeDiagnosisResultJson(List<String> resultlist, MangoDTO dto, Disease disease){
        StringBuilder sb=new StringBuilder();
        sb.append("{");

        String top3list="\"top3list\" : [\""+resultlist.get(0)
                +"\", \""+resultlist.get(1)
                +"\", \""+resultlist.get(2)
                +"\"],";
        sb.append(top3list);

        String mangoResult="\"mangoresult\":{"
                +"\"is_disease\":\""+dto.is_disease()
                +"\", \"disease\":\""+dto.getDisease()
                +"\", \"img_url\":\""+dto.getImg_url()
                +"\", \"location\":\""+dto.getLocation()
                +"\"},";
        sb.append(mangoResult);

        String diseasedetail="\"disease\" : {"
                +"\"name\" : \""+disease.getName()
                +"\", \"en_name\" : \""+disease.getEname()
                +"\", \"reason\" : \""+disease.getReason()
                +"\", \"symptom\" : \""+disease.getSymptom()
                +"\", \"handle\" : \""+disease.getHandle()
                +"\"}";
        sb.append(diseasedetail);

        sb.append("}");

        String jsonStr = sb.toString();

        return jsonStr;
    }

    public static String makeMyMangoListJson(List<Mango> mangoList){
        String jsonStr="\"mangolist\" : [";

        for(int i=0; i<mangoList.size(); i++){
            Mango mango = mangoList.get(i);
            String mangoJson = MakeJsonUtil.makeMangoJson(mango);
            jsonStr+=mangoJson;
        }
        jsonStr+="],";
        return jsonStr;

    }
    public static JSONObject makeModifyJson(UserDTO dto) throws ParseException {
        String jsonStr="{\"name\":\""+dto.getName()
                +"\", \"nickname\":\""+dto.getNickname()
                +"\", \"username\":\""+dto.getUsername()
                +"\", \"password\":\""+dto.getPassword()
                +"\",\"email\":\""+dto.getEmail()
                +"\", \"role\":\""+dto.getRole()
                +"\"}";
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);

        return jsonObject;

    }

}
