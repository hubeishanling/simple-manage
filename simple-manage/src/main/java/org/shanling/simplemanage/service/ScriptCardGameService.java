package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.entity.ScriptCardGame;
import org.shanling.simplemanage.mapper.ScriptCardGameMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 卡密游戏关联管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptCardGameService {

    private final ScriptCardGameMapper scriptCardGameMapper;

    public ScriptCardGameService(ScriptCardGameMapper scriptCardGameMapper) {
        this.scriptCardGameMapper = scriptCardGameMapper;
    }

    /**
     * 保存卡密游戏关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCardGames(String cardId, List<String> gameIds, List<String> gameTitles) {
        log.info("保存卡密游戏关联：cardId={}, gameIds={}", cardId, gameIds);
        
        // 1. 删除旧的关联
        LambdaQueryWrapper<ScriptCardGame> deleteWrapper = Wrappers.lambdaQuery();
        deleteWrapper.eq(ScriptCardGame::getCardId, cardId);
        scriptCardGameMapper.delete(deleteWrapper);
        
        // 2. 插入新的关联
        if (gameIds != null && !gameIds.isEmpty()) {
            for (int i = 0; i < gameIds.size(); i++) {
                ScriptCardGame cardGame = new ScriptCardGame();
                cardGame.setCardId(cardId);
                cardGame.setGameId(gameIds.get(i));
                cardGame.setGameTitle(gameTitles != null && i < gameTitles.size() ? gameTitles.get(i) : null);
                cardGame.setCreateTime(new Date());
                cardGame.setUpdateTime(new Date());
                scriptCardGameMapper.insert(cardGame);
            }
            log.info("卡密游戏关联保存成功");
        }
    }

    /**
     * 获取卡密关联的游戏列表
     */
    public List<ScriptCardGame> getCardGames(String cardId) {
        log.info("获取卡密关联的游戏列表：cardId={}", cardId);
        
        LambdaQueryWrapper<ScriptCardGame> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCardGame::getCardId, cardId);
        
        return scriptCardGameMapper.selectList(wrapper);
    }

    /**
     * 删除卡密的所有游戏关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCardGames(String cardId) {
        log.info("删除卡密游戏关联：cardId={}", cardId);
        
        LambdaQueryWrapper<ScriptCardGame> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCardGame::getCardId, cardId);
        scriptCardGameMapper.delete(wrapper);
    }

}
