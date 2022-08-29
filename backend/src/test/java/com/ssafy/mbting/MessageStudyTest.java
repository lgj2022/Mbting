package com.ssafy.mbting;

import com.ssafy.mbting.db.entity.Member;
import com.ssafy.mbting.db.entity.Message;
import com.ssafy.mbting.db.repository.MemberRepository;
import com.ssafy.mbting.db.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MessageStudyTest {

    @Autowired
    MemberRepository mbr;
    @Autowired
    MessageRepository msgr;

    @Test
    void Test() {
        System.out.println("hello");

        Member mb1 = new Member();
        Member mb2 = new Member();
        mb1.setNickname("홍동");
        mb2.setNickname("유순");


        mbr.save(mb1);
        mbr.save(mb2);

        Message msg1 = new Message();
        msg1.setContent("테스트 메시지입니다.");
        msg1.setFromId(mb1);
        msg1.setToId(mb2);

        msgr.save(msg1);

        List<Message> messages = msgr.findMessages(mb2, "테스트");

        System.out.println(messages);
    }
}
