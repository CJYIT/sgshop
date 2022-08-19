package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.changgou.dao *
 * @since 1.0
 */

/**
 * Mapper<Brand>  指定通用的mapper的父接口:封装了所有的CRUD的操作
 * T  指定操作的表对应的POJO
 * 查询所有，使用tk下的Mapper接口
 * 继承之后如果想添加数据，直接Mapper.insert()或者调用Mapper.insertselective()，就是直接在service中直接去调用相应的方法
 * 修改，调用mapper.updata方法（T）或者mapper.updatabyprimarykey(T)
 * 查询数据mapper.selectbyprimarykey(id)
 * 条件查询mapper.select(T)
 */
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 根据分类查询品牌集合信息
     * @param categoryid
     * @return
     */
    @Select("select tb.* from tb_brand tb,tb_category_brand tcb where tb.id=tcb.brand_id and tcb.category_id=#{categoryid};")
    List<Brand> findByCategory(Integer categoryid);
}
