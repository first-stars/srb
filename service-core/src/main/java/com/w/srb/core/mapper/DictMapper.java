package com.w.srb.core.mapper;

import com.w.srb.core.pojo.dto.ExcelDictDTO;
import com.w.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);
}
