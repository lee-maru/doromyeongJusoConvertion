package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestURL {

    @Test
    void testUrl() throws IOException {
        String apiUrl = "https://www.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+1+"&countPerPage="+1+"&keyword="+ URLEncoder.encode("전라북도 임실군 청웅면 두복리 36 37, 37-3, 37-1, 37-2, 531-2, 4, 산86-8, 산86-4, 산88-2, 6-6, 6- ","UTF-8")+"&confmKey="+"devU01TX0FVVEgyMDIwMTIxNTIxMDM0NTExMDU1MjI="+"&resultType="+"json";

            URL url = new URL(apiUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            StringBuffer sb = new StringBuffer();
            String tempStr = null;

            while (true) {
                tempStr = br.readLine();
                if (tempStr == null) break;
                sb.append(tempStr);
                System.out.println(tempStr);
                JSONArray jsonArray = new JSONObject(tempStr).getJSONObject("results").getJSONArray("juso");
                if(jsonArray.isEmpty()){
                    System.out.println("도로명 주소 없음");
                }else{
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    System.out.println((String)jsonObject.get("roadAddr"));
                }
            }
            br.close();

    }

}