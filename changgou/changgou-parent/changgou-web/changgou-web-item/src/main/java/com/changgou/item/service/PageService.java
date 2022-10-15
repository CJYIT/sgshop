package com.changgou.item.service;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.item.service *
 * @since 1.0
 */
public interface PageService {
    //生成静态页,注意这里的是spuId不是id，生成静态页面的public void createPageHtml(Long spuId) {
    void createPageHtml(Long spuId);
}
