package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.changgou.goods.service *
 * @since 1.0
 */
public interface BrandService {
    /**
     * 根据分类id查询品牌集合
     * @return categoryid  分类id
     */
    List<Brand> findByCategory(Integer categoryid);

    List<Brand> findAll();//查询所有

    Brand findById(Integer id);//根据id查询

//添加品牌
    void add(Brand brand);
/*
* 修改品牌数据*/
    void update(Brand brand);

    void delete(Integer id);

    /**
     * 根据条件查询品牌的列表
     * @param brand
     * @return
     */
    List<Brand> findList(Brand brand);


    /**
     * 分页查询 无条件
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Integer page, Integer size);

    /**
     * 有条件的分页查询
     *
     * @param page
     * @param size
     * @param brand
     * @return
     */
    PageInfo<Brand> findPage(Integer page, Integer size, Brand brand);
}
