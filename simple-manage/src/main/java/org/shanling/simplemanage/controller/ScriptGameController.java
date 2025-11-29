package org.shanling.simplemanage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.ScriptGameDTO;
import org.shanling.simplemanage.entity.ScriptGame;
import org.shanling.simplemanage.service.ScriptGameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏列表管理控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/script/game")
public class ScriptGameController {

    private final ScriptGameService scriptGameService;

    public ScriptGameController(ScriptGameService scriptGameService) {
        this.scriptGameService = scriptGameService;
    }

    /**
     * 获取游戏列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<ScriptGame>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String title) {
        log.info("查询游戏列表：current={}, size={}, title={}", current, size, title);
        IPage<ScriptGame> page = scriptGameService.getGameList(current, size, title);
        return Result.success(page);
    }

    /**
     * 获取所有游戏列表（不分页）
     */
    @GetMapping("/all")
    public Result<List<ScriptGame>> all() {
        log.info("查询所有游戏列表");
        List<ScriptGame> list = scriptGameService.getAllGames();
        return Result.success(list);
    }

    /**
     * 获取游戏详情
     */
    @GetMapping("/detail/{id}")
    public Result<ScriptGame> detail(@PathVariable String id) {
        log.info("查询游戏详情：id={}", id);
        ScriptGame game = scriptGameService.getGameDetail(id);
        return Result.success(game);
    }

    /**
     * 添加游戏
     */
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody ScriptGameDTO gameDTO) {
        log.info("添加游戏：{}", gameDTO.getTitle());
        scriptGameService.addGame(gameDTO);
        return Result.success("添加成功", null);
    }

    /**
     * 更新游戏
     */
    @PutMapping("/update")
    public Result<String> update(@Valid @RequestBody ScriptGameDTO gameDTO) {
        log.info("更新游戏：id={}", gameDTO.getId());
        scriptGameService.updateGame(gameDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除游戏
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        log.info("删除游戏：id={}", id);
        scriptGameService.deleteGame(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除游戏
     */
    @DeleteMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<String> ids) {
        log.info("批量删除游戏：ids={}", ids);
        scriptGameService.batchDeleteGame(ids);
        return Result.success("批量删除成功", null);
    }

}
