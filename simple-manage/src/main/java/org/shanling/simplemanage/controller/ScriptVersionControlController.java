package org.shanling.simplemanage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.ScriptVersionControlDTO;
import org.shanling.simplemanage.entity.ScriptVersionControl;
import org.shanling.simplemanage.service.ScriptVersionControlService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 脚本版本控制管理控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/script/version")
public class ScriptVersionControlController {

    private final ScriptVersionControlService versionControlService;

    public ScriptVersionControlController(ScriptVersionControlService versionControlService) {
        this.versionControlService = versionControlService;
    }

    /**
     * 获取版本列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<ScriptVersionControl>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String gameId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer version) {
        log.info("查询版本列表：current={}, size={}, gameId={}, type={}, version={}", current, size, gameId, type, version);
        IPage<ScriptVersionControl> page = versionControlService.getVersionList(current, size, gameId, type, version);
        return Result.success(page);
    }

    /**
     * 获取版本详情
     */
    @GetMapping("/detail/{id}")
    public Result<ScriptVersionControl> detail(@PathVariable String id) {
        log.info("查询版本详情：id={}", id);
        ScriptVersionControl version = versionControlService.getVersionDetail(id);
        return Result.success(version);
    }

    /**
     * 上传脚本文件
     */
    @PostMapping("/upload")
    public Result<String> upload(
            @RequestParam("gameId") String gameId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "remark", required = false) String remark) {
        log.info("上传脚本文件：gameId={}", gameId);
        versionControlService.uploadScriptFile(gameId, file, remark);
        return Result.success("上传成功", null);
    }

    /**
     * 更新脚本文件（带文件）
     */
    @PostMapping("/updateFile")
    public Result<String> updateFile(
            @RequestParam("id") String id,
            @RequestParam("gameId") String gameId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "remark", required = false) String remark) {
        log.info("更新脚本文件：id={}", id);
        versionControlService.updateScriptFile(id, gameId, file, remark);
        return Result.success("更新成功", null);
    }

    /**
     * 更新版本信息（不更新文件）
     */
    @PutMapping("/update")
    public Result<String> update(@Valid @RequestBody ScriptVersionControlDTO versionDTO) {
        log.info("更新版本信息：id={}", versionDTO.getId());
        versionControlService.updateVersion(versionDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除版本
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        log.info("删除版本：id={}", id);
        versionControlService.deleteVersion(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除版本
     */
    @DeleteMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<String> ids) {
        log.info("批量删除版本：ids={}", ids);
        versionControlService.batchDeleteVersion(ids);
        return Result.success("批量删除成功", null);
    }

    /**
     * 获取指定游戏的版本历史
     */
    @GetMapping("/history/{gameId}")
    public Result<List<ScriptVersionControl>> history(@PathVariable String gameId) {
        log.info("查询版本历史：gameId={}", gameId);
        List<ScriptVersionControl> list = versionControlService.getVersionHistory(gameId);
        return Result.success(list);
    }

}
