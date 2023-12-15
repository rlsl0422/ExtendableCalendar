package com.example.extendablecalendar;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {
    ZonedDateTime nowTime = ZonedDateTime.now();
    ZonedDateTime selectedDate = ZonedDateTime.of(1970,1,1,1,1,1,1,nowTime.getZone());
    ZonedDateTime justNow = ZonedDateTime.now();
    ArrayList<ArrayList<String>> data = new ArrayList<>(); //이차원 어레이 리스트 일정 정보 저장
    String qdate; //쿼리문에 쓰일 날짜 형식의 문자열
    @FXML
    private Button btn_addlist;
    @FXML
    private FlowPane calendar_body, fp_list;
    @FXML
    private Label lb_month;

    @FXML
    private void nextMonth() {
        calendar_body.getChildren().clear();
        nowTime = nowTime.plusMonths(1);
        changeMonthLabel();
        drawCalendar();
    }
    @FXML
    private void previousMonth() {
        calendar_body.getChildren().clear();
        nowTime = nowTime.minusMonths(1);
        changeMonthLabel();
        drawCalendar();
    }
    private void changeMonthLabel() {
        int year = nowTime.getYear();
        int month = nowTime.getMonth().getValue();
        lb_month.setText(year+ "년 " + month + "월");
    }




    private void drawCalendar() {

        double calendarWidth = calendar_body.getPrefWidth();
        double calendarHeight = calendar_body.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar_body.getHgap();
        double spacingV = calendar_body.getVgap();

        int maxDate = nowTime.getMonth().maxLength();

        ArrayList<String> eDate; //현재 달의 일정이 있는 날을 불러옴

        //지금 날짜의 년도와 월을 합침
        String tyear = String.valueOf(nowTime.getYear());
        String tmonth = String.valueOf(nowTime.getMonth().getValue());
        if (tmonth.length() == 1) {
            tmonth = "0" + tmonth;
        }

        try {
            eDate = ImportDateList.importdatelist(tyear+tmonth);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //
        if(nowTime.getYear() % 4 != 0 && maxDate == 29){
            maxDate = 28;
        }// 윤년 체크
        int dateOffset = ZonedDateTime.of(nowTime.getYear(), nowTime.getMonthValue(), 1,0,0,0,0,nowTime.getZone()).getDayOfWeek().getValue();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth =(calendarWidth/7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight/6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                rectangle.setArcWidth(10);
                rectangle.setArcHeight(10);
                stackPane.getChildren().add(rectangle);// 상자 추가
                int calculatedDate = (j+1)+(7*i);
                if (dateOffset == 7) {
                    calculatedDate += 7;
                }
                if (calculatedDate > dateOffset) {
                    int currentDate = calculatedDate - dateOffset;
                    if (currentDate == nowTime.getDayOfMonth() && nowTime.getMonth() == justNow.getMonth() && nowTime.getYear() == justNow.getYear()) {
                        rectangle.setStroke(Color.GREEN); // 현재 날짜 강조
                        rectangle.setFill(Color.valueOf("#A9F5BC"));
                    }
                    if (currentDate == selectedDate.getDayOfMonth() && selectedDate.getMonth() == nowTime.getMonth() && selectedDate.getYear() == nowTime.getYear()) {
                        rectangle.setStroke(Color.valueOf("#F7819F")); // 선택 된 날짜 강조
                        rectangle.setFill(Color.valueOf("#F6CED8"));
                    }
                    if (currentDate <= maxDate) {
                        //날짜 숫자가 있는 날에는 이벤트 핸들러 추가
                        rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                fp_list.getChildren().clear();
                                btn_addlist.setDisable(false);
                                selectedDate = ZonedDateTime.of(nowTime.getYear(),nowTime.getMonth().getValue(),currentDate,1,1,1,1,nowTime.getZone());
                                String selyear = String.valueOf(selectedDate.getYear());
                                String selmonth = String.valueOf(selectedDate.getMonth().getValue());
                                String seldate = String.valueOf(selectedDate.getDayOfMonth());
                                if (selmonth.length() == 1) {
                                    selmonth = '0'+selmonth;
                                }
                                if (seldate.length() == 1) {
                                    seldate = '0'+seldate;
                                }
                                qdate = selyear+selmonth+seldate;
                                //테스트
                                drawSchedule(qdate);

                                //
                                //System.out.println(qdate);
                                calendar_body.getChildren().clear();
                                drawCalendar();
                            }
                        });
                        Text date = new Text(String.valueOf(currentDate));//새로운 날짜 텍스트 객체 생성 이름은 date
                        if ((calculatedDate - 1) % 7 == 0) date.setFill(Color.RED);
                        else if ((calculatedDate) % 7 == 0) date.setFill(Color.BLUE);
                        else date.setFill(Color.BLACK);
                        double textTranslationY = -(rectangleHeight / 2) * 0.75;//날짜 상자에서 반절을 취하고 그 중에서 3/4위치
                        date.setTranslateY(textTranslationY);//위로 이동
                        date.setFont(Font.font("Arial"));
                        stackPane.getChildren().add(date);
                        //일정이 있는 날 빨간 점 추가
                        String sNum = String.valueOf(currentDate);
                        if (sNum.length() == 1) {
                            sNum = "0" + sNum;
                        }
                        if (eDate.contains(sNum)) {
                            Circle circle = new Circle();
                            circle.setRadius(4);
                            circle.setFill(Color.RED);
                            circle.setTranslateY(-(rectangleHeight / 2) * 0.75);
                            circle.setTranslateX(-(rectangleWidth / 2) * 0.75);
                            stackPane.getChildren().add(circle);
                        }
                    }
                }
                calendar_body.getChildren().add(stackPane);
            }
        }
    }
    private void drawSchedule(String qdate) {
        try {
            data = ImportDatas.importdatas(qdate);
            for (ArrayList<String> row : data) {
                //System.out.println(row.get(0));
                //System.out.println(row.get(1));
                //System.out.println(row.get(2));
                //SQL 에서 들어온 데이터로 그리기

                Button btn_del = new Button("삭제");
                StackPane stackPane = new StackPane();
                TextField textField = new TextField(row.get(1));
                TextArea textArea = new TextArea(row.get(2));
                Label lb_text_title = new Label("제목");
                double width = fp_list.getWidth();
                Rectangle rectangle = new Rectangle();

                //textField.setDisable(true);
                //textArea.setDisable(true);

                rectangle.setWidth(width-6.0);
                rectangle.setHeight(150.0);
                rectangle.setFill(Color.valueOf("#b4eaed"));
                rectangle.setStrokeWidth(3);
                rectangle.setStroke(Color.valueOf("#3ca5ab"));
                rectangle.setArcWidth(10);
                rectangle.setArcHeight(10);

                double rectangleHeight = rectangle.getHeight();
                double rectangleWeight = rectangle.getWidth();

                textField.setMaxWidth(rectangleWeight/1.5);
                textField.setTranslateX(- (rectangleWeight / 2) + 123);
                textField.setTranslateY(- (rectangleHeight / 2) + 15);
                lb_text_title.setTranslateX(- 70);
                lb_text_title.setTranslateY(- 60);

                textArea.setMaxWidth(rectangleWeight/1.2);
                textArea.setMaxHeight(rectangleHeight / 2.0);

                btn_del.setTranslateY(50);

                stackPane.getChildren().add(rectangle);
                stackPane.getChildren().add(textField);
                stackPane.getChildren().add(lb_text_title);
                stackPane.getChildren().add(textArea);
                stackPane.getChildren().add(btn_del);
                fp_list.getChildren().add(stackPane);
                btn_del.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            DeleteDatas.deletedatas(row.get(0));
                            stackPane.getChildren().clear();
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        calendar_body.getChildren().clear();
                        drawCalendar();
                    }
                });

            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeMonthLabel();
        drawCalendar();
        btn_addlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_addlist.setDisable(true);
                Button btn_save = new Button("저장");
                StackPane stackPane = new StackPane();
                TextField textField = new TextField();
                TextArea textArea = new TextArea();
                Label lb_text_title = new Label("제목");
                double width = fp_list.getWidth();
                Rectangle rectangle = new Rectangle();

                rectangle.setWidth(width-6.0);
                rectangle.setHeight(150.0);
                rectangle.setFill(Color.valueOf("#f0c0ce"));
                rectangle.setStrokeWidth(3);
                rectangle.setStroke(Color.valueOf("#cf2b5b"));
                rectangle.setArcWidth(10);
                rectangle.setArcHeight(10);

                double rectangleHeight = rectangle.getHeight();
                double rectangleWeight = rectangle.getWidth();

                textField.setMaxWidth(rectangleWeight/1.5);
                textField.setTranslateX(- (rectangleWeight / 2) + 123);
                textField.setTranslateY(- (rectangleHeight / 2) + 15);
                lb_text_title.setTranslateX(- 70);
                lb_text_title.setTranslateY(- 60);

                textArea.setMaxWidth(rectangleWeight/1.2);
                textArea.setMaxHeight(rectangleHeight / 2.0);

                btn_save.setTranslateY(50);

                //이벤트 핸들러
                btn_save.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //System.out.println(lb_text_title.getText());
                        //System.out.println(textArea.getText());
                        try {
                            SaveDatas.saveDatas(qdate,textField.getText(),textArea.getText());
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        btn_addlist.setDisable(false);
                        fp_list.getChildren().clear();
                        drawSchedule(qdate);
                        calendar_body.getChildren().clear();
                        drawCalendar();
                    }
                });

                stackPane.getChildren().add(rectangle);
                stackPane.getChildren().add(textField);
                stackPane.getChildren().add(lb_text_title);
                stackPane.getChildren().add(textArea);
                stackPane.getChildren().add(btn_save);
                fp_list.getChildren().add(stackPane);
            }
        });
    }
}

/*
System.out.println("back");
String nowYear = Integer.toString(nowTime.getYear());
String nowMonth = nowTime.getMonth().toString();
String nowDate = Integer.toString(nowTime.getDayOfMonth());
int maxDate = nowTime.getMonth().maxLength();
int fisrtDateOfWeek = ZonedDateTime.of(nowTime.getYear(), nowTime.getMonthValue(), 1,0,0,0,0,nowTime.getZone()).getDayOfWeek().getValue();
lb_month.setText(nowYear+nowMonth+nowDate+ fisrtDateOfWeek + maxDate);
*/