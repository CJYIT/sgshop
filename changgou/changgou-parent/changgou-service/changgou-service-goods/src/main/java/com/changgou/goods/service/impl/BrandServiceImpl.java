package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.changgou.goods.service.impl *
 * @since 1.0
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;//调用dao

    /**
     * 根据分类id查询品牌集合
     * @param categoryid
     * @return
     */
    @Override
    public List<Brand> findByCategory(Integer categoryid) {
        return brandMapper.findByCategory(categoryid);
    }

    @Override
    public List<Brand> findAll() {
        List<Brand> brands = brandMapper.selectAll();

        return brands;
    }

    @Override
    public Brand findById(Integer id) {//根据id查询的实现
//        使用通用mapper的selectByPrimaryKey方法根据主键查询
        // select * from tb_brand where id = ?
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {//mapper.insertSelective()方法实现添加品牌
        //insert into tb_brand(id,name,....) values(#{})//只要有Selective会忽略空值
        brandMapper.insertSelective(brand);
    }
//修改
    @Override
    public void update(Brand brand) {
        //update tb_brand  set name=? where id =?
        // Mapper.updateByPrimaryKeySelective(brand);会忽略空值，空值不改
        brandMapper.updateByPrimaryKeySelective(brand);
    }
//删除
    @Override
    public void delete(Integer id) {
        //delete from tb_brand wehre id =?
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Brand brand) {
        //select * from tb_brand where name like "%?%" and letter = '?'
        //1.创建条件对象( 设置字节码对象 标识 查询哪一个表)
        //CTR + ALT + M
//        调用方法构建条件
        Example example = createExample(brand);

        //3.根据条件来执行查询
        List<Brand> brands = brandMapper.selectByExample(example);
        //4.返回结果
        return brands;
    }

    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {

        //1.开始分页 紧跟着的[第一个查询 才会被分页]
        PageHelper.startPage(page, size);
        //2.执行查询
        List<Brand> brands = brandMapper.selectAll();
        List<Brand> brands1111 = brandMapper.selectAll();

        System.out.println(brands.size() + "::::::::brands1111:" + brands1111.size());

        //3.获取到结果集


        //4.封装pageinfo 返回

        return new PageInfo<Brand>(brands);
    }

    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size, Brand brand) {
        //1.开始分页
        PageHelper.startPage(page,size);
        //2.构建查询的条件，搜索依据
        Example example = createExample(brand);
        //3.执行查询
        List<Brand> brands = brandMapper.selectByExample(example);
        //4.获取结果
        //5.封装pageinfo 返回list
        return new PageInfo<Brand>(brands);
    }

    private Example createExample(Brand brand) {
        Example example = new Example(Brand.class);//select * from tb_brand
        Example.Criteria criteria = example.createCriteria();//条件构造器
        //2.判断 拼接条件
        if (brand != null) {//判断条件是否为空
            if (!StringUtils.isEmpty(brand.getName())) {// where name like "%%"?
                //第一个参数:指定要条件比较的 属性的名称(POJO(brand)的属性名)
                //第二个参数:指定要比较的值,占位符要搜索的条件
                criteria.andLike("name", "%" + brand.getName() + "%");
            }

            if (!StringUtils.isEmpty(brand.getLetter())) {// where letter = ?
                //第一个参数:指定要条件比较的 属性的名称(POJO的属性名)
                //第二个参数:指定要比较的值
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }
        return example;
    }


}
