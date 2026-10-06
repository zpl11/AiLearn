package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysOperLog;
import org.example.componeyoa.entity.dto.SysOperLogQueryDTO;

import java.util.List;

@Mapper
public interface SysOperLogMapper {

    /**
     * 新增操作审计日志
     */
    @Insert("INSERT INTO sys_oper_log(" +
            "title, business_type, method, request_method, oper_name, dept_name, " +
            "oper_url, oper_ip, oper_param, json_result, status, error_msg, cost_time, oper_time) " +
            "VALUES(" +
            "#{title}, #{businessType}, #{method}, #{requestMethod}, #{operName}, #{deptName}, " +
            "#{operUrl}, #{operIp}, #{operParam}, #{jsonResult}, #{status}, #{errorMsg}, #{costTime}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "operId")
    int insertOperLog(SysOperLog operLog);

    /**
     * 多条件分页查询操作日志列表
     */
    @Select("<script>" +
            "SELECT oper_id, title, business_type, method, request_method, oper_name, dept_name, " +
            "       oper_url, oper_ip, oper_param, json_result, status, error_msg, cost_time, oper_time " +
            "FROM sys_oper_log " +
            "<where>" +
            "  <if test=\"title != null and title != ''\">" +
            "    AND title LIKE CONCAT('%', #{title}, '%')" +
            "  </if>" +
            "  <if test=\"operName != null and operName != ''\">" +
            "    AND oper_name LIKE CONCAT('%', #{operName}, '%')" +
            "  </if>" +
            "  <if test=\"businessType != null\">" +
            "    AND business_type = #{businessType}" +
            "  </if>" +
            "  <if test=\"status != null\">" +
            "    AND status = #{status}" +
            "  </if>" +
            "  <if test=\"beginTime != null and beginTime != ''\">" +
            "    AND oper_time &gt;= #{beginTime}" +
            "  </if>" +
            "  <if test=\"endTime != null and endTime != ''\">" +
            "    AND oper_time &lt;= #{endTime}" +
            "  </if>" +
            "</where> " +
            "ORDER BY oper_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<SysOperLog> selectOperLogList(SysOperLogQueryDTO queryDTO);

    /**
     * 多条件统计总记录数
     */
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM sys_oper_log " +
            "<where>" +
            "  <if test=\"title != null and title != ''\">" +
            "    AND title LIKE CONCAT('%', #{title}, '%')" +
            "  </if>" +
            "  <if test=\"operName != null and operName != ''\">" +
            "    AND oper_name LIKE CONCAT('%', #{operName}, '%')" +
            "  </if>" +
            "  <if test=\"businessType != null\">" +
            "    AND business_type = #{businessType}" +
            "  </if>" +
            "  <if test=\"status != null\">" +
            "    AND status = #{status}" +
            "  </if>" +
            "  <if test=\"beginTime != null and beginTime != ''\">" +
            "    AND oper_time &gt;= #{beginTime}" +
            "  </if>" +
            "  <if test=\"endTime != null and endTime != ''\">" +
            "    AND oper_time &lt;= #{endTime}" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countOperLogList(SysOperLogQueryDTO queryDTO);

    /**
     * 根据日志主键查询单条完整详情
     */
    @Select("SELECT oper_id, title, business_type, method, request_method, oper_name, dept_name, " +
            "       oper_url, oper_ip, oper_param, json_result, status, error_msg, cost_time, oper_time " +
            "FROM sys_oper_log " +
            "WHERE oper_id = #{operId}")
    SysOperLog selectOperLogById(@Param("operId") Long operId);

    /**
     * 批量或单条删除日志记录
     */
    @Delete("<script>" +
            "DELETE FROM sys_oper_log WHERE oper_id IN " +
            "<foreach collection=\"operIds\" item=\"id\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{id}" +
            "</foreach>" +
            "</script>")
    int deleteOperLogByIds(@Param("operIds") List<Long> operIds);

    /**
     * 清空全部操作日志
     */
    @Delete("TRUNCATE TABLE sys_oper_log")
    void cleanOperLog();
}
