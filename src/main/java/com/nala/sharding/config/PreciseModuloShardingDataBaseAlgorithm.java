package com.nala.sharding.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public final class PreciseModuloShardingDataBaseAlgorithm implements PreciseShardingAlgorithm<String>{

    @Override
    public String doSharding(final Collection<String> tableNames, final PreciseShardingValue<String> shardingValue) {
        for (String each : tableNames) {
            int i = shardingValue.getValue().hashCode() % 2;
            if (each.endsWith(i + "")) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }
}
