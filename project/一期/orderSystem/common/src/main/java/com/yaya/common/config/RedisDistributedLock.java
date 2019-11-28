package com.yaya.common.config;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

/**
 * Redis分布式锁实现
 * RedisLockImpl
 */
public class RedisDistributedLock implements Lock {
    StringRedisTemplate stringRedisTemplate;
    
    String resourceName = null;
    
    int timeout = 10;

    ThreadLocal<String> lockRandomValue = new ThreadLocal<>();

    /**
     * 构建一把锁
     *
     * @param resourceName 资源唯一标识
     * @param timeout      资源锁定超时时间~防止资源死锁,单位秒
     */
    public RedisDistributedLock(String resourceName, int timeout, StringRedisTemplate stringRedisTemplate) {
        this.resourceName = "lock_" + resourceName;
        this.timeout = timeout;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean tryLock() {
        Boolean lockResult = stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
            // 随机设置一个值
            lockRandomValue.set(UUID.randomUUID().toString());
            Boolean status = connection.set(resourceName.getBytes(), lockRandomValue.get().getBytes(),
                    Expiration.seconds(timeout), RedisStringCommands.SetOption.SET_IF_ABSENT);
            return status;
        });
        return lockResult;
    }

    Lock lock = new ReentrantLock();

    // 多台机器的情况下，会出现大量的等待，加重redis的压力。 
    // 在lock方法上，加入同步关键字。单机同步，多机用redis
    @Override
    public void lock() {
        lock.lock();
        try {

            while (!tryLock()) {
                stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
                    try {
                        CountDownLatch waiter = new CountDownLatch(1);
                        // 等待通知结果
                        connection.subscribe((message, pattern) -> {
                            // 收到通知，不管结果，立刻再次抢锁
                            waiter.countDown();
                        }, (resourceName + "_unlock_channel").getBytes());
                        // 等待一段时间，超过这个时间都没收到消息，肯定有问题
                        waiter.await(timeout, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                });
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unlock() {
        // 释放锁。（底层框架开发者要防止被API的调用者误调用）
        // 错误示范 stringRedisTemplate.delete(resourceName);
        // 1、 要比对内部的值，同一个线程，才能够去释放锁。 
    	// 2、 同时发出释放锁的通知
        if (lockRandomValue.get().equals(stringRedisTemplate.opsForValue().get(resourceName))) {
        	
        	// 通过lua脚本实现
        	unlockUseLua();
        }
        System.out.println("释放锁："+resourceName);
    }


	private void unlockUseLua() {
    	/* 脚本文件的方式
    	ScriptSource scriptSource = 
    			new ResourceScriptSource(new ClassPathResource("unlock.lua"));
    	DefaultRedisScript<Boolean> defaultScript = new DefaultRedisScript<Boolean>();
    	defaultScript.setScriptSource(scriptSource);
    	*/
    	
    	String scriptText = 
    			"if redis.call('get',KEYS[1]) == ARGV[1] then\r\n" + 
    			"local result = redis.call('del',KEYS[1])\r\n" + 
    			"redis.call('publish',KEYS[2], ARGV[2])\r\n" + 
    			"return result\r\n" + 
    			"else\r\n" + 
    			"return 0\r\n" + 
    			"end";
    	RedisScript<Boolean> script = RedisScript.of(scriptText, Boolean.class);
    	
    	Boolean result = stringRedisTemplate.execute(script, 
    			Arrays.asList(resourceName, resourceName + "_unlock_channel"), 
    			new Object[] {lockRandomValue.get(), lockRandomValue.get()});
    	
    	System.out.println("lua脚本执行结果："+result);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
