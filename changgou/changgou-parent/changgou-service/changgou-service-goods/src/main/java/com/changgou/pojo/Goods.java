package com.changgou.pojo;

import java.io.Serializable;
import java.util.List;

/****
 * @Author:cjy
 * @Description:
 * @Date 2022/8/9 17:30
 *****/
public class Goods implements Serializable {
    //spu信息
    private Spu spu;
    //sku信息
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
