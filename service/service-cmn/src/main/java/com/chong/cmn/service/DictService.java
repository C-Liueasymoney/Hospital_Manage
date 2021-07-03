package com.chong.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chong.hospital.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/26 9:16 下午
 */
public interface DictService extends IService<Dict> {
    List<Dict> queryChildData(Long id);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);

    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode
     * @param value
     * @return
     */
    String getNameByParentDictCodeAndValue(String parentDictCode, String value);

    List<Dict> queryByDictCode(String dictCode);
}
