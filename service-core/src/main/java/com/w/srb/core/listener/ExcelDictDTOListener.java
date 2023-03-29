package com.w.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.w.srb.core.mapper.DictMapper;
import com.w.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xin
 * @date 2022-10-09-15:36
 */
@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;


    private static final int BATCH_COUNT = 5;
    private List<ExcelDictDTO> list= new ArrayList();

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        log.info("解析到一条数据",data);

        //将数据存储到数据列表
        list.add(data);
        if (list.size()>=BATCH_COUNT){
            saveData();
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //当最后剩余的记录数不足BATCH_COUNT时，最终一次性存入数据库
        saveData();
        log.info("所有数据解析完成");
    }

    public void saveData(){
        log.info("{}条数据被存储到数据库.......",list.size());

        dictMapper.insertBatch(list);

        log.info("{}条数据被存储到数据库成功",list.size());
    }
}
