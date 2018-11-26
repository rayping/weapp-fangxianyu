package io.github.nnkwrik.goodsservice.service.impl;

import com.github.pagehelper.PageHelper;
import io.github.nnkwrik.goodsservice.dao.CategoryMapper;
import io.github.nnkwrik.goodsservice.dao.GoodsMapper;
import io.github.nnkwrik.goodsservice.dao.OtherMapper;
import io.github.nnkwrik.goodsservice.model.po.*;
import io.github.nnkwrik.goodsservice.model.vo.CatalogVo;
import io.github.nnkwrik.goodsservice.model.vo.IndexVO;
import io.github.nnkwrik.goodsservice.model.vo.inner.BannerVo;
import io.github.nnkwrik.goodsservice.model.vo.inner.CategoryVo;
import io.github.nnkwrik.goodsservice.model.vo.inner.ChannelVo;
import io.github.nnkwrik.goodsservice.model.vo.inner.GoodsSimpleVo;
import io.github.nnkwrik.goodsservice.service.IndexService;
import io.github.nnkwrik.goodsservice.util.PO2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nnkwrik
 * @date 18/11/16 15:23
 */
@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private OtherMapper otherMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public IndexVO getIndex() {
        //广告
        List<Ad> adList = otherMapper.findAd();
        List<BannerVo> bannerVoList = PO2VO.convertList(PO2VO.banner, adList);

        //分类
        List<Channel> channelList = otherMapper.findChannel();
        List<ChannelVo> channelVoList = PO2VO.convertList(PO2VO.channel, channelList);

        //推荐商品
        int pageNum = 1;
        int pageSize = 10;
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> goodsList = goodsMapper.findSimpleGoods();
        List<GoodsSimpleVo> goodsSimpleVOList = PO2VO.convertList(PO2VO.goodsSimple, goodsList);

        return new IndexVO(goodsSimpleVOList, bannerVoList, channelVoList);
    }

    @Override
    public CatalogVo getCatalogIndex() {
        List<Category> mainCategory = categoryMapper.findMainCategory();
        Category currentCate = mainCategory.get(0);
        List<Category> subCategory = categoryMapper.findSubCategory(currentCate.getId());


        List<CategoryVo> mainCategoryVo = PO2VO.convertList(PO2VO.category, mainCategory);
        List<CategoryVo> subCategoryVo = PO2VO.convertList(PO2VO.category, subCategory);
        CategoryVo currentCategoryVo =
                new CategoryVo(currentCate.getId(), currentCate.getName(), currentCate.getIconUrl(), subCategoryVo);

        return new CatalogVo(mainCategoryVo, currentCategoryVo);
    }

    @Override
    public CatalogVo getCatalogById(int id) {
        List<Category> subCategory = categoryMapper.findSubCategory(id);
        List<CategoryVo> subCategoryVo = PO2VO.convertList(PO2VO.category, subCategory);
        Category currentCategory = categoryMapper.findCategoryById(id);
        CategoryVo currentCategoryVo = PO2VO.convert(PO2VO.category, currentCategory);
        currentCategoryVo.setSubCategoryList(subCategoryVo);

        return new CatalogVo(null, currentCategoryVo);
    }

    @Override
    public void collectAddOrDelete(int goodsId, String userId, boolean hasCollect) {
        if (hasCollect) {
            otherMapper.deleteUserCollect(userId, goodsId);
        } else {
            otherMapper.setUserCollect(userId, goodsId);
        }
    }

    @Override
    public void addComment(int goodsId, String userId, int replyCommentId, String replyUserId, String content) {
        otherMapper.addComment(goodsId,userId,replyCommentId,replyUserId,content);
    }


}