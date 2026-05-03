package com.erp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.system.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT p.permission_code FROM sys_menu p " +
            "INNER JOIN sys_role_menu rm ON p.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.permission_code IS NOT NULL AND p.permission_code != ''")
    List<String> getPermissionsByUserId(Long userId);

    @Select("SELECT DISTINCT r.role_code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> getRoleCodesByUserId(Long userId);

    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> getRoleIdsByUserId(Long userId);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteUserRoles(Long userId);

    @Insert("<script>" +
            "INSERT INTO sys_user_role (user_id, role_id) VALUES " +
            "<foreach collection='roleIds' item='r' separator=','>(#{userId}, #{r})</foreach>" +
            "</script>")
    void insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
