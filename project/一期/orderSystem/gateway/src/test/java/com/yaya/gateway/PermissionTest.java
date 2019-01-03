package com.yaya.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/12/25
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PermissionTest {

    private  final  static String url =  "http://localhost:8093/";

    private static RestTemplate restTemplate = new RestTemplate();

    @Test
    public void test() throws InterruptedException {
        int count = 10;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0;i < count;i++){
            executorService.submit(createThread(i,cyclicBarrier));
        }
        Thread.sleep(50000);
        System.out.println("线程启动完成");
    }

    private Thread createThread(int i,CyclicBarrier cyclicBarrier) {
        Thread thread = new Thread(() ->{
            try {
                System.out.println("Thread:" + Thread.currentThread().getName() + "提前准备完毕,time:" + System.currentTimeMillis());
                cyclicBarrier.await();
                System.out.println("Thread:" + Thread.currentThread().getName() + "准备完毕,time:" + System.currentTimeMillis());
                ResponseEntity<String> response = restTemplate.exchange(url + "/permission/getMethodNameByType?userType={userType}" ,
                        HttpMethod.GET,
                        new HttpEntity(null),
                        String.class,1);
                System.out.println("result: " + response.getBody());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });

        thread.setName("name" + i);
        return thread;
    }
}
