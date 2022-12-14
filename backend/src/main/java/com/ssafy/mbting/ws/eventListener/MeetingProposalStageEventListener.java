package com.ssafy.mbting.ws.eventListener;

import com.ssafy.mbting.api.response.MemberResponse;
import com.ssafy.mbting.api.service.InterestService;
import com.ssafy.mbting.api.service.MemberService;
import com.ssafy.mbting.db.entity.InterestMember;
import com.ssafy.mbting.db.entity.Member;
import com.ssafy.mbting.ws.model.event.proposal.ProposalBothAcceptedEvent;
import com.ssafy.mbting.ws.model.event.proposal.ProposalResultArriveEvent;
import com.ssafy.mbting.ws.model.event.proposal.ProposalResultsMadeEvent;
import com.ssafy.mbting.ws.model.stompMessageBody.sub.BaseMessageBody;
import com.ssafy.mbting.ws.model.stompMessageBody.sub.MeetingRoom;
import com.ssafy.mbting.ws.model.vo.IndividualDestination;
import com.ssafy.mbting.ws.model.vo.StompUser;
import com.ssafy.mbting.ws.repository.AppRepository;
import com.ssafy.mbting.ws.service.AppStompService;
import com.ssafy.mbting.ws.service.MeetingRoomService;
import com.ssafy.mbting.ws.service.WaitingMeetingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class MeetingProposalStageEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AppStompService appStompService;
    private final WaitingMeetingService waitingMeetingService;
    private final MeetingRoomService meetingRoomService;
    private final MemberService memberService;
    private final AppRepository appRepository;

    @Async
    @EventListener
    public void onProposalResultArrive(ProposalResultArriveEvent event) {
        String sessionId = event.getSessionId();
        Boolean accepted = event.getAccepted();

        logger.debug("\n\nProposal Result Arrive ????????? ?????????\nSession ID: {}\naccepted: {}\n"
                , sessionId
                , accepted ? "??????" : "??????");

        waitingMeetingService.setProposalAcceptedAndHandleIt(
                sessionId,
                accepted);
    }

    @Async
    @EventListener
    public void onProposalResultsMade(ProposalResultsMadeEvent event) {
        String[] sessionIds = event.getSessionIds();
        Boolean[] accepteds = event.getAccepteds();

        logger.debug("\n\nProposal Results Made ????????? ?????????\n1: ({}, {})\n2: ({}, {})\n"
                , sessionIds[0]
                , accepteds[0] ? "??????" : "??????"
                , sessionIds[1]
                , accepteds[1] ? "??????" : "??????");

        if (accepteds[0] && accepteds[1]) {
            applicationEventPublisher.publishEvent(new ProposalBothAcceptedEvent(
                    this,
                    Clock.systemDefaultZone(),
                    sessionIds,
                    meetingRoomService
                            .setMeetingRoomAndGetTokensForTwoUsers(
                                    sessionIds[0],
                                    sessionIds[1])));
            return;
        }

        IntStream.range(0, 2).forEach(i -> {
            if (accepteds[i]) simpMessagingTemplate.convertAndSend(
                    IndividualDestination.of(appStompService
                            .getStompUserBySessionId(sessionIds[i])
                            .orElseThrow(() -> new RuntimeException("Session Not Found!"))
                            .getEmail()).toString(),
                    BaseMessageBody.builder()
                            .command("opponentRefusal")
                            .build());
        });
    }

    @Async
    @EventListener
    public void onProposalBothAccepted(ProposalBothAcceptedEvent event) {
        String[] sessionIds = event.getSessionIds();
        String[] openviduTokens = event.getOpenviduTokens();

        logger.debug("\n\nProposal Both Accepted ????????? ?????????\nSession IDs: {}\nOpenvidu Tokens: {}\n"
                , sessionIds
                , openviduTokens);

        StompUser[] stompUsers = new StompUser[]{
                appStompService
                        .getStompUserBySessionId(sessionIds[0])
                        .orElseThrow(() -> new RuntimeException("Session Not Found!")),
                appStompService
                        .getStompUserBySessionId(sessionIds[1])
                        .orElseThrow(() -> new RuntimeException("Session Not Found!"))};

        logger.debug("\n\nemail1: {}\nemail2: {}\n"
                , stompUsers[1].getEmail()
                , stompUsers[0].getEmail());

//        // Todo: ???????????? ?????? ??????!!!
//        Member[] opponents = new Member[]{
//                memberService.getUserByEmail("wp29dud@naver.com"),
//                memberService.getUserByEmail("rlwls1101@hanmail.net")};

        Member[] opponents = new Member[]{
                memberService.getUserByEmail(stompUsers[1].getEmail()),
                memberService.getUserByEmail(stompUsers[0].getEmail())};
        // ?????? 0????????? ?????? ??????
        StompUser stompUser1 = appRepository.findStompUserBySessionId(sessionIds[0]).orElseThrow(() -> new RuntimeException("Session Not Found!"));
        // ?????? 1????????? ?????? ??????
        StompUser stompUser0 = appRepository.findStompUserBySessionId(sessionIds[1]).orElseThrow(() -> new RuntimeException("Session Not Found!"));

        ArrayList[] interests = new ArrayList[]{(ArrayList) stompUser0.getMeetingUser().getInterests(), (ArrayList) stompUser1.getMeetingUser().getInterests()};

        IntStream.range(0, 2).forEach(i -> simpMessagingTemplate.convertAndSend(
                IndividualDestination.of(stompUsers[i].getEmail()).toString(),
                BaseMessageBody.builder()
                        .command("accept")
                        .data(MeetingRoom.builder()
                                .openviduToken(openviduTokens[i])
                                .opponent(MemberResponse.builder()
                                        .interests(interests[i])
                                        .mbti(opponents[i].getMbti())
                                        .nickname(opponents[i].getNickname())
                                        .gender(opponents[i].getGender())
                                        .sido(opponents[i].getSido())
                                        .email(opponents[i].getEmail())
                                        .birth(opponents[i].getBirth())
                                        .build())
                                .build())
                        .build()));
    }
}
