package com.ssafy.mbting.ws.repository;

import com.ssafy.mbting.api.request.AudioStageResult;
import com.ssafy.mbting.db.enums.Gender;
import com.ssafy.mbting.ws.model.vo.MeetingRoom;
import com.ssafy.mbting.ws.model.vo.MeetingUser;
import com.ssafy.mbting.ws.model.vo.StompUser;
import com.ssafy.mbting.ws.model.vo.StompUserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Sets.newConcurrentHashSet;

import static java.util.Optional.*;

@Repository
public class AppRepositoryImpl implements AppRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, StompUser> sessionIdStompUserMap = newConcurrentMap();
    private final LinkedHashSet<String> waitingMeetingUserQueue = new LinkedHashSet<>();
    private final Map<String, MeetingRoom> meetingRoomIdMeetingRoomMap = newConcurrentMap();
    private final Map<Gender, Set<String>> genderSessionIdMap = newConcurrentMap();
    private final Map<String, Set<String>> sidoSessionIdMap = newConcurrentMap();
    private final Map<String, Set<String>> interestSessionIdMap = newConcurrentMap();

    @PostConstruct
    private void init() {
        genderSessionIdMap.put(Gender.MALE, newConcurrentHashSet());
        genderSessionIdMap.put(Gender.FEMALE, newConcurrentHashSet());
    }

    @Override
    public Map<Gender, Set<String>> getGenderTable() {
        return genderSessionIdMap;
    }

    @Override
    public Map<String, Set<String>> getSidoTable() {
        return sidoSessionIdMap;
    }

    @Override
    public Map<String, Set<String>> getInterestTable() {
        return interestSessionIdMap;
    }

    @Override
    public StompUser createSession(String sessionId, StompUser stompUser) {
        StompUser oldValue = sessionIdStompUserMap.put(sessionId, stompUser);
        logger.debug("\n\nsessionIdStompUserMap??? ({}, {}) ??????\n"
                , sessionId
                , stompUser);
        return oldValue;
    }

    @Override
    public Optional<StompUser> findStompUserBySessionId(String sessionId) {
        Optional<StompUser> stompUser = ofNullable(sessionIdStompUserMap.get(sessionId));
        stompUser.ifPresent(user -> logger.debug("\n\nStompUser found\n({}, {})\n", sessionId, user));
        return stompUser;
    }

    @Override
    public void removeSession(String sessionId) {
        sessionIdStompUserMap.remove(sessionId);
        logger.debug("\n\nsessionIdStompUserMap?????? ?????? \"{}\" ?????????\n", sessionId);
    }

    @Override
    public void saveMeetingUser(String sessionId, MeetingUser meetingUser) {
        StompUser stompUser = ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"));
        if (stompUser == null) {
            logger.error("\n\n????????? ???????????? ??????\nSession ID: {}\n", sessionId);
            throw new RuntimeException("No Session!");
        }
        stompUser.setMeetingUser(meetingUser);
        stompUser.setStompUserStatus(StompUserStatus.INPROGRESS);
        logger.debug("\n\n?????? \"{}\" ??? ?????? ?????? ?????? {} ?????? ???\n?????? ????????? INPROGRESS\n", sessionId, meetingUser);
    }

    @Override
    public void joinToQueue(String sessionId) {
        StompUser stompUser = ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"));
        stompUser.cleanForJoiningToQueue();
        waitingMeetingUserQueue.add(sessionId);
        addSessionIdToFeatureUserTables(sessionId);
        stompUser.setStompUserStatus(StompUserStatus.INQUEUE);
        logger.debug("\n\n?????? \"{}\" ??? ?????? ?????? ????????? ???\nQueue, FetureTables ??? ?????? ???\n?????? ????????? INQUEUE\n", sessionId);
    }

    @Override
    public void leaveFromQueue(String sessionId) {
        waitingMeetingUserQueue.remove(sessionId);
        removeSessionIdFromFeatureUserTables(sessionId);
        ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"))
                .setStompUserStatus(StompUserStatus.INPROGRESS);
        logger.debug("\n\nQueue, FeatureTables ?????? \"{}\" ?????? ??? ?????? ????????? INPROGRESS\n", sessionId);
    }

    @Override
    public void setMatchedMeetingUser(String subjectSessionId, String matchedSessionId) {
        ofNullable(sessionIdStompUserMap.get(subjectSessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"))
                .setMatchedMeetingUserSessionId(matchedSessionId);
        logger.debug("\n\n?????? \"{}\" ??? \"{}\" ????????? ??????\n", subjectSessionId, matchedSessionId);
    }

    @Override
    public void setProposalAccepted(String sessionId, Boolean accepted) {
        ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"))
                .setProposalAccepted(accepted);
        logger.debug("\n\n?????? \"{}\" ??? ?????? ?????? ????????? {} ??? ??????\n", sessionId, accepted);
    }

    @Override
    public Optional<MeetingRoom> findMeetingRoomByMeetingRoomId(String meetingRoomId) {
        Optional<MeetingRoom> meetingRoom = ofNullable(meetingRoomIdMeetingRoomMap.get(meetingRoomId));
        meetingRoom.ifPresent(room -> logger.debug("\n\nMeetingRoom found\n({}, {})\n", meetingRoomId, room));
        return meetingRoom;
    }

    @Override
    public void setVoiceResult(String sessionId, AudioStageResult voiceResult) {
        StompUser stompUser = ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"));
        String meetingRoomId = stompUser.getMeetingRoomId();
        Integer indexOnRoom = stompUser.getIndexOnRoom();
        ofNullable(meetingRoomIdMeetingRoomMap.get(meetingRoomId))
                .orElseThrow(() -> new RuntimeException("Room Not Found!"))
                .getMeetingRoomResults()[indexOnRoom]
                .setVoiceResult(voiceResult);

        logger.debug("\n\n?????? \"{}\" ??? ?????? ???????????? ????????? {} ??? ??????\nMeeting Room ID: {}\nIndex On Room: {}\n"
                , sessionId
                , voiceResult
                , meetingRoomId
                , indexOnRoom);
    }

    @Override
    public void saveMeetingRoom(String meetingRoomId, MeetingRoom meetingRoom) {
        meetingRoomIdMeetingRoomMap.put(meetingRoomId, meetingRoom);
        logger.debug("\n\nmeetingRoomIdMeetingRoomMap???\n({}, {}) ?????????\n", meetingRoomId, meetingRoom);
    }

    @Override
    public void setMeetingRoomIdAndIndex(String sessionId, String meetingRoomId, Integer indexOnRoom) {
        StompUser stompUser = ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"));
        stompUser.setMeetingRoomId(meetingRoomId);
        stompUser.setIndexOnRoom(indexOnRoom);
        stompUser.setStompUserStatus(StompUserStatus.INROOM);
        logger.debug("\n\n?????? \"{}\" ??? ?????? ??? \"{}[{}]\" ?????? ??? ?????? ?????? INROOM\n"
                , sessionId
                , meetingRoomId
                , indexOnRoom);
    }

    @Override
    public void setMeetingRoomStatusToFalse(String meetingRoomId, int indexOnRoom) {
        ofNullable(meetingRoomIdMeetingRoomMap.get(meetingRoomId)).ifPresent(room -> {
            room.getMeetingRoomStatus()[indexOnRoom] = false;
            logger.debug("\n\n????????? {} ??? {} ?????? ????????? false??? ??????\n"
                    , meetingRoomId
                    , indexOnRoom);
        });
    }

    @Override
    public void removeMeetingRoom(String meetingRoomId) {
        ofNullable(meetingRoomIdMeetingRoomMap.remove(meetingRoomId)).ifPresent(oldRoom ->
                logger.debug("\n\n?????? ??? \"{}\" ?????????\nRemoved Room: {}\n", meetingRoomId, oldRoom));
    }

    @Override
    public int getQueueSize() {
        int size = waitingMeetingUserQueue.size();
        logger.debug("\n\n?????? ????????? ??????: {}\n", size);
        return size;
    }

    @Override
    public Optional<String> getFirstSessionId() {
        Optional<String> firstSessionId = waitingMeetingUserQueue.stream().findFirst();
        logger.debug("\n\n???????????? ??? ??? Session ID: {}\n", firstSessionId);
        return firstSessionId;
    }

    private void addSessionIdToFeatureUserTables(String sessionId) {
        MeetingUser meetingUser = ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"))
                .getMeetingUser();
        genderSessionIdMap.get(meetingUser.getGender()).add(sessionId);
        String sido = meetingUser.getSido();
        sidoSessionIdMap.putIfAbsent(sido, newConcurrentHashSet());
        sidoSessionIdMap.get(sido).add(sessionId);
        meetingUser.getInterests().forEach(interest -> {
            interestSessionIdMap.putIfAbsent(interest, newConcurrentHashSet());
            interestSessionIdMap.get(interest).add(sessionId);
        });
        logger.debug("\n\nFeatureUserTables ??? \"{}\" ??????\nMeetingUser: {}\n", sessionId, meetingUser);
    }

    private void removeSessionIdFromFeatureUserTables(String sessionId) {
        MeetingUser meetingUser = ofNullable(sessionIdStompUserMap.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Session Not Found!"))
                .getMeetingUser();
        genderSessionIdMap.get(meetingUser.getGender()).remove(sessionId);
        sidoSessionIdMap.get(meetingUser.getSido()).remove(sessionId);
        meetingUser.getInterests().forEach(
                interest -> interestSessionIdMap.get(interest).remove(sessionId));
        logger.debug("\n\nFeatureUserTables ?????? \"{}\" ???\nMeetingUser: {}\n", sessionId, meetingUser);
    }
}
