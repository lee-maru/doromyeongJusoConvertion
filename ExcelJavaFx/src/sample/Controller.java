package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class Controller {
    @FXML
    private Button btn;

    @FXML
    private Label excelPath;

    @FXML
    private TextField jibunRow;

    @FXML
    private TextField doroRow;

    @FXML
    private TextField maxNum;

    @FXML
    private AnchorPane anchorid;

    @FXML
    private Label resultPATH;

    @FXML
    public static void rewritePath(String str){
    }

    @FXML
    public void excute(ActionEvent actionEvent) throws IOException {
        int jinunLow2 = Integer.parseInt(jibunRow.getText());
        int doroRow2 = Integer.parseInt(doroRow.getText());
        int maxNum2 = Integer.parseInt(maxNum.getText());
        String excelPath2 = excelPath.getText();
        String resultPath2 = resultPATH.getText();
        System.out.println(jinunLow2);
        System.out.println(doroRow2);
        System.out.println(maxNum2);
        System.out.println(resultPath2);
        System.out.println(excelPath2);
        //    public static void excutemain(String wrtiePath, String filePath, int FINALNUM, int jibunRow) throws IOException {
        Controller.excutemain(resultPath2,excelPath2,maxNum2,jinunLow2,doroRow2);
    }
    @FXML
    public void choosePath(ActionEvent actionEvent) {

        final DirectoryChooser directoryChooser = new DirectoryChooser();

        Stage stage = (Stage) anchorid.getScene().getWindow();

        File file = directoryChooser.showDialog(stage);

        if(file != null){
            System.out.println("Path : " + file.getAbsolutePath());
            resultPATH.setText(file.getAbsolutePath());
        }

    }

    public void chooseWritePath(ActionEvent actionEvent) {
        final JFileChooser fileChooser = new JFileChooser();

        JFrame window2 = new JFrame();
        int result = fileChooser.showOpenDialog(window2);
        File selectedFile = fileChooser.getSelectedFile();
        System.out.println(selectedFile);
        if(selectedFile != null){
            System.out.println("Path : " + selectedFile.getAbsolutePath());
            excelPath.setText(selectedFile.getAbsolutePath());
        }
    }
    public static void excutemain(String wrtiePath, String filePath33, int FINALNUM, int jibunRow, int ROADNUM) throws IOException {
        String FILEPATH = filePath33;
        String WRITEPATH = wrtiePath;
        Workbook workbook = null;
        int finalNum = FINALNUM-1;
        int roadNum = ROADNUM-1;
        int sheetNum = 0;
        int rowNum = jibunRow-1;
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
                    String errormessage = new JSONObject(tempStr).getJSONObject("results").getJSONObject("common").get("errorMessage").toString();
                    if(errormessage.equals("검색어가 입력되지 않았습니다.")){
                        System.out.println(i+"행 "+ stringCellValue + " 지번이 아님");
                        result = "지번이 아님";
                    } else if(errormessage.equals("검색어가 너무 깁니다. (한글40자, 영문,숫자 80자 이하)")){
                        result = "검색어가 너무 깁니다. 직접 확인해보세요.";
                    } else {
                        JSONArray jsonArray = new JSONObject(tempStr).getJSONObject("results").getJSONArray("juso");
                        if (jsonArray.isEmpty()) {
                            result = "도로명 주소 없음";
                            System.out.println(i + "행 " + stringCellValue + "도로명 주소 없음");
                        } else {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                            System.out.println(i + "행 " + stringCellValue + (String) jsonObject.get("roadAddr"));
                            result = (String) jsonObject.get("roadAddr");
                        }
                    }
                    if(!result.equals("지번이 아님")){
                        row.getCell(roadNum).setCellValue(result);
                    }
                }
                br.close();
                i++;
                if(i == finalNum){break;}
            }
            FileOutputStream outputStream = new FileOutputStream(WRITEPATH + "\\결과.xls");
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(workbook != null){
                workbook.close();
            }
        }
    }

}
