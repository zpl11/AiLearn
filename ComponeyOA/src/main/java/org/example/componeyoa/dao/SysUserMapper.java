package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysUser;

import java.util.List;

@Mapper
public interface SysUserMapper {

  // 1. 查询所有未删除的用户（注意 FROM 前加空格，且建议过滤已删除数据 del_flag = 0）
  @Select("SELECT user_id, dept_id, user_name, nick_name, phone, email, sex, status, create_time " +
          "FROM sys_user WHERE del_flag = 0")
  List<SysUser> selectAll();

  // 2. 真实模糊查询（使用 MySQL 的 CONCAT 函数拼接通配符 %）
  @Select("SELECT user_id, dept_id, user_name, nick_name, phone, email, sex, status, create_time " +
          "FROM sys_user " +
          "WHERE del_flag = 0 AND nick_name LIKE CONCAT('%', #{nickName}, '%')")
  List<SysUser> selectByNickNameLike(@Param("nickName") String nickName);

  // 3. 新增用户（补全表名 sys_user 及括号，必填项 user_name 和 password 建议一并传入）
  @Insert("INSERT INTO sys_user(dept_id, user_name, nick_name, password, phone, email, sex, create_by) " +
          "VALUES(#{deptId}, #{userName}, #{nickName}, #{password}, #{phone}, #{email}, #{sex}, #{createBy})")
  int addUser(SysUser sysUser);
  // 提示：MyBatis 的 Insert/Update 返回值底层是受影响行数（int），Spring 会自动转为 Boolean（大于0为true），直接传对象更清晰

  // 4. 逻辑删除员工（del_flag 置为 1，类型对应为 Integer 与 Long）
  @Update("UPDATE sys_user SET del_flag = 1, update_time = NOW() " +
          "WHERE user_id = #{userId}")
  int delUser(@Param("userId") Long userId);

  // 5. 修改员工姓名（必须指定 WHERE 条件限制具体员工）
  @Update("UPDATE sys_user " +
          "SET dept_id = #{deptId}, " +
          "    nick_name = #{nickName}, " +
          "    phone = #{phone}, " +
          "    email = #{email}, " +
          "    sex = #{sex}, " +
          "    status = #{status}, " +
          "    update_time = NOW() " +
          "WHERE user_id = #{userId}")
  int updateUser(SysUser sysUser);


//  点击左侧单位树得到人员列表
  @Select("SELECT user_id, dept_id, user_name, nick_name, phone, email, sex, status, create_time " +
          "FROM sys_user " +
          "WHERE del_flag = 0 AND dept_id = #{deptId}")
  List<SysUser> selectByDeptId(@Param("deptId") Long deptId);

// 点击编辑回显用户数据
  @Select("SELECT user_id, dept_id, user_name, nick_name, phone, email, sex, status, create_time " +
          "FROM sys_user " +
          "WHERE user_id = #{userId} AND del_flag = 0")
  SysUser selectById(@Param("userId") Long userId);


  // 新增用户时候查看账户是否已经被占用
  @Select("SELECT user_id, dept_id, user_name, nick_name, password, phone, email, sex, status, del_flag " +
          "FROM sys_user " +
          "WHERE user_name = #{userName} AND del_flag = 0 LIMIT 1")
  SysUser selectByUserName(@Param("userName") String userName);


  // 快速停用/启用用户
  @Update("UPDATE sys_user SET status = #{status}, update_time = NOW() " +
          "WHERE user_id = #{userId}")
  int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

}