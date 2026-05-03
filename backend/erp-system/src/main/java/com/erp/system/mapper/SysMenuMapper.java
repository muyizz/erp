package com.erp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.type IN (1, 2) AND m.visible = 1 " +
            "ORDER BY m.sort_order")
    List<SysMenu> getMenusByUserId(Long userId);

    @Select("SELECT m.* FROM sys_menu m WHERE m.parent_id = #{parentId} ORDER BY m.sort_order")
    List<SysMenu> getChildrenByParentId(Long parentId);
}
