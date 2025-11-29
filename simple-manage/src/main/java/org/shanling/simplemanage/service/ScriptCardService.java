package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.ScriptCardDTO;
import org.shanling.simplemanage.entity.ScriptCard;
import org.shanling.simplemanage.entity.ScriptGame;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptCardMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 脚本卡密管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptCardService {

    private final ScriptCardMapper scriptCardMapper;
    private final ScriptCardGameService scriptCardGameService;
    private final ScriptGameService scriptGameService;

    public ScriptCardService(ScriptCardMapper scriptCardMapper,
                             ScriptCardGameService scriptCardGameService,
                             ScriptGameService scriptGameService) {
        this.scriptCardMapper = scriptCardMapper;
        this.scriptCardGameService = scriptCardGameService;
        this.scriptGameService = scriptGameService;
    }

    /**
     * 分页查询卡密列表
     */
    public IPage<ScriptCard> getCardList(long current, long size, String cardNo, Integer status) {
        log.info("查询卡密列表：current={}, size={}, cardNo={}, status={}", current, size, cardNo, status);
        
        Page<ScriptCard> page = new Page<>(current, size);
        LambdaQueryWrapper<ScriptCard> wrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(cardNo)) {
            wrapper.like(ScriptCard::getCardNo, cardNo);
        }
        if (status != null) {
            wrapper.eq(ScriptCard::getStatus, status);
        }
        
        wrapper.orderByDesc(ScriptCard::getCreateTime);
        
        IPage<ScriptCard> result = scriptCardMapper.selectPage(page, wrapper);
        
        // 为每个卡密添加关联的游戏列表
        result.getRecords().forEach(card -> {
            card.setCardGames(scriptCardGameService.getCardGames(card.getId()));
        });
        
        return result;
    }

    /**
     * 获取卡密详情
     */
    public ScriptCard getCardDetail(String id) {
        log.info("查询卡密详情：id={}", id);
        ScriptCard card = scriptCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密不存在");
        }
        
        // 获取关联的游戏列表
        card.setCardGames(scriptCardGameService.getCardGames(id));
        
        return card;
    }

    /**
     * 添加卡密
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCard(ScriptCardDTO cardDTO) {
        log.info("添加卡密：{}", cardDTO.getCardNo());
        
        // 检查卡密是否已存在
        ScriptCard existCard = scriptCardMapper.selectOne(new LambdaQueryWrapper<ScriptCard>()
                .eq(ScriptCard::getCardNo, cardDTO.getCardNo()));
        
        if (existCard != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密已存在");
        }
        
        ScriptCard card = new ScriptCard();
        BeanUtils.copyProperties(cardDTO, card);
        
        card.setCreateTime(new Date());
        card.setUpdateTime(new Date());
        
        scriptCardMapper.insert(card);
        
        // 保存游戏关联
        if (cardDTO.getGameIds() != null && !cardDTO.getGameIds().isEmpty()) {
            List<String> gameTitles = getGameTitlesByIds(cardDTO.getGameIds());
            scriptCardGameService.saveCardGames(card.getId(), cardDTO.getGameIds(), gameTitles);
        }
        
        log.info("卡密添加成功：{}", card.getCardNo());
    }

    /**
     * 更新卡密
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCard(ScriptCardDTO cardDTO) {
        log.info("更新卡密：id={}", cardDTO.getId());
        
        ScriptCard card = scriptCardMapper.selectById(cardDTO.getId());
        if (card == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密不存在");
        }
        
        // 更新字段（卡密号不允许修改）
        if (cardDTO.getExpireDay() != null) {
            card.setExpireDay(cardDTO.getExpireDay());
        }
        if (cardDTO.getPrice() != null) {
            card.setPrice(cardDTO.getPrice());
        }
        if (cardDTO.getDeviceSize() != null) {
            card.setDeviceSize(cardDTO.getDeviceSize());
        }
        if (cardDTO.getStatus() != null) {
            card.setStatus(cardDTO.getStatus());
        }
        if (StringUtils.hasText(cardDTO.getRemark())) {
            card.setRemark(cardDTO.getRemark());
        }
        
        card.setUpdateTime(new Date());
        
        scriptCardMapper.updateById(card);
        
        // 更新游戏关联
        if (cardDTO.getGameIds() != null) {
            List<String> gameTitles = getGameTitlesByIds(cardDTO.getGameIds());
            scriptCardGameService.saveCardGames(card.getId(), cardDTO.getGameIds(), gameTitles);
        }
        
        log.info("卡密更新成功：{}", card.getCardNo());
    }

    /**
     * 删除卡密
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCard(String id) {
        log.info("删除卡密：id={}", id);
        
        ScriptCard card = scriptCardMapper.selectById(id);
        if (card == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密不存在");
        }
        
        scriptCardMapper.deleteById(id);
        log.info("卡密删除成功：{}", card.getCardNo());
    }

    /**
     * 批量删除卡密
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteCard(Collection<String> ids) {
        log.info("批量删除卡密：ids={}", ids);
        
        // 删除游戏关联
        for (String id : ids) {
            scriptCardGameService.deleteCardGames(id);
        }
        
        scriptCardMapper.deleteByIds(ids);
        log.info("批量删除卡密成功");
    }

    /**
     * 根据游戏ID列表获取游戏标题
     */
    private List<String> getGameTitlesByIds(List<String> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return List.of();
        }
        
        List<ScriptGame> games = scriptGameService.getAllGames();
        return gameIds.stream()
                .map(gameId -> games.stream()
                        .filter(game -> game.getId().equals(gameId))
                        .findFirst()
                        .map(ScriptGame::getTitle)
                        .orElse(null))
                .collect(Collectors.toList());
    }

}
