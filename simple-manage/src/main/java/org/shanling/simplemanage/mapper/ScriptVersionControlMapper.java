package org.shanling.simplemanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.shanling.simplemanage.entity.ScriptVersionControl;

/**
 * 脚本版本控制Mapper接口
 * 
 * @author shanling
 */
public interface ScriptVersionControlMapper extends BaseMapper<ScriptVersionControl> {

    /**
     * 获取指定游戏的最新版本号
     */
    @Select("SELECT MAX(version) FROM script_version_control WHERE game_id = #{gameId}")
    Integer getLatestVersionByGameId(@Param("gameId") String gameId);

}
