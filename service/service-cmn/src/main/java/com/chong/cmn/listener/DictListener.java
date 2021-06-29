package com.chong.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.chong.cmn.service.DictService;
import com.chong.hospital.model.cmn.Dict;
import com.chong.hospital.vo.cmn.DictEeVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/27 9:05 上午
 */
@Log4j2
public class DictListener extends AnalysisEventListener<DictEeVo> {
    private DictService dictService;
    private final static int BATCH_COUNT = 100;
    List<Dict> list = new ArrayList<>();

    public DictListener(DictService dictService){
        this.dictService = dictService;
    }

    // 批量插入
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        list.add(dict);
        if (list.size() >= BATCH_COUNT){
            dictService.saveBatch(list, BATCH_COUNT);
            list.clear();
        }
//        log.info("所有数据解析完成");
    }

    // 所有的数据解析完成后需要调用此方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 在这个方法中也保存一下，确保最后没到达BATCH_COUNT的遗留数据也被保存
        dictService.saveBatch(list, BATCH_COUNT);
        log.info("数据保存完成");
    }
}
