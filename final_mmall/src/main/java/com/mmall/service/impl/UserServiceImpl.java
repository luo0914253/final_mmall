package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, password);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    /**
     * 注册
     */
    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> response = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!response.isSuccess()){
            return response;
        }
        response = this.checkValid(user.getUsername(),Const.EMAIL);
        if (!response.isSuccess()){
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 校验用户名和邮箱是否存在
     * @param str
     * @param type 类型 userName email
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(str)){
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount >0){
                    return ServerResponse.createByErrorMessage("email已经存在");
                }
            }else if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount >0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 忘记密码-查找问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse<String> checkValid = this.checkValid(username, Const.USERNAME);
        if(checkValid.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
    }

    /**
     * 忘记密码-提交答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(Const.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    /**
     * 忘记密码-重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse<String> checkValid = this.checkValid(username, Const.USERNAME);
        if(checkValid.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(Const.TOKEN_PREFIX + username);
        if (StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("token已经失效");
        }
        if (StringUtils.equals(forgetToken,token)){
            String password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePassword(username,password);
            if (resultCount > 0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else {
            return ServerResponse.createBySuccess("Token错误，请重新获取重置密码的Token");
        }
        return ServerResponse.createByErrorMessage("修改密码成功");
    }

    /**
     * 登录状态下重置密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        passwordOld = MD5Util.MD5EncodeUtf8(passwordOld);
        int resultCount = userMapper.checkPassword(passwordOld,user.getId());
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码输入错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount >0){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 登录状态更新个人信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if (resultCount >0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     * 获取当前登录用户的详细信息，并强制登录
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 检查是否管理员
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
