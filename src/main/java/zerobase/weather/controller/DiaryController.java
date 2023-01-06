package zerobase.weather.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;


@RestController // 기본컨트롤러 기능외에 상태코드를 지정하여 내려줌
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping ("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);

    }
}
