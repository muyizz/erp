package com.erp.inventory.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.inventory.entity.InvStock;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface InvStockMapper extends BaseMapper<InvStock> {
    @org.apache.ibatis.annotations.Update("INSERT INTO inv_stock (material_id, warehouse_id, quantity, updated_at) VALUES (#{materialId}, #{warehouseId}, #{qty}, NOW()) ON CONFLICT (material_id, warehouse_id) DO UPDATE SET quantity = inv_stock.quantity + EXCLUDED.quantity, updated_at = NOW()")
    void upsert(@org.apache.ibatis.annotations.Param("materialId") Long materialId, @org.apache.ibatis.annotations.Param("warehouseId") Long warehouseId, @org.apache.ibatis.annotations.Param("qty") java.math.BigDecimal qty);
    @org.apache.ibatis.annotations.Update("UPDATE inv_stock SET quantity = quantity - #{qty} WHERE material_id = #{materialId} AND warehouse_id = #{warehouseId} AND quantity >= #{qty}")
    int deduct(@org.apache.ibatis.annotations.Param("materialId") Long materialId, @org.apache.ibatis.annotations.Param("warehouseId") Long warehouseId, @org.apache.ibatis.annotations.Param("qty") java.math.BigDecimal qty);
}
