package com.service.impl;

import com.service.AllUserList;
import com.service.EarlyRankService;
import com.util.EarlyUser;
import com.util.ServiceResult;
import com.util.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lala
 */

@Slf4j
@Service
public class EarlyRankServiceImpl implements EarlyRankService {

    @Autowired
    AllUserList allUserList;

    @Autowired
    JedisPool jedisPool;

    @Override
    public ServiceResult earlyRank(String date) {
        List<EarlyUser> earlyUsers = new ArrayList<>();
        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();

        User[] users =  allUserList.allUserList.toArray(new User[allUserList.allUserList.size()]);
        String[] keys = new String[users.length];
        int i = 0;
        for(User user : users) {
            keys[i] = (date + ":" + user.getId());
            pipeline.bitpos(keys[i], true);
            i++;
        }

        List<Object> all = pipeline.syncAndReturnAll();
        jedis.close();
        for(i = 0; i < users.length; i++) {
            if(all.get(i).toString().equals("-1"))
                continue;
            EarlyUser earlyUser = new EarlyUser();
            earlyUser.setName(users[i].getName());
            earlyUser.setDepartment(users[i].getDepartment());
            earlyUser.setEarlyTime(Integer.parseInt(all.get(i).toString()));
            earlyUsers.add(earlyUser);
        }
        earlyUsers.sort((a, b) -> a.getEarlyTime() - b.getEarlyTime());
        return ServiceResult.success(earlyUsers);
    }
}
