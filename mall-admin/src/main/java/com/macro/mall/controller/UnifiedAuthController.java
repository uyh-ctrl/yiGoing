package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UnifiedLoginParam;
import com.macro.mall.dto.MemberRegisterParam;
import com.macro.mall.dto.UnifiedLoginResult;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.UmsAdminService;
import com.macro.mall.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class UnifiedAuthController {
    private final UmsAdminService adminService;
    private final UmsMemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}") private String tokenHead;

    public UnifiedAuthController(UmsAdminService adminService, UmsMemberMapper memberMapper,
                                 PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public CommonResult<UnifiedLoginResult> login(@Valid @RequestBody UnifiedLoginParam request) {
        UmsAdmin admin = adminService.getAdminByUsername(request.username());
        if (admin != null) {
            String token = adminService.login(request.username(), request.password());
            if (token == null) return CommonResult.validateFailed("用户名或密码错误");
            return CommonResult.success(new UnifiedLoginResult("ADMIN", token, tokenHead,
                    admin.getNickName() == null ? admin.getUsername() : admin.getNickName(), admin.getIcon()));
        }
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(request.username()).andStatusEqualTo(1);
        List<UmsMember> members = memberMapper.selectByExample(example);
        if (members.isEmpty() || !passwordEncoder.matches(request.password(), members.get(0).getPassword())) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        UmsMember member = members.get(0);
        return CommonResult.success(new UnifiedLoginResult("MEMBER", jwtTokenUtil.generateMemberToken(member.getUsername()), tokenHead,
                member.getNickname() == null ? member.getUsername() : member.getNickname(), member.getIcon()));
    }

    @PostMapping("/register")
    public CommonResult<Void> register(@Valid @RequestBody MemberRegisterParam request) {
        if (adminService.getAdminByUsername(request.username()) != null) {
            return CommonResult.validateFailed("用户名已被占用");
        }
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(request.username());
        if (!memberMapper.selectByExample(example).isEmpty()) {
            return CommonResult.validateFailed("用户名已被占用");
        }
        UmsMember member = new UmsMember();
        member.setUsername(request.username());
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setPhone(request.phone());
        member.setNickname(request.username());
        member.setStatus(1);
        member.setCreateTime(new Date());
        member.setMemberLevelId(4L);
        memberMapper.insertSelective(member);
        return CommonResult.success(null);
    }
}
