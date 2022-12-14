package com.ssafy.mbting.api.controller;

import com.ssafy.mbting.api.response.MemberLoginResponse;
import com.ssafy.mbting.api.response.MemberResponse;
import com.ssafy.mbting.common.util.BaseResponseUtil;
import com.ssafy.mbting.common.util.KakaoAPI;
import com.ssafy.mbting.db.entity.Member;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.mbting.api.service.MemberService;
import com.ssafy.mbting.common.util.JwtTokenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "인증 API", tags = {"Auth."})
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
@RequiredArgsConstructor
//auth/login
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BaseResponseUtil baseResponseUtil;
    private final MemberService memberService;
    private final KakaoAPI kakaoApi;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam("code") String code) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = null;
        logger.debug("code " + code);
        // 1번 인증코드 요청 전달

        String accessToken = kakaoApi.getAccessToken(code);

        logger.debug("accessToken : " + accessToken);
        // 2번 인증코드로 토큰 전달
        HashMap<String, Object> userInfo = kakaoApi.getUserInfo(accessToken);

        logger.debug("\n\nlogin info: {}\n", userInfo.toString());

        String token = JwtTokenUtil.getToken((String) userInfo.get("email"));

        MemberResponse mres = new MemberResponse();
        mres.setEmail((String) userInfo.get("email"));
        mres.setProfileUrl((String) userInfo.get("profile_image"));
        mres.setNickname((String) userInfo.get("nickname"));

        if (userInfo.get("email") != null) {
            Member member = memberService.getUserByEmail((String) userInfo.get("email"));
            if (member == null) {
                return baseResponseUtil.success(MemberLoginResponse.builder()
                        .visited(false)
                        .jwt(token)
                        .member(mres)
                        .build());
            } else if (member.getDeleted() == true) {
                return baseResponseUtil.success(MemberLoginResponse.builder()
                        .visited(false)
                        .jwt(token)
                        .member(mres)
                        .build());
            }
        } else {
            return baseResponseUtil.success("no email");
        }
        return baseResponseUtil.success(MemberLoginResponse.builder()
                .visited(true)
                .jwt(token)
                .member(mres)
                .build());
    }
}
