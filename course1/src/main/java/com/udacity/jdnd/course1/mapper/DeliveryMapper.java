package com.udacity.jdnd.course1.mapper;

import com.udacity.jdnd.course1.model.Delivery;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DeliveryMapper {

  @Select("SELECT * FROM DELIVERY WHERE id = #{id}")
  Delivery findDelivery(Integer id);

  @Insert("INSERT INTO DELIVERY(orderId, time) VALUES (#{orderId},#{time})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  Integer insertDelivery(Delivery delivery);

  @Delete("DELETE FROM DELIVERY WHERE id=#{id}")
  void deleteDelivery(Integer id);
}