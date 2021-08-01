package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username,String password);

    /**
     * 注册
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 校验用户名和邮箱是否存在
     * @param str
     * @param type 类型 userName email
     * @return
     */
    ServerResponse<String> checkValid(String str,String type);

    /**
     * 忘记密码-查找问题
     * @param username
     * @return
     */
    ServerResponse<String> forgetGetQuestion(String username);

    /**
     * 忘记密码-提交答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username,String question,String answer);

    /**
     * 忘记密码-重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> resetPassword(String username,String passwordNew,String forgetToken);

    /**
     * 登录状态下重置密码
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    /**
     * 登录状态更新个人信息
     * @return
     */
    ServerResponse<User> updateInformation(User user);

    /**
     * 获取当前登录用户的详细信息，并强制登录
     * @param userId
     * @return
     */
    ServerResponse<User> getInformation(Integer userId);

    /**
     * 检查是否管理员
     * @param user
     * @return
     */
    ServerResponse checkAdminRole(User user);
}
