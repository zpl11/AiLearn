package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysErrorLog;
import org.example.componeyoa.entity.dto.SysErrorLogQueryDTO;

import java.util.List;

@Mapper
public interface SysErrorLogMapper {

    /**
     * 新增系统异常错误监控日志
     */
    @Insert("INSERT INTO sys_error_log(" +
            "request_uri, request_method, error_name, error_message, error_stack, oper_name, create_time) " +
            "VALUES(" +
            "#{requestUri}, #{requestMethod}, #{errorName}, #{errorMessage}, #{errorStack}, #{operName}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "errorId")
    int insertErrorLog(SysErrorLog errorLog);

    /**
     * 多条件分页查询异常日志
     */
    @Select("<script>" +
            "SELECT error_id, request_uri, request_method, error_name, error_message, error_stack, oper_name, create_time " +
            "FROM sys_error_log " +
            "<where>" +
            "  <if test=\"errorName != null and errorName != ''\">" +
            "    AND error_name LIKE CONCAT('%', #{errorName}, '%')" +
            "  </if>" +
            "  <if test=\"requestUri != null and requestUri != ''\">" +
            "    AND request_uri LIKE CONCAT('%', #{requestUri}, '%')" +
            "  </if>" +
            "  <if test=\"operName != null and operName != ''\">" +
            "    AND oper_name LIKE CONCAT('%', #{operName}, '%')" +
            "  </if>" +
            "  <if test=\"beginTime != null and beginTime != ''\">" +
            "    AND create_time &gt;= #{beginTime}" +
            "  </if>" +
            "  <if test=\"endTime != null and endTime != ''\">" +
            "    AND create_time &lt;= #{endTime}" +
            "  </if>" +
            "</where> " +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<SysErrorLog> selectErrorLogList(SysErrorLogQueryDTO queryDTO);

    /**
     * 多条件统计总异常日志条数
     */
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM sys_error_log " +
            "<where>" +
            "  <if test=\"errorName != null and errorName != ''\">" +
            "    AND error_name LIKE CONCAT('%', #{errorName}, '%')" +
            "  </if>" +
            "  <if test=\"requestUri != null and requestUri != ''\">" +
            "    AND request_uri LIKE CONCAT('%', #{requestUri}, '%')" +
            "  </if>" +
            "  <if test=\"operName != null and operName != ''\">" +
            "    AND oper_name LIKE CONCAT('%', #{operName}, '%')" +
            "  </if>" +
            "  <if test=\"beginTime != null and beginTime != ''\">" +
            "    AND create_time &gt;= #{beginTime}" +
            "  </if>" +
            "  <if test=\"endTime != null and endTime != ''\">" +
            "    AND create_time &lt;= #{endTime}" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countErrorLogList(SysErrorLogQueryDTO queryDTO);

    /**
     * 根据日志主键查询完整详情（包含完整堆栈信息）
     */
    @Select("SELECT error_id, request_uri, request_method, error_name, error_message, error_stack, oper_name, create_time " +
            "FROM sys_error_log " +
            "WHERE error_id = #{errorId}")
    SysErrorLog selectErrorLogById(@Param("errorId") Long errorId);

    /**
     * 批量或单条删除异常日志
     */
    @Delete("<script>" +
            "DELETE FROM sys_error_log WHERE error_id IN " +
            "<foreach collection=\"errorIds\" item=\"id\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{id}" +
            "</foreach>" +
            "</script>")
    int deleteErrorLogByIds(@Param("errorIds") List<Long> errorIds);

    /**
     * 清空全部系统错误日志
     */
    @Delete("TRUNCATE TABLE sys_error_log")
    void cleanErrorLog();
}
