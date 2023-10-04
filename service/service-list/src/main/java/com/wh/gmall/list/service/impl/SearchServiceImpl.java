package com.wh.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tdunning.math.stats.AVLTreeDigest;
import com.wh.gmall.list.repository.GoodsRepository;
import com.wh.gmall.list.service.SearchService;
import com.wh.gmall.model.list.*;
import com.wh.gmall.model.product.*;
import com.wh.gmall.product.client.ProductFeignClient;
import org.apache.lucene.search.join.ScoreMode;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.ParsedAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void upperGoods(Long skuId) {
        Goods goods = new Goods();

        //查询sku对应的平台属性
        List<BaseAttrInfo> baseAttrInfoList = productFeignClient.getAttrList(skuId);
        if(null != baseAttrInfoList) {
            List<SearchAttr> searchAttrList =  baseAttrInfoList.stream().map(baseAttrInfo -> {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(baseAttrInfo.getId());
                searchAttr.setAttrName(baseAttrInfo.getAttrName());
                //一个sku只对应一个属性值
                List<BaseAttrValue> baseAttrValueList = baseAttrInfo.getAttrValueList();
                searchAttr.setAttrValue(baseAttrValueList.get(0).getValueName());
                return searchAttr;
            }).collect(Collectors.toList());

            goods.setAttrs(searchAttrList);
        }

        //查询sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 查询品牌
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        if (baseTrademark != null){
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        }

        // 查询分类
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        if (baseCategoryView != null) {
            goods.setCategory1Id(baseCategoryView.getCategory1Id());
            goods.setCategory1Name(baseCategoryView.getCategory1Name());
            goods.setCategory2Id(baseCategoryView.getCategory2Id());
            goods.setCategory2Name(baseCategoryView.getCategory2Name());
            goods.setCategory3Id(baseCategoryView.getCategory3Id());
            goods.setCategory3Name(baseCategoryView.getCategory3Name());
        }

        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setId(skuInfo.getId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setCreateTime(new Date());

        goodsRepository.save(goods);
    }

    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public void incrHotScore(Long skuId){
        // 定义key
        String hotKey = "hotScore";
//        if(!redisTemplate.hasKey(hotKey)){
//            redisTemplate.opsForZSet().add(hotKey, "skuId:" + skuId, 0);
//        }
        // 保存数据

        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId:" + skuId, 1);
        if ( hotScore % 10 == 0 ){
            // 更新es
            Optional<Goods> optional = goodsRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(Math.round(hotScore));
            goodsRepository.save(goods);
        }
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) throws IOException {
        SearchRequest searchRequest = buildQuery(searchParam);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchResponseVo searchResponseVo = parseResponseVo(searchResponse);
        searchResponseVo.setPageNo(searchParam.getPageNo());
        searchResponseVo.setPageSize(searchParam.getPageSize());
        searchResponseVo.setTotalPages(
                (searchResponseVo.getTotal() + searchResponseVo.getPageSize() - 1) / searchResponseVo.getPageSize());
        return searchResponseVo;
    }

    private SearchRequest buildQuery(SearchParam searchParam){
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilderOuter = QueryBuilders.boolQuery();
        //关键字
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            MatchQueryBuilder mustTitle = QueryBuilders.matchQuery("title", searchParam.getKeyword());
            boolQueryBuilderOuter.must(mustTitle);
        }

        //品牌过滤 2:华为
        if (!StringUtils.isEmpty(searchParam.getTrademark())){
            String[] split = searchParam.getTrademark().split(":");
            if (split.length == 2){
                TermQueryBuilder termTmId = QueryBuilders.termQuery("tmId", split[0]);
                boolQueryBuilderOuter.filter(termTmId);
            }
        }

        //分类过滤
        if (!StringUtils.isEmpty(searchParam.getCategory1Id())){
            TermQueryBuilder termCate1Id = QueryBuilders.termQuery("category1Id", searchParam.getCategory1Id());
            boolQueryBuilderOuter.filter(termCate1Id);
        }
        if (!StringUtils.isEmpty(searchParam.getCategory2Id())){
            TermQueryBuilder termCate2Id = QueryBuilders.termQuery("category2Id", searchParam.getCategory2Id());
            boolQueryBuilderOuter.filter(termCate2Id);
        }
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())){
            TermQueryBuilder termCate3Id = QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id());
            boolQueryBuilderOuter.filter(termCate3Id);
        }

        //平台属性过滤 平台属性Id:平台属性值:平台属性名
        if (null != searchParam.getProps() && searchParam.getProps().length > 0){

            for (String prop : searchParam.getProps()) {
                String[] split = prop.split(":");
                BoolQueryBuilder boolQueryBuilderInner = QueryBuilders.boolQuery();
                boolQueryBuilderInner.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                boolQueryBuilderInner.must(QueryBuilders.termQuery("attrs.attrId", split[0]));

                NestedQueryBuilder nestedAttrs = QueryBuilders.nestedQuery("attrs", boolQueryBuilderInner, ScoreMode.None);
                BoolQueryBuilder boolQueryBuilderMiddle = QueryBuilders.boolQuery().must(nestedAttrs);
                boolQueryBuilderOuter.filter(boolQueryBuilderMiddle);
            }
        }
        searchSourceBuilder.query(boolQueryBuilderOuter);

        //分页
        searchSourceBuilder.from((searchParam.getPageNo() - 1) * searchParam.getPageSize());
        searchSourceBuilder.size(searchParam.getPageSize());

        //排序 1:asc
        //1:hotScore 2:price
        String order = searchParam.getOrder();
        if (null != order){
            String[] split = order.split(":");
            if (split.length == 2){
                String keyword = split[0].equals("1") ? "hotScore" : "price";
                SortOrder sequence = split[1].equals("asc") ? SortOrder.ASC : SortOrder.DESC;

                searchSourceBuilder.sort(keyword, sequence);
            } else {
                searchSourceBuilder.sort("hotScore", SortOrder.DESC);
            }
        }

        //聚合-品牌
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId");
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"));
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(tmIdAgg);

        //聚合-属性
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName");
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue");

        attrIdAgg.subAggregation(attrNameAgg);
        attrIdAgg.subAggregation(attrValueAgg);
        attrAgg.subAggregation(attrIdAgg);

        searchSourceBuilder.aggregation(attrAgg);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style=color:red>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        //结果过滤
        searchSourceBuilder.fetchSource(new String[]{"id", "defaultImg", "title", "price"}, null);


        searchRequest.source(searchSourceBuilder);

        System.out.println(searchSourceBuilder.toString());

        return searchRequest;
    }

    private SearchResponseVo parseResponseVo(SearchResponse searchResponse){
        SearchResponseVo searchResponseVo = new SearchResponseVo();

        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        SearchHit[] hits = searchResponse.getHits().getHits();

        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregationMap.get("tmIdAgg");
        List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();

        if(!CollectionUtils.isEmpty(buckets)){
            List<SearchResponseTmVo> trademarkList = buckets.stream().map(bucket -> {
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();

                //获取品牌id
                Long tmId = bucket.getKeyAsNumber().longValue();
                searchResponseTmVo.setTmId(tmId);

                Map<String, Aggregation> bucketMap = bucket.getAggregations().getAsMap();

                //获取品牌Name
                ParsedStringTerms tmNameAgg = (ParsedStringTerms) bucketMap.get("tmNameAgg");
                String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);

                //获取品牌logo
                ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) bucketMap.get("tmLogoUrlAgg");
                String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);

                return searchResponseTmVo;
            }).collect(Collectors.toList());
            searchResponseVo.setTrademarkList(trademarkList);
        }

        ParsedNested attrAgg = (ParsedNested) aggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> bucketsAttr = attrIdAgg.getBuckets();

        if(!CollectionUtils.isEmpty(bucketsAttr)){
            List<SearchResponseAttrVo> attrList = bucketsAttr.stream().map(bucket -> {
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();

                //获取
                Long attrId = bucket.getKeyAsNumber().longValue();
                searchResponseAttrVo.setAttrId(attrId);

                Map<String, Aggregation> bucketMap = bucket.getAggregations().getAsMap();

                //获取
                ParsedStringTerms tmNameAgg = (ParsedStringTerms) bucketMap.get("attrNameAgg");
                String attrName = tmNameAgg.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);

                //获取
                ParsedStringTerms attrValueAgg = (ParsedStringTerms) bucketMap.get("attrValueAgg");
                List<? extends Terms.Bucket> bucketsAttrValue = attrValueAgg.getBuckets();
                if(!CollectionUtils.isEmpty(bucketsAttrValue)){
                    List<String> attrValueList = bucketsAttrValue.stream()
                            .map(MultiBucketsAggregation.Bucket::getKeyAsString)
                            .collect(Collectors.toList());
                    searchResponseAttrVo.setAttrValueList(attrValueList);
                }

                return searchResponseAttrVo;
            }).collect(Collectors.toList());
            searchResponseVo.setAttrsList(attrList);
        }

        ArrayList<Goods> goodsList = new ArrayList<>();
        if(null != hits && hits.length > 0){
            for (SearchHit hit : hits) {
                Goods goods = JSONObject.parseObject(hit.getSourceAsString(), Goods.class);
                if (hit.getHighlightFields().get("title") != null) {
                    Text title = hit.getHighlightFields().get("title").getFragments()[0];
                    goods.setTitle(title.toString());
                }
                goodsList.add(goods);
            }
        }
        searchResponseVo.setGoodsList(goodsList);

        searchResponseVo.setTotal(searchResponse.getHits().getTotalHits().value);

        return searchResponseVo;
    }
}
