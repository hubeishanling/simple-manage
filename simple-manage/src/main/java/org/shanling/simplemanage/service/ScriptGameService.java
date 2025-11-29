package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.ScriptGameDTO;
import org.shanling.simplemanage.entity.ScriptGame;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptGameMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 游戏列表管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptGameService {

    private final ScriptGameMapper scriptGameMapper;

    public ScriptGameService(ScriptGameMapper scriptGameMapper) {
        this.scriptGameMapper = scriptGameMapper;
    }

    /**
     * 分页查询游戏列表
     */
    public IPage<ScriptGame> getGameList(long current, long size, String title) {
        log.info("查询游戏列表：current={}, size={}, title={}", current, size, title);
        
        Page<ScriptGame> page = new Page<>(current, size);
        LambdaQueryWrapper<ScriptGame> wrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(title)) {
            wrapper.like(ScriptGame::getTitle, title);
        }
        
        wrapper.orderByDesc(ScriptGame::getCreateTime);
        
        return scriptGameMapper.selectPage(page, wrapper);
    }

    /**
     * 获取所有游戏列表（不分页）
     */
    public List<ScriptGame> getAllGames() {
        log.info("查询所有游戏列表");
        LambdaQueryWrapper<ScriptGame> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(ScriptGame::getCreateTime);
        return scriptGameMapper.selectList(wrapper);
    }

    /**
     * 获取游戏详情
     */
    public ScriptGame getGameDetail(String id) {
        log.info("查询游戏详情：id={}", id);
        ScriptGame game = scriptGameMapper.selectById(id);
        if (game == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "游戏不存在");
        }
        return game;
    }

    /**
     * 添加游戏
     */
    @Transactional(rollbackFor = Exception.class)
    public void addGame(ScriptGameDTO gameDTO) {
        log.info("添加游戏：{}", gameDTO.getTitle());
        
        ScriptGame game = new ScriptGame();
        BeanUtils.copyProperties(gameDTO, game);
        
        game.setCreateTime(LocalDateTime.now());
        game.setUpdateTime(LocalDateTime.now());
        
        scriptGameMapper.insert(game);
        log.info("游戏添加成功：{}", game.getTitle());
    }

    /**
     * 更新游戏
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGame(ScriptGameDTO gameDTO) {
        log.info("更新游戏：id={}", gameDTO.getId());
        
        ScriptGame game = scriptGameMapper.selectById(gameDTO.getId());
        if (game == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "游戏不存在");
        }
        
        if (StringUtils.hasText(gameDTO.getTitle())) {
            game.setTitle(gameDTO.getTitle());
        }
        if (StringUtils.hasText(gameDTO.getRemark())) {
            game.setRemark(gameDTO.getRemark());
        }
        
        game.setUpdateTime(LocalDateTime.now());
        
        scriptGameMapper.updateById(game);
        log.info("游戏更新成功：{}", game.getTitle());
    }

    /**
     * 删除游戏
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGame(String id) {
        log.info("删除游戏：id={}", id);
        
        ScriptGame game = scriptGameMapper.selectById(id);
        if (game == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "游戏不存在");
        }
        
        scriptGameMapper.deleteById(id);
        log.info("游戏删除成功：{}", game.getTitle());
    }

    /**
     * 批量删除游戏
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteGame(Collection<String> ids) {
        log.info("批量删除游戏：ids={}", ids);
        scriptGameMapper.deleteBatchIds(ids);
        log.info("批量删除游戏成功");
    }

}
