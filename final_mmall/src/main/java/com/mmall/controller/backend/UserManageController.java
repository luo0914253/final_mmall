package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping("/manage/user/")
@Controller
public class UserManageController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()){
            User user = response.getData();
            if (user.getRole().equals(Const.Role.ROLE_ADMIN)){
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员，无操作权限");
            }
        }
        return response;
    }
}
