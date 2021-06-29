package com.chong.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chong.cmn.listener.DictListener;
import com.chong.cmn.mapper.DictMapper;
import com.chong.cmn.service.DictService;
import com.chong.hospital.model.cmn.Dict;
import com.chong.hospital.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/26 9:16 下午
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictService dictService;

//    根据数据id查询子数据列表
    @Override
    @Cacheable(cacheNames = "dict", keyGenerator = "keyGenerator")  // 添加缓存
    public List<Dict> queryChildData(Long id) {
        if (hasChild(id)){
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", id);
            List<Dict> childData = baseMapper.selectList(wrapper);
//            为列表中的每一个数据设置一下hasChild字段,标注一下子数据还有没有子数据
            for (Dict dict : childData){
                Long childId = dict.getId();
                boolean hasChild = this.hasChild(childId);
                dict.setHasChildren(hasChild);
            }
            return childData;
        }else
            return null;
    }

    private boolean hasChild(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
//        条件查询：统计一下表中parent_id列的值等于id的行数，如果大于0说明id存在对应的子数据
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }

    // 导出数据字典接口
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
//            防止中文乱码
            String filename = URLEncoder.encode("dict", "utf-8");
            response.setHeader("Content-Disposition", "attachment:filename=" + filename + ".xlsx");
//            查询数据库把数据写入表中
            List<Dict> dicts = baseMapper.selectList(null);
            ArrayList<DictEeVo> dictEeVos = new ArrayList<>(dicts.size());   // 设置个initCapacity防止不断扩容（数据量较大）
//            把dicts转换为dictEeVos因为只需要dict里面的部分信息房子啊dictEeVos
            for (Dict dict : dicts){
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictEeVo, DictEeVo.class);
                dictEeVos.add(dictEeVo);
            }

//            进行excel的写操作,这里doWrite会自动把流关闭
            EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("dict").doWrite(dictEeVos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 导入数据字典
    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(dictService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
