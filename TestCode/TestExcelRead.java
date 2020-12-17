package org.example;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TestExcelRead {


    @Test
    void testExcelRead() throws IOException {
        String FILEPATH = "C:\\Users\\seokm\\OneDrive\\바탕 화면\\excelFile.xls";
        Workbook workbook = null;
        List<String> jibun = new ArrayList<>();
        int finalNum = 2759;
        int sheetNum = 0;
        int rowNum = 6;
        try {
            workbook = WorkbookFactory.create(new File(FILEPATH));
            Sheet sheet = workbook.getSheetAt(sheetNum);
            int i = 0;
            for(Row row : sheet){
                jibun.add(row.getCell(rowNum).getStringCellValue());
                i++;
                if(i == 2760){break;}
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(workbook != null){
                workbook.close();
            }
        }
        //Sheet sheet = workbook.getSheetAt(0);
        assertEquals(FILEPATH,"C:/helloExcel.xlsx");
    }

    @Test
    void testURL() throws IOException {
        String FILEPATH = "C:\\Users\\seokm\\OneDrive\\바탕 화면\\excelFile.xls";
        Workbook workbook = null;
        int finalNum = 2759;
        int sheetNum = 0;
        int rowNum = 6;
        try {
            workbook = WorkbookFactory.create(new File(FILEPATH));
            Sheet sheet = workbook.getSheetAt(sheetNum);
            int i = 0;
            for(Row row : sheet){
                String stringCellValue = row.getCell(rowNum).getStringCellValue();
                String apiUrl = "https://www.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+1+"&countPerPage="+1+"&keyword="+ URLEncoder.encode(stringCellValue,"UTF-8")+"&confmKey="+"devU01TX0FVVEgyMDIwMTIxNTIxMDM0NTExMDU1MjI="+"&resultType="+"json";
                URL url = new URL(apiUrl);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                String tempStr = null;

                String result = null;
                while (true) {
                    tempStr = br.readLine();
                    if (tempStr == null) break;
                    sb.append(tempStr);
                    if(new JSONObject(tempStr).getJSONObject("results").getJSONObject("common").get("errorMessage").toString().equals("검색어가 입력되지 않았습니다.")){
                        System.out.println(i+"행 "+ stringCellValue + " 지번이 아님");
                        result = "지번이 아님";
                    }else{
                        JSONArray jsonArray = new JSONObject(tempStr).getJSONObject("results").getJSONArray("juso");
                        if(jsonArray.isEmpty()){
                            result = "도로명 주소 없음";
                            System.out.println(i+"행 "+ stringCellValue +"도로명 주소 없음");
                        }else{
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                            System.out.println(i + "행 "+ stringCellValue +(String)jsonObject.get("roadAddr"));
                            result = (String)jsonObject.get("roadAddr");
                        }
                    }
                    if(!result.equals("지번이 아님")){
                        row.getCell(rowNum+1).setCellValue(result);
                    }
                }
                br.close();
                i++;
                if(i == 2760){break;}
            }
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\seokm\\OneDrive\\바탕 화면\\result.xls");
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(workbook != null){
                workbook.close();
            }
        }
        //Sheet sheet = workbook.getSheetAt(0);
        assertEquals(FILEPATH,"C:/helloExcel.xlsx");
    }

    @Test
    void testExcelWrite() throws IOException {
        String FILEPATH = "C:\\Users\\seokm\\OneDrive\\바탕 화면\\write.xls";
        Workbook workbook = null;
        workbook = WorkbookFactory.create(new File(FILEPATH));
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(7);

        row.getCell(7).setCellValue("도로명 주소 없음");


        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\seokm\\OneDrive\\바탕 화면\\result.xls");
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
