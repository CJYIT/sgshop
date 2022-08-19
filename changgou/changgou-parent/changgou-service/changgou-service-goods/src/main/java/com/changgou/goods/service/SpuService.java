package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/****
 * @Author:admin
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService {

    /**
     * 批量上架
     * ids []要上架的所有商品id   spuid
     */
    void putMany(Long[] ids);

    /**
     * 批量下架
     * ids []要上架的所有商品id   spuid
     */
    void pullMany(Long[] ids);

    /**
     * 商品审核
     * @param spuid
     */
    void audit(Long spuid);

    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
     Spu findById(Long id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();

    /**
     * 添加商品(SPU+ SKUlIST)
     * @param goods   update  add
     */
    void save(Goods goods);

    /**
     * 根据id查Goods
     * @param id
     * @return
     */
    Goods findGoodsById(Long id);

    /**
     * 商品审核
     * @param id
     */
    void auditSpu(Long id);

    /**
     *商品上架
     */
    void put(Long spuid);

    /**
     * 下架
     * @param id
     */
    void pullSpu(Long id);

    void logicDeleteSpu(Long id);

    /**
     * 商品逻辑删除
     * @param spuId
     */
    @Transactional
    void logicDelete(Long spuId);

    /**
     * 商品还原
     * @param id
     */
    void restoreSpu(Long id);

    /**
     * 保存商品
     * @param goods
     */
    void saveGoods(Goods goods);

    void restore(Long spuId);
}
