package com.w.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.w.srb.core.listener.ExcelDictDTOListener;
import com.w.srb.core.pojo.dto.ExcelDictDTO;
import com.w.srb.core.pojo.entity.Dict;
import com.w.srb.core.mapper.DictMapper;
import com.w.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * Excel导入
     * @param inputStream
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("Excel导入成功");
    }

    /**
     * Excel导出
     * @return
     */
    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        //创建ExcelDictDTO列表，将Dict列表转换成ExcelDictDTO列表

        ArrayList<ExcelDictDTO> excelDictDTOS = new ArrayList<>(dictList.size());

        dictList.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            excelDictDTOS.add(excelDictDTO);
        });

        return excelDictDTOS;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {


        try {

            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            if (dictList!=null){
                log.info("从redis中取出数据");
                return dictList;
            }

        } catch (Exception e) {
            log.info("redis服务器出现错误"+ ExceptionUtils.getStackTrace(e));
        }
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",parentId);
        log.info("从数据库中取出数据");
        List<Dict> dicts = baseMapper.selectList(dictQueryWrapper);
        dicts.forEach(dict -> {
            boolean b = this.hasChildren(dict.getId());
            dict.setHasChildren(b);
        });

        //将数据存入redis
        try {
            log.info("将数据存入redis");
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId,dicts,5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("redis服务器出现错误"+ ExceptionUtils.getStackTrace(e));
        }

        return dicts;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {

        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);

        return this.listByParentId(dict.getId());
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {

        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict parentDict = baseMapper.selectOne(dictQueryWrapper);

        if (parentDict==null){
            return "";
        }

        dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",parentDict.getId())
                .eq("value",value);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);

        if (dict==null){
            return "";
        }

        return dict.getName();
    }

    /**
     * 判断当前id所在的节点下是否有子节点
     * @param id
     * @return
     */
    private boolean hasChildren(Long id){
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",id);
        Integer integer = baseMapper.selectCount(dictQueryWrapper);
        if (integer.intValue()>0){
            return true;
        }else {
            return false;
        }
    }


}
