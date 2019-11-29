package com.yaya.order.socket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnMessage;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;


import java.io.IOException;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/30
 * @description
 */
@ServerEndpoint("/topic/order")
@Slf4j
@Component
public class OrderSocket {


    private Session session;

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        log.info("连接成功");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        session.close();
        log.info("连关闭成功");
    }

    @OnMessage
    public void handleMessage(String message){
        if (Optional.ofNullable(session).isPresent() && session.isOpen()){
            session.sendText(message);
        }
    }
}
