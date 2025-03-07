package com.BINexus.back.controller;

import cn.hutool.core.util.NumberUtil;
import com.BINexus.back.annotation.AuthCheck;
import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.common.DeleteRequest;
import com.BINexus.back.common.ErrorCode;
import com.BINexus.back.common.ResultUtils;
import com.BINexus.back.constant.UserConstant;
import com.BINexus.back.exception.BusinessException;
import com.BINexus.back.exception.ThrowUtils;
import com.BINexus.back.model.dto.user.UserAddRequest;
import com.BINexus.back.model.dto.user.UserUpdateRequest;
import com.BINexus.back.model.entity.User;
import com.BINexus.back.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class AdminController {

    @Autowired
    UserService userService;

    @PostMapping("/addUser")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest){
        ThrowUtils.throwIf(Objects.isNull(userAddRequest), ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest,user);
        boolean saved = userService.save(user);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        Long newUserId = user.getId();
        return ResultUtils.success(newUserId);
    }

    @PostMapping("/deleteUser")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest){
        ThrowUtils.throwIf(Objects.isNull(deleteRequest)|| deleteRequest.getId()<0, ErrorCode.PARAMS_ERROR);
        boolean result = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

}
