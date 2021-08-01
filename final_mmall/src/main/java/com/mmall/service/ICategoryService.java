package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    /**
     * 添加分类
     * @param parentId 父节点id
     * @param categoryName
     * @return
     */
    ServerResponse<Category> addCategory(Integer parentId, String categoryName);

    /**
     * 修改品类名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 获取品类子节点(平级)
     * @param categoryId
     * @return
     */
    ServerResponse<List<Category>> getCategory(int categoryId);

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> getDeepCategory(Integer categoryId);
}
