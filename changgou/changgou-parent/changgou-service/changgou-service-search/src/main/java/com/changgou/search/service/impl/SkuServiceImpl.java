package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/****
 * @Author:cjy
 * @Description: com.changgou.search.service.impl
 * @Date 2022/8/13 12:48
 *****/
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SkuEsMapper skuEsMapper;

    /**
     * ElasticsearchTemplate实现索引库的高级搜索
     */
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 搜索需要搜索条件searchMap，使用那个对象去搜索elasticsearchTemplate
     * 多条件搜索
     *
     * @param searchMap
     * @return resultMap
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {//Map<String, Object>
        /**
         * 搜索条件封装
         */
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBasicQuery(searchMap);
        //条件构建
//        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);


        //集合搜索
        Map<String, Object> resultMap = searchList(nativeSearchQueryBuilder);
        //搜索集合
//        Map resultMap = searchList(builder);


        //这两个地方报错未找出原因  已解决
        //当用户选择了分类，将分类作为搜索条件，则不需要对分类进行分组搜索，因为分组搜索的数据用于显示分类搜索条件的
        //分类->searchMap->category
        /*if (searchMap == null || searchMap.get("category") == null) {
            //分类分组查询实现   调用抽取出来的类方法
//            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
            resultMap.put("categoryList", categoryList);
        }


        //当用户选择了品牌，将品牌作为搜索条件，则不需要对品牌进行分组搜索，因为分组搜索的数据用于显示品牌搜索条件的
        //品牌->brandMap->brand
        if (searchMap == null || searchMap.get("brand") == null) {
            //查询品牌集合[搜索条件]   调用抽取出来的类方法
            List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
            resultMap.put("brandList", brandList);
        }


        //规格查询  调用抽取出来的类方法
//        Map<String, Set<String>> specList = searchSpcList(nativeSearchQueryBuilder);
        Map<String, Set<String>> specList = searchSpcList(nativeSearchQueryBuilder);
        resultMap.put("specList", specList);*/

        //分组搜索实现
//        searchGroupList(nativeSearchQueryBuilder);
        Map<String, Object> groupMap = searchGroupList(nativeSearchQueryBuilder, searchMap);
        resultMap.putAll(groupMap);
        return resultMap;

    }


    /**分组查询>分类分组、品牌分组、规格分组
     * Map<String, String> searchMap)未被抽取时候在map方法中，抽取过来同样放到map方法中
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String, Object> searchGroupList(NativeSearchQueryBuilder nativeSearchQueryBuilder,Map<String, String> searchMap) {//分组查询抽取出来的方法
        /**
         * addAggregation添加一个聚合操作
         * 分组查询分类集合
         * terms取别名
         * field根据哪个域进行分组
         * size(50)查询50条数据
         */
        if (searchMap == null || searchMap.get("category") == null) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName").size(50));
        }
        if (searchMap == null || searchMap.get("brand") == null) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName").size(50));
        }
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(50));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        //
        /**获取分组数据
         * aggregatedPage.getAggregations()获取聚合数据集合，可以根据多个域进行分组
         * 现在根据一个域进行分组，get("skuCategory")获取指定域的集合数据
         * 使用aggregatedPage的实现类StringTerms接收数据，将数据转化成字符串数据
         */
        /**
         *
         */
        HashMap<String, Object> groupMapResult = new HashMap<>();
        if (searchMap == null || searchMap.get("category") == null) {
            StringTerms categoryTerms = aggregatedPage.getAggregations().get("skuCategory");
            List<String> categoryList = getGroupList(categoryTerms);
            groupMapResult.put("categoryList",categoryList);
        }
        if (searchMap == null || searchMap.get("brand") == null) {
            StringTerms brandTerms = aggregatedPage.getAggregations().get("skuBrand");
            List<String> brandList = getGroupList(brandTerms);
            groupMapResult.put("brandList",brandList);
        }
        StringTerms specTerms = aggregatedPage.getAggregations().get("skuSpec");
        /*//集合数据返回页面，使用list接收   这段代码抽取出来了
        List<String> categoryList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String categoryName = bucket.getKeyAsString();//其中的一个分类名字
            categoryList.add(categoryName);//添加数据
        }*/
        /*//调用抽取的公共方法获取分类分组集合数据  方法是选择调用，放到if判断条件中
        List<String> categoryList = getGroupList(categoryTerms);
        //调用抽取的公共方法获取品牌分组集合数据
        List<String> brandList = getGroupList(brandTerms);*/
        //调用抽取的公共方法获取规格分组集合数据
        List<String> specList = getGroupList(specTerms);
        Map<String, Set<String>> specMap = putAllSpec(specList);//实现合并操作
        groupMapResult.put("specList",specMap);
        return groupMapResult;


//        return categoryList;
        //不用方法返回测试看看能否成功
        /*System.out.println(categoryList);
        System.out.println(brandList);
        System.out.println(specList);*/
    }

    /**
     * 获取分组集合数据
     * @param stringTerms
     * @return
     */
    public List<String> getGroupList(StringTerms stringTerms){
        //集合数据返回页面，使用list接收
        List<String> groupList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String feildName = bucket.getKeyAsString();//其中的一个分类名字
            groupList.add(feildName);//添加数据
        }
        return groupList;
    }



    /**
     * 搜索条件封装
     *
     * @param searchMap
     * @return
     */
    public NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        //jdbc的过程：
        //查询条件
        //执行sql=select * from table = ==》ResultSet
        //循环ResultSet》Liast》JavaBean


        //NativeSearchQueryBuilder搜索条件构建对象，用于构建各种搜索条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //构建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //1.获取到关键字
        String keywords = searchMap.get("keywords");

        //根据关键词搜索
        if (searchMap != null && searchMap.size() > 0) {
            /*//根据关键词搜索
            String keywords = searchMap.get("keywords");
            //如果关键词不为空，搜关键词
            if (!StringUtils.isEmpty("keywords")) {//使用这个会报错
                nativeSearchQueryBuilder.withQuery(QueryBuilders.queryStringQuery(keywords).field("name"));
            }*/

            //关键词
            if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
                queryBuilder.must(QueryBuilders.matchQuery("name", searchMap.get("keywords")));
            }

            //分类筛选
            if (!StringUtils.isEmpty(searchMap.get("category"))) {
                queryBuilder.must(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
            }

            //品牌
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                queryBuilder.must(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
            }

            /**
             * keySet是键的集合，Set里面的类型即key的类型
             * entrySet是 键-值 对的集合，Set里面的类型是Map.Entry
             */
            //规格筛选过滤    spec_网络=联通4g&spec_颜色=红色
            for (String key : searchMap.keySet()) { //也可以使用searchMap.entrySet.for循环
                //如果是规格参数
                if (key.startsWith("spec_")) {//如果key以spec开始，表示规格查询
                    //索引的存储方式中数据在spechMap.规格名字.keyword中，所以也使用这种方式去查找,key从第五个之后开始截取：key.substring(5)
                    queryBuilder.must(QueryBuilders.matchQuery("specMap." + key.substring(5) + ".keyword", searchMap.get(key)));
                }
            }
            /*for (Map.Entry<String, String> entry : searchMap.entrySet()) {//使用视频的这种方法报错
                //如果是规格参数
                String key = entry.getKey();//这是对应的key
                if(key.startsWith("spec_")){//如果key以spec开始，表示规格查询
                    //规格条件的值
                    String value = entry.getValue();
                    queryBuilder.must(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value));
                }*/
            /**
             * 价格区间
             * 价格区间查询，每次需要将价格传入到后台，前端传入后台的价格大概是price=0-500元或者price=500-1000元依次类推，
             * 最后一个是price=3000元以上,后台可以根据-分割，如果分割得到的结果最多有2个，第1个表示x<price，第2个表示price<=y。
             * 去掉数字以外的元素   0-500  500-1000 1500-2000   .。。。。。
             * 根据-进行分割得到数组的形式prices[]  [0-500]。。。。。。
             * x一定不为空，y有可能为空
             * price[0]!=null     price>prices[0]
             * price[1]!=null     price<=price[1]
             */
            String price = searchMap.get("price");//获取前端传来的数据

            //直接前端选择范围的换算如100-200
            /*if (!StringUtils.isEmpty(price)){
                //根据-分割
                String[] array = price.split("-");
                //x<price
                queryBuilder.must(QueryBuilders.rangeQuery("price").gt(array[0]));
                if (array.length==2){
                    //price<y
                    queryBuilder.must(QueryBuilders.rangeQuery("price").gt(array[1]));
                }
            }*/

            if (!StringUtils.isEmpty(price)) {

                //去掉数字以外的元素   0-500  500-1000 1500-2000   .。。。。。
                price = price.replace("元", "").replace("以上", "");
                //根据-进行分割得到数组的形式prices[]  [0-500]。。。。。。
                String[] prices = price.split("-");

                if (prices != null && prices.length > 0) {//x一定不为空，y有可能为空
                    queryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));//gt大于
                    //price[0]!=null     price>prices[0]
                    // price[1]!=null     price<=price[1]
                    if (prices.length == 2) {
                        //rice<=price[1]
                        queryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));//lte小于
                    }
                }
            }


            //排序实现
            String sorField = searchMap.get("sortField");//指定排序的域
            String sortRule = searchMap.get("sortRule");//指定排序的规则
            if (!StringUtils.isEmpty(sorField) && !StringUtils.isEmpty(sortRule)) {
                nativeSearchQueryBuilder.withSort(
                        new FieldSortBuilder(sorField)//指定排序域
                        .order(SortOrder.valueOf(sortRule)));//指定排序规则
            }


            //分页，如果用户不选择分页那就默认为1
//            Integer pageNum = 1;//默认第一页
            Integer pageNum = converterPage(searchMap);//调用分页类方法
            Integer size = 10;//默认查询的数据条数
            nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, size));//第一页从0开始


        }

        //添加筛选条件
        nativeSearchQueryBuilder.withQuery(queryBuilder);

        return nativeSearchQueryBuilder;
    }

    /**
     * 接收前端传入发分页参数
     */
    public Integer converterPage(Map<String, String> searchMap) {
        /*if (searchMap != null) {//视频的方法有错误
            String pageNum = searchMap.get("pageNum");//获取pageNum,用pageNum接收
            try {
                return Integer.parseInt(pageNum);//转化为int类型
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 1;//默认返回1*/

        //构建分页查询
        Integer pageNum = 1;
        if (!StringUtils.isEmpty(searchMap.get("pageNum"))) {//从map中获取的是string类型
            try {
                pageNum = Integer.valueOf(searchMap.get("pageNum"));//转成Integer
            } catch (NumberFormatException e) {
                e.printStackTrace();
                pageNum=1;
            }
        }
        return pageNum;
    }


    /**
     * 结果集搜索
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String, Object> searchList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {

        //高亮配置
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");//指定高亮域,这里使用商品的名称name
        //前缀
        field.preTags("<em style=\"color:red\">");
        //后缀
        field.postTags("</em>");
        //碎片长度   关键词碎片的长度，截取前缀和后缀多少长度
        field.fragmentSize(100);


        //添加高亮搜索
        nativeSearchQueryBuilder.withHighlightFields(field);

        /**
         * 执行搜索，相应结果
         * 1搜索条件的封装
         * 2搜索的结果(集合数据)需要转换的类型
         * AggregatedPage<SkuInfo>搜索结果集的封装
         */
//        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> page = elasticsearchTemplate
                .queryForPage(
                        nativeSearchQueryBuilder.build(), //搜索条件的封装
                        SkuInfo.class,//数据集合要转换的类型的字节码
                        //SearchResultMapper//执行搜索后将数据封装到该对象中,这是一个接口，需要给他创建一个实现类
                        new SearchResultMapper(){

                            @Override
                            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                                //存储所有转换后的高亮数据对象
                                List<T> list = new ArrayList<T>();//用于存放获取到的高亮数据，注意这里的使用泛型T不能漏

                                //执行查询，获取所有数据》结果集[非高亮数据|高亮数据] 方法里提供了response
                                for (SearchHit hit : response.getHits()) {
                                    //分析数据结果，获取非高亮数据
                                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(),SkuInfo.class);

                                    //分析结果集，获取高亮数据》只有某个域的数据高亮
                                    HighlightField highlightField = hit.getHighlightFields().get("name");

                                    if (highlightField!=null&&highlightField.getFragments()!=null){
                                        //高亮数据读取出来
                                        Text[] fragments = highlightField.getFragments();//得到一个集合对象，下面循环将数据取出来
                                        StringBuffer buffer = new StringBuffer();//创建一个对象获取集合的数据
                                        for (Text fragment : fragments) {
                                            buffer.append(fragment.toString());
                                        }
                                        //非高亮数据中指定的域替换成高亮数据
                                        skuInfo.setName(buffer.toString());
                                    }
                                    //将高亮数据添加到集合
                                    list.add((T) skuInfo);//不知道什么类型直接使用泛型T


                                    //将数据返回
                                }

                                /**
                                 * 搜索的集合数据
                                 * public AggregatedPageImpl(List<T> content, Pageable pageable, long total)
                                 * 1搜索的集合数据content2分页对象数据pageable3搜索记录的总条数total
                                 */
                                return new AggregatedPageImpl<T>(list,pageable,response.getHits().totalHits);
                            }
                        });

        /*//分类分组查询  抽取方法后移动到下面
        List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);*/


        long totalElements = page.getTotalElements();//总记录数
        //总页数
        int totalPages = page.getTotalPages();

        //获取数据结果集   所有的数据存储的集合SkuInfo
        List<SkuInfo> contents = page.getContent();
        //分析数据



        //获取数据结果集
        //封装一个map存储数据并返回
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("rows", contents);//数据添加到对象rows
        resultMap.put("total", totalElements);//
        resultMap.put("totalPages", totalPages);

        //获取搜索封装信息
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        Pageable pageable = query.getPageable();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        //分页数据
        resultMap.put("pageSize", pageSize);//
        resultMap.put("pageNumber", pageNumber);
        return resultMap;
    }


    /**
     * 规格分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         *分组查询规格集合
         * addAggregation():添加一个集合操作
         * terms()取别名
         * field()根据哪个域进行分组
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .terms("skuSpec").field("spec.keyword").size(10000));//spec.keyword表示一组数据都不要分组,默认只显示10条，size(10000)
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()获取聚合数据的集合，可以根据多个域进行分组
         * .get("skuSpec")获取指定域的集合数   多条不重复的[{"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}]
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");

        List<String> specList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {//注意生成循环时使用类StringTerms报错
            String brandName = bucket.getKeyAsString();//其中的一个数据规格名字
            specList.add(brandName);
        }

        //规格汇总合并   调用抽取出来的规格汇总方法
        Map<String, Set<String>> allSpec = putAllSpec(specList);

//        System.out.println(allSpec);
        return allSpec;
    }

    /**
     * 规格汇总合并
     *
     * @param specList
     * @return
     */
    public Map<String, Set<String>> putAllSpec(List<String> specList) {
        //3合并后的map对象  将每个map对象合并成一个Map<String,Set<String>>
        Map<String, Set<String>> allSpec = new HashMap<String, Set<String>>();
        //1循环specList   spec={"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
        for (String spec : specList) {
            //2将每个json字符串转成map
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);

            //4合并流程,循环所有map
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                //4.1取出当前map，获取对应的key，以及对应的value
                String key = entry.getKey();  //规格名字
                String value = entry.getValue(); //规格值

                //4.2将当前循环的数据合并到一个Map<String,Set<String>>中
                /*注意，这种方式不行，因为new了一个对象会覆盖原来的值，value无法合并到原来的对象中
                Set<String> specSet = new HashSet<>();
                specSet.add(value);
                allSpec.put(key,specSet);*/
                //从allSpec中获取当前规格对应的Set集合数据
                Set<String> specSet = allSpec.get(key);
                if (specSet == null) {
                    //之前allSpec中没用规格
                    specSet = new HashSet<>();
                }

                specSet.add(value);
                allSpec.put(key, specSet);
            }
        }
        return allSpec;
    }


    /**
     * 品牌分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {//分组查询抽取出来的方法
        /**
         * addAggregation添加一个聚合操作
         * 分组查询品牌集合
         * terms取别名
         * field根据哪个域进行分组
         * size(50)查询50条数据
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName").size(50));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        //
        /**获取分组数据
         * aggregatedPage.getAggregations()获取聚合数据集合，可以根据多个域进行分组
         * 现在根据一个域进行分组，get("skuCategory")获取指定域的集合数据
         * 使用aggregatedPage的实现类StringTerms接收数据，将数据转化成字符串数据
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuBrand");
        //集合数据返回页面，使用list接收
        List<String> brandList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String brandName = bucket.getKeyAsString();//其中的一个品牌名字
            brandList.add(brandName);//添加数据
        }
        return brandList;
    }

    /**
     * 分类分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {//分组查询抽取出来的方法
        /**
         * addAggregation添加一个聚合操作
         * 分组查询分类集合
         * terms取别名
         * field根据哪个域进行分组
         * size(50)查询50条数据
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName").size(50));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        //
        /**获取分组数据
         * aggregatedPage.getAggregations()获取聚合数据集合，可以根据多个域进行分组
         * 现在根据一个域进行分组，get("skuCategory")获取指定域的集合数据
         * 使用aggregatedPage的实现类StringTerms接收数据，将数据转化成字符串数据
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuCategory");
        //集合数据返回页面，使用list接收
        List<String> categoryList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String categoryName = bucket.getKeyAsString();//其中的一个分类名字
            categoryList.add(categoryName);//添加数据
        }
        return categoryList;
    }


    /**
     * 导入索引
     */
    @Override
    public void importData() {

        //feign调用，查询List<Sku>
        Result<List<Sku>> skuResult = skuFeign.findAll();
        //将List<Sku>转成List<SkuInfo>
        /**List<Sku>->[{skuJSON}]->List<SkuInfo>
         * List<Sku>集合转成json后是集合数据里面有一个对象[{skuJSON}]，skuJSON是普通的字符串想转什么对象就转什么对象->List<SkuInfo>
         */
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuResult.getData()), SkuInfo.class);
//        循环当前的skuInfoList
        for (SkuInfo skuInfo : skuInfoList) {
//            获取spec->Map(String)->Map类型    {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
//            如果需要生成动态的域，只需要将该域存到一个Map(String,Object)对象即可，该域Map（String,Object）的key会生成一个域，域的名字为该map的key
//        当前Map<String,Object>后面的Object的值会作为当前Sku对象该域(key)对应的值
            skuInfo.setSpecMap(specMap);
        }
        //调用dao实现批量数据导入
        skuEsMapper.saveAll(skuInfoList);
    }
}
