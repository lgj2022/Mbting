package com.ssafy.mbting.api.controller;

import com.ssafy.mbting.api.request.ReportRegisterRequest;
import com.ssafy.mbting.api.response.*;
import com.ssafy.mbting.api.service.ReportService;
import com.ssafy.mbting.common.util.BaseResponseUtil;
import com.ssafy.mbting.db.entity.Report;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BaseResponseUtil baseResponseUtil;
    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity<?> register(@RequestBody ReportRegisterRequest reportRegisterRequest) {
        Report report = reportService.createReport(reportRegisterRequest);
        return baseResponseUtil.success();
    }
}
