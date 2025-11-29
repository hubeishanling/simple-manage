package org.shanling.simplemanage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.ScriptCardDTO;
import org.shanling.simplemanage.entity.ScriptCard;
import org.shanling.simplemanage.entity.ScriptGame;
import org.shanling.simplemanage.service.ScriptCardService;
import org.shanling.simplemanage.service.ScriptGameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 脚本卡密管理控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/script/card")
public class ScriptCardController {

    private final ScriptCardService scriptCardService;
    private final ScriptGameService scriptGameService;

    public ScriptCardController(ScriptCardService scriptCardService,
                                 ScriptGameService scriptGameService) {
        this.scriptCardService = scriptCardService;
        this.scriptGameService = scriptGameService;
    }

    /**
     * 获取卡密列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<ScriptCard>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String cardNo,
            @RequestParam(required = false) Integer status) {
        log.info("查询卡密列表：current={}, size={}, cardNo={}, status={}", current, size, cardNo, status);
        IPage<ScriptCard> page = scriptCardService.getCardList(current, size, cardNo, status);
        return Result.success(page);
    }

    /**
     * 获取卡密详情
     */
    @GetMapping("/detail/{id}")
    public Result<ScriptCard> detail(@PathVariable String id) {
        log.info("查询卡密详情：id={}", id);
        ScriptCard card = scriptCardService.getCardDetail(id);
        return Result.success(card);
    }

    /**
     * 添加卡密
     */
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody ScriptCardDTO cardDTO) {
        log.info("添加卡密：{}", cardDTO.getCardNo());
        scriptCardService.addCard(cardDTO);
        return Result.success("添加成功", null);
    }

    /**
     * 更新卡密
     */
    @PutMapping("/update")
    public Result<String> update(@Valid @RequestBody ScriptCardDTO cardDTO) {
        log.info("更新卡密：id={}", cardDTO.getId());
        scriptCardService.updateCard(cardDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除卡密
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        log.info("删除卡密：id={}", id);
        scriptCardService.deleteCard(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除卡密
     */
    @DeleteMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<String> ids) {
        log.info("批量删除卡密：ids={}", ids);
        scriptCardService.batchDeleteCard(ids);
        return Result.success("批量删除成功", null);
    }

    /**
     * 获取游戏列表（用于卡密关联游戏选择）
     */
    @GetMapping("/gameList")
    public Result<List<ScriptGame>> getGameList() {
        log.info("获取游戏列表");
        List<ScriptGame> gameList = scriptGameService.getAllGames();
        return Result.success(gameList);
    }

}
