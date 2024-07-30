package com.fcwenergy.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.fcwenergy.common.domain.entity.LogKw;
import com.fcwenergy.common.domain.param.LogKwQueryParam;
import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.base.DataCategoryEnum;
import com.fcwenergy.iotdb.dts.core.IBaseParamFunction;
import com.fcwenergy.iotdb.dts.core.IotDbDataDTSService;
import com.fcwenergy.iotdb.modules.LogKwIotDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@RestController
@RequestMapping({"/logKw"})
@RequiredArgsConstructor
public class LogKwController {

    private final LogKwIotDbService logKwIotDbService;
    private final IotDbDataDTSService<LogKwQueryParam, LogKw> dataDTSService;

    @GetMapping(value = "/random/add")
    @ResponseBody
    public boolean random(@RequestParam Long equipmentId) {
        LogKw logKw = new LogKw();
        logKw.setEquipmentId(equipmentId);
        logKw.setName(equipmentId.toString());
        logKw.setBattKw(BigDecimal.valueOf(RandomUtil.randomDouble(0, 200)));
        logKw.setGridKw(BigDecimal.valueOf(RandomUtil.randomDouble(0, 200)));
        logKw.setLoadKw(BigDecimal.valueOf(RandomUtil.randomDouble(0, 200)));
        logKw.setBattInKw(BigDecimal.valueOf(RandomUtil.randomDouble(0, 200)));
        logKw.setBattOutKw(BigDecimal.valueOf(RandomUtil.randomDouble(0, 200)));
        logKw.setMpptKw(BigDecimal.valueOf(RandomUtil.randomDouble(0, 200)));
        Date now = DateUtil.date();
        logKw.setCreateTime(DateUtil.offsetMillisecond(now, RandomUtil.randomInt(0, 1000000)));
        logKwIotDbService.insert(logKw, logKw.getCreateTime().getTime());
        return true;
    }

    @GetMapping(value = "/list/days")
    @ResponseBody
    public List<LogKw> list(@RequestParam Long equipmentId,
                            @RequestParam(required = false) Integer days) {
        DateTime nowTime = DateUtil.date();
        DateTime recentTime = DateUtil.offsetDay(nowTime, -ObjectUtil.defaultIfNull(days, 2));
        List<Date> createTimes = CollUtil.newArrayList(recentTime, nowTime);
        LogKwQueryParam queryParam = LogKwQueryParam.builder()
                                                    .equipmentId(equipmentId)
                                                    .createTime(createTimes)
                                                    .build();
        return logKwIotDbService.queryAll(queryParam);
    }

    /**
     * @param equipmentId 设备id
     * @return 状态
     */
    @GetMapping(value = "/dts/logKw")
    @ResponseBody
    public boolean dts(@RequestParam Long equipmentId,
                       @RequestParam(required = false) Integer days) {
        DateTime nowTime = DateUtil.date();
        //同步最近七天数据
        DateTime recentTime = DateUtil.offsetDay(nowTime, -ObjectUtil.defaultIfNull(days, 30));
        LogKwQueryParam queryParam = LogKwQueryParam.builder()
                                                    .equipmentId(equipmentId)
                                                    .build();
        IBaseParamFunction<LogKw> function = logKw -> BigdataBaseParam.builder()
                                                                      .category(DataCategoryEnum.ES_KW_LOG)
                                                                      .equipmentId(logKw.getEquipmentId())
                                                                      .build();
        CompletableFuture.runAsync(() -> dataDTSService.dataTransformBackUp(logKwIotDbService,
                queryParam,
                function,
                recentTime,
                nowTime
        ));
        return true;
    }
}


