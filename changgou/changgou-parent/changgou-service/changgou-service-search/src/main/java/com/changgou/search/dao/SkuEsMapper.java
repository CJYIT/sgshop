package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/****
 * @Author:cjy
 * @Description: com.changgou.search.dao
 * @Date 2022/8/13 13:04
 *****/
@Repository
public interface SkuEsMapper extends ElasticsearchCrudRepository<SkuInfo,Long> {
}
