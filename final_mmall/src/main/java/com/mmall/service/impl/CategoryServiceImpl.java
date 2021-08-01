package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 添加分类
     * @param parentId 父节点id
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<Category> addCategory(Integer parentId, String categoryName) {
        if (StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int resultCount = categoryMapper.insertSelective(category);
        if (resultCount >0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createBySuccessMessage("添加品类失败");
    }

    /**
     * 修改品类名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null ||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改品类名称参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount >0){
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字成功");
    }

    /**
     * 获取品类子节点(平级)
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getCategory(int categoryId) {
        List<Category> categoryList = categoryMapper.selectChildrenCategory(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            return ServerResponse.createByErrorMessage("未找到该品类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId) {
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
//      TODO 逻辑没搞明白，一脸懵
        addCategoryIds(categoryIds,categoryId);
        return ServerResponse.createBySuccess(categoryIds);
    }
    private void addCategoryIds(List<Integer> categoryIds,Integer categoryId){
        List<Category> categoryList = categoryMapper.selectChildrenCategory(categoryId);
        if (CollectionUtils.isNotEmpty(categoryList)){
            for (Category category:categoryList){
                categoryIds.add(category.getId());
                addCategoryIds(categoryIds,category.getId());
            }
        }
    }

}
