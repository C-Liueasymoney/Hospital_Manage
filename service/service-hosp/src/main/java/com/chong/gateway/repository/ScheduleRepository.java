package com.chong.gateway.repository;

import com.chong.hospital.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 9:05 下午
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> getSchedulesByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);
}
