package com.shuke.shukepicturebe.manager.sharding;


import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author 舒克、舒克
 * @date 2025/6/18 14:45
 * @description 动态分表算法
 */
@Configuration
public class PictureShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    /**
     * 逻辑分表
     * @param collection  所有支持的表的集合
     * @param preciseShardingValue 逻辑分表键值
     * @return  返回查询的表名
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        Long spaceId = preciseShardingValue.getValue();
        String logicTableName = preciseShardingValue.getLogicTableName();
        // 当spaceId 为空时查询所有的表
        if(spaceId == null){
            return logicTableName;
        }
        // 根据spaceId 查询对应的表
        String tableName = logicTableName + spaceId;
        if(collection.contains(tableName)){
            return tableName;
        }else {
            return logicTableName;
        }

    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return new ArrayList<>();
    }
}
