package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.ScriptCardDeviceDTO;
import org.shanling.simplemanage.entity.ScriptCardDevice;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptCardDeviceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;

/**
 * 卡密关联设备管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptCardDeviceService {

    private final ScriptCardDeviceMapper scriptCardDeviceMapper;

    public ScriptCardDeviceService(ScriptCardDeviceMapper scriptCardDeviceMapper) {
        this.scriptCardDeviceMapper = scriptCardDeviceMapper;
    }

    /**
     * 分页查询设备列表
     */
    public IPage<ScriptCardDevice> getDeviceList(long current, long size, String cardNo, String deviceAndroidId, 
                                                   String deviceBrand, String deviceModel) {
        log.info("查询设备列表：current={}, size={}, cardNo={}, deviceAndroidId={}, deviceBrand={}, deviceModel={}", 
                 current, size, cardNo, deviceAndroidId, deviceBrand, deviceModel);
        
        Page<ScriptCardDevice> page = new Page<>(current, size);
        LambdaQueryWrapper<ScriptCardDevice> wrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(cardNo)) {
            wrapper.like(ScriptCardDevice::getCardNo, cardNo);
        }
        if (StringUtils.hasText(deviceAndroidId)) {
            wrapper.like(ScriptCardDevice::getDeviceAndroidId, deviceAndroidId);
        }
        if (StringUtils.hasText(deviceBrand)) {
            wrapper.like(ScriptCardDevice::getDeviceBrand, deviceBrand);
        }
        if (StringUtils.hasText(deviceModel)) {
            wrapper.like(ScriptCardDevice::getDeviceModel, deviceModel);
        }
        
        wrapper.orderByDesc(ScriptCardDevice::getCreateTime);
        
        return scriptCardDeviceMapper.selectPage(page, wrapper);
    }

    /**
     * 获取设备详情
     */
    public ScriptCardDevice getDeviceDetail(String id) {
        log.info("查询设备详情：id={}", id);
        ScriptCardDevice device = scriptCardDeviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "设备记录不存在");
        }
        return device;
    }

    /**
     * 添加设备绑定
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDevice(ScriptCardDeviceDTO deviceDTO) {
        log.info("添加设备绑定：cardNo={}, deviceAndroidId={}", deviceDTO.getCardNo(), deviceDTO.getDeviceAndroidId());
        
        // 检查是否已绑定
        if (isDeviceBoundToCard(deviceDTO.getCardNo(), deviceDTO.getDeviceAndroidId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该设备已绑定此卡密");
        }
        
        ScriptCardDevice device = new ScriptCardDevice();
        BeanUtils.copyProperties(deviceDTO, device);
        
        device.setCreateTime(new Date());
        device.setUpdateTime(new Date());
        
        scriptCardDeviceMapper.insert(device);
        log.info("设备绑定成功：cardNo={}, deviceAndroidId={}", device.getCardNo(), device.getDeviceAndroidId());
    }

    /**
     * 删除设备绑定
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDevice(String id) {
        log.info("删除设备绑定：id={}", id);
        
        ScriptCardDevice device = scriptCardDeviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "设备记录不存在");
        }
        
        scriptCardDeviceMapper.deleteById(id);
        log.info("设备绑定删除成功：id={}", id);
    }

    /**
     * 批量删除设备绑定
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDevice(Collection<String> ids) {
        log.info("批量删除设备绑定：ids={}", ids);
        scriptCardDeviceMapper.deleteByIds(ids);
        log.info("批量删除设备绑定成功");
    }

    /**
     * 统计卡密绑定的设备数量
     */
    public long countBoundDevices(String cardNo) {
        log.info("统计设备绑定数量：cardNo={}", cardNo);
        
        LambdaQueryWrapper<ScriptCardDevice> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCardDevice::getCardNo, cardNo);
        
        return scriptCardDeviceMapper.selectCount(wrapper);
    }

    /**
     * 检查卡密和设备是否已关联
     */
    public boolean isDeviceBoundToCard(String cardNo, String deviceAndroidId) {
        LambdaQueryWrapper<ScriptCardDevice> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCardDevice::getCardNo, cardNo);
        wrapper.eq(ScriptCardDevice::getDeviceAndroidId, deviceAndroidId);
        return scriptCardDeviceMapper.selectCount(wrapper) > 0;
    }

}
