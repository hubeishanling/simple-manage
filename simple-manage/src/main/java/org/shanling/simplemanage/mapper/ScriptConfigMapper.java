package org.shanling.simplemanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.shanling.simplemanage.entity.ScriptConfig;

/**
 * 脚本配置模板Mapper接口
 * 
 * @author shanling
 */
public interface ScriptConfigMapper extends BaseMapper<ScriptConfig> {

    /**
     * 根据配置类型获取默认配置
     */
    @Select("SELECT * FROM script_config WHERE config_type = #{configType} AND is_default = 1 AND status = 0 LIMIT 1")
    ScriptConfig selectDefaultByType(@Param("configType") String configType);

}
