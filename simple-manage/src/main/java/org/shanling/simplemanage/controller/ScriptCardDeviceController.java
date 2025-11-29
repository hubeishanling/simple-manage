package org.shanling.simplemanage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.dto.ScriptCardDeviceDTO;
import org.shanling.simplemanage.entity.ScriptCardDevice;
import org.shanling.simplemanage.service.ScriptCardDeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 卡密关联设备管理控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/script/cardDevice")
public class ScriptCardDeviceController {

    private final ScriptCardDeviceService scriptCardDeviceService;

    public ScriptCardDeviceController(ScriptCardDeviceService scriptCardDeviceService) {
        this.scriptCardDeviceService = scriptCardDeviceService;
    }

    /**
     * 获取设备列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<ScriptCardDevice>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String cardNo,
            @RequestParam(required = false) String deviceAndroidId,
            @RequestParam(required = false) String deviceBrand,
            @RequestParam(required = false) String deviceModel) {
        log.info("查询设备列表：current={}, size={}, cardNo={}, deviceAndroidId={}, deviceBrand={}, deviceModel={}", 
                 current, size, cardNo, deviceAndroidId, deviceBrand, deviceModel);
        IPage<ScriptCardDevice> page = scriptCardDeviceService.getDeviceList(current, size, cardNo, deviceAndroidId, deviceBrand, deviceModel);
        return Result.success(page);
    }

    /**
     * 获取设备详情
     */
    @GetMapping("/detail/{id}")
    public Result<ScriptCardDevice> detail(@PathVariable String id) {
        log.info("查询设备详情：id={}", id);
        ScriptCardDevice device = scriptCardDeviceService.getDeviceDetail(id);
        return Result.success(device);
    }

    /**
     * 添加设备绑定
     */
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody ScriptCardDeviceDTO deviceDTO) {
        log.info("添加设备绑定：cardNo={}, deviceAndroidId={}", deviceDTO.getCardNo(), deviceDTO.getDeviceAndroidId());
        scriptCardDeviceService.addDevice(deviceDTO);
        return Result.success("添加成功", null);
    }

    /**
     * 删除设备绑定
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        log.info("删除设备绑定：id={}", id);
        scriptCardDeviceService.deleteDevice(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除设备绑定
     */
    @DeleteMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<String> ids) {
        log.info("批量删除设备绑定：ids={}", ids);
        scriptCardDeviceService.batchDeleteDevice(ids);
        return Result.success("批量删除成功", null);
    }

    /**
     * 统计卡密已绑定的设备数量
     */
    @GetMapping("/count/{cardNo}")
    public Result<Long> countByCardNo(@PathVariable String cardNo) {
        log.info("统计卡密绑定设备数：cardNo={}", cardNo);
        long count = scriptCardDeviceService.countBoundDevices(cardNo);
        return Result.success(count);
    }

}
