package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;//使用ID生成工具idWorker，因为商品的id不属于自增

    @Autowired
    private SkuMapper skuMapper;//sku需要添加到数据库中

    @Autowired
    private CategoryMapper categoryMapper;//查询三级分类

    @Autowired
    private BrandMapper brandMapper;//品牌名称



    /**添加商品信息
     * 添加保存Goods的方法实现
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        //添加商品的时候，spu添加一次，sku添加多次
        //spu->一个
        //先获得需要维护的信息
        Spu spu = goods.getSpu();//获取商品的spu
        spu.setId(idWorker.nextId());//生成商品id
//        spu.setIsMarketable("1");//标记商品上架状态，这里默认是下架就不写了
        spuMapper.insertSelective(spu);//添加到数据库
        Date date = new Date();
        //三级分类
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //品牌信息
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //sku->list集合
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            sku.setId(idWorker.nextId());
            //获取spec的值     {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}  json转map
            String name = spu.getName();//上面有spu，Spu spu = goods.getSpu();//获取商品的spu
            //防止空值   如果前端传来的值为空就传一个空的对象给它
            if (StringUtils.isEmpty(sku.getSpec())){
                sku.setSpec("{}");
            }
            //将spec转成Map
            Map<String,String> specMap = JSON.parseObject(sku.getSpec(), Map.class);//前端传来的格式sku.getSpec()，转成Map.class格式
            //拼接name
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                name+=""+ entry.getValue();//使用entry遍历
            }
            sku.setName("name");//Spu.name+规格信息
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setSpuId(spu.getId());//从前端传过来
            sku.setCategoryId(spu.getCategory3Id());//从spu获取三级分类
            sku.setCategoryName(category.getName());//分类名字-》3级分类名字，spu中没有，需要从数据库中查询  categoryMapper
            sku.setBrandName(brand.getName());//品牌名字,spu里面只要品牌id没有品牌名称，需要从数据库中查

            //将sku添加到数据库
            skuMapper.insertSelective(sku);
        }


    }

    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建  排除掉 已删除的
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete",0);//只找 没有被删除的
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 物理删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new RuntimeException("商品不存在");
        }
        if (!spu.getIsDelete().equals("1")) {
            throw new RuntimeException("必须先逻辑删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 无spuid增加商品有spuid修改商品
     * @param goods   update  add
     */
    @Override
    public void save(Goods goods) {

        Spu spu = goods.getSpu();
        if (goods.getSpu().getId() == null) {
            //新增
            //1.先获取SPU的数据  添加到表中 spu表中
            spu.setId(idWorker.nextId());//生成主键 要唯一 雪花算法.
            spuMapper.insertSelective(spu);

            //新增SKU
        } else {
            //修改
            spuMapper.updateByPrimaryKeySelective(spu);

            //1.先删除spu对应的原来的SKU的类别

            //delete from tb_sku where spu_id = ?//已经修改spu，原来的sku与之对应不上所以需要删除
            Sku condition = new Sku();
            condition.setSpuId(spu.getId());//获得sku表中需要删除有之前spuid的数据
            skuMapper.delete(condition);
            //2.再新增

        }


        //2.获取SKU 的列表数据  循环遍历添加掉sku表中
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            // id 要生成
            sku.setId(idWorker.nextId());
            // name 要生成 (spu的名称+ " "+ 规格的选项的值 )  //  spu的名称: iphonex  规格的数据: spec:{ 颜色:红色,内存大小:16G}
            // 先获取规格的数据
            String spec = sku.getSpec(); //{ 颜色:红色,内存大小:16G}
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            // 转成map对象  key:颜色  value:红色
            String titile = spu.getName();
            for (String key : map.keySet()) {
                // 获取SPU的名称 拼接 即可
                titile += " " + map.get(key);
            }
            sku.setName(titile);

            // create_time
            sku.setCreateTime(new Date());

            // update_time
            sku.setUpdateTime(sku.getCreateTime());

            // spu_id
            sku.setSpuId(spu.getId());

            // category_id 3级分类的ID
            Integer category3Id = spu.getCategory3Id();
            sku.setCategoryId(category3Id);
            // category_name 3级分类的名称
            Category category = categoryMapper.selectByPrimaryKey(category3Id);
            sku.setCategoryName(category.getName());
            // brand_name
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            sku.setBrandName(brand.getName());
            skuMapper.insertSelective(sku);
        }
    }

    /**
     * 根据id查goods数据
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(Long id) {

        //2.根据id查询SPU的数据
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //3.根据ID 查询SKU的列表数据
        //select * from tb_sku where spu_id = ?
        Sku sku = new Sku();
        sku.setSpuId(id);//上面的spuid，sku表中有spuid
        List<Sku> skuList = skuMapper.select(sku);
        //1.创建goods对象
        Goods goods = new Goods();
        //4.组合对象 返回
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 批量上架
     * @param ids
     */
    @Override
    public void putMany(Long[] ids) {
        //update tb_sku IsMarketable=1 id in(ids) and isdelete=0 status=1
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();//criteria构建条件查询
        //id in (ids)
        criteria.andIn("id", Arrays.asList(ids));
        //未删除
        criteria.andEqualTo("isDelete","0");
        //已审核
        criteria.andEqualTo("status","1");

        //准备修改数据
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        spuMapper.updateByExampleSelective(spu,example);//两个参数分别是修改的结果spu和条件example
    }

    /**
     * 批量下架
     * @param ids
     */
    @Override
    public void pullMany(Long[] ids) {
        //update tb_sku IsMarketable=1 id in(ids) and setIsMarketable=0
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();//criteria构建条件查询
        //id in (ids)
        criteria.andIn("id", Arrays.asList(ids));
        //未删除
        criteria.andEqualTo("isMarketable","1");

        //准备修改数据
        Spu spu = new Spu();
        spu.setIsMarketable("0");
        spuMapper.updateByExampleSelective(spu,example);//两个参数分别是修改的结果spu和条件example

    }


    /**
     * 商品审核
     * @param spuid
     */
    @Override
    public void audit(Long spuid) {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(spuid);
        //判断商品是否被删除
        if (spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("不能对已删除的商品进行审核");
        }
        //审核修改状态
        spu.setStatus("1");
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }


    @Override
    public void auditSpu(Long id) {
        //update tb_spu set status=1,is_marketable=1 where is_delete=0 and id = ?

        //先判断是否已经被删除
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null || spu.getIsDelete().equals("1")) {//已经被删除了 或者商品部存在
            throw new RuntimeException("商品不存在或者已经删除");
        }
        //审核商品
        spu.setStatus("1");//已经审核
        spu.setIsMarketable("1");//自动上架
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品上架
     * @param spuid
     */
    @Override
    public void put(Long spuid) {
        //update tb_spu set is_marketable=0 where is_delete=0 and id = ? and is_marketable=1 and status=1
        Spu spu = spuMapper.selectByPrimaryKey(spuid); //警告重复行提取到一个公共方法来重构代码
        if (spu == null || spu.getIsDelete().equals("1")) {//已经被删除了 或者商品部存在
            throw new RuntimeException("商品不存在或者已经删除");
        }

        if(!spu.getStatus().equals("1") || !spu.getIsMarketable().equals("1")){
            throw new RuntimeException("商品必须要审核或者商品必须要是上架的状态");
        }

        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);

    }

    @Override
    public void pullSpu(Long id) {
        //update tb_spu set is_marketable=0 where is_delete=0 and id = ? and is_marketable=1 and status=1
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null || spu.getIsDelete().equals("1")) {//已经被删除了 或者商品部存在
            throw new RuntimeException("商品不存在或者已经删除");
        }

        if(!spu.getStatus().equals("1") || !spu.getIsMarketable().equals("1")){
            throw new RuntimeException("商品必须要审核或者商品必须要是上架的状态");
        }

        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);

    }

    @Override
    public void logicDeleteSpu(Long id) {
        // update set is_delete=1 where id =? and is_delete=0
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new RuntimeException("商品不存在");
        }

        if(spu.getIsMarketable().equals("1")){
            throw new RuntimeException("商品还没下架,不能删除");
        }
        spu.setIsDelete("1");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /***
     * 逻辑删除
     * @param spuId
     */
    @Override
    @Transactional
    public void logicDelete(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //检查是否下架的商品
        if(!spu.getIsMarketable().equals("0")){
            throw new RuntimeException("必须先下架再删除！");
        }
        //删除
        spu.setIsDelete("1");
        //未审核
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void restoreSpu(Long id) {
        // update set is_delete=0 where id =? and is_delete=1
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new RuntimeException("商品不存在");
        }
        Spu data = new Spu();
        data.setIsDelete("0");//恢复
        Example exmaple = new Example(Spu.class);
        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andEqualTo("id",id);//where id =1
        criteria.andEqualTo("isDelete","1");
        spuMapper.updateByExampleSelective(data,exmaple);
// spuMapper.updateByPrimaryKeySelective(spu);//根据主键来进行更新  update set name=? where id=?
    }

    /**
     * 恢复数据
     * @param spuId
     */
    @Override
    public void restore(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //检查是否删除的商品
        if(!spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品未删除！");
        }
        //未删除
        spu.setIsDelete("0");
        //未审核
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }
}
