package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.ScriptVersionControlDTO;
import org.shanling.simplemanage.entity.ScriptVersionControl;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptVersionControlMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 脚本版本控制管理服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptVersionControlService {

    @Value("${file.upload.path:D:/uploads/scripts}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8080/api/files/scripts}")
    private String urlPrefix;

    private final ScriptVersionControlMapper versionControlMapper;

    public ScriptVersionControlService(ScriptVersionControlMapper versionControlMapper) {
        this.versionControlMapper = versionControlMapper;
    }

    /**
     * 分页查询版本列表
     */
    public IPage<ScriptVersionControl> getVersionList(long current, long size, String gameId, Integer type, Integer version) {
        log.info("查询版本列表：current={}, size={}, gameId={}, type={}, version={}", current, size, gameId, type, version);
        
        Page<ScriptVersionControl> page = new Page<>(current, size);
        LambdaQueryWrapper<ScriptVersionControl> wrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(gameId)) {
            wrapper.eq(ScriptVersionControl::getGameId, gameId);
        }
        if (type != null) {
            wrapper.eq(ScriptVersionControl::getType, type);
        }
        if (version != null) {
            wrapper.eq(ScriptVersionControl::getVersion, version);
        }
        
        wrapper.orderByDesc(ScriptVersionControl::getCreateTime);
        
        return versionControlMapper.selectPage(page, wrapper);
    }

    /**
     * 获取版本详情
     */
    public ScriptVersionControl getVersionDetail(String id) {
        log.info("查询版本详情：id={}", id);
        ScriptVersionControl version = versionControlMapper.selectById(id);
        if (version == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "版本记录不存在");
        }
        return version;
    }

    /**
     * 上传脚本文件（新增）
     */
    @Transactional(rollbackFor = Exception.class)
    public void uploadScriptFile(String gameId, MultipartFile file, String remark) {
        log.info("上传脚本文件：gameId={}", gameId);
        
        try {
            // 获取文件字节
            byte[] fileBytes = file.getBytes();
            
            // 计算文件MD5
            String fileMd5 = calculateMd5(fileBytes);
            
            // 获取文件大小
            long fileSize = file.getSize();
            
            // 保存文件到本地
            String fileUrl = saveFileToLocal(file, gameId);
            
            // 获取最新版本号
            Integer latestVersion = versionControlMapper.getLatestVersionByGameId(gameId);
            Integer newVersion = (latestVersion == null) ? 1 : latestVersion + 1;
            
            // 创建版本记录
            ScriptVersionControl versionControl = new ScriptVersionControl();
            versionControl.setGameId(gameId);
            versionControl.setFileUrl(fileUrl);
            versionControl.setType(0); // APK插件
            versionControl.setVersion(newVersion);
            versionControl.setFileMd5(fileMd5);
            versionControl.setFileSize(fileSize);
            versionControl.setRemark(remark);
            versionControl.setCreateTime(new Date());
            versionControl.setUpdateTime(new Date());
            
            versionControlMapper.insert(versionControl);
            log.info("脚本文件上传成功：gameId={}, version={}", gameId, newVersion);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 更新脚本文件（修改）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateScriptFile(String id, String gameId, MultipartFile file, String remark) {
        log.info("更新脚本文件：id={}", id);
        
        ScriptVersionControl existingRecord = versionControlMapper.selectById(id);
        if (existingRecord == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "版本记录不存在");
        }
        
        try {
            if (file != null && !file.isEmpty()) {
                // 获取文件字节
                byte[] fileBytes = file.getBytes();
                
                // 计算文件MD5
                String fileMd5 = calculateMd5(fileBytes);
                
                // 获取文件大小
                long fileSize = file.getSize();
                
                // 删除旧文件
                deleteFileFromLocal(existingRecord.getFileUrl());
                
                // 保存新文件到本地
                String fileUrl = saveFileToLocal(file, gameId);
                
                // 更新版本记录
                existingRecord.setFileUrl(fileUrl);
                existingRecord.setFileMd5(fileMd5);
                existingRecord.setFileSize(fileSize);
            }
            
            // 更新备注
            if (StringUtils.hasText(remark)) {
                existingRecord.setRemark(remark);
            }
            existingRecord.setGameId(gameId);
            existingRecord.setUpdateTime(new Date());
            
            versionControlMapper.updateById(existingRecord);
            log.info("脚本文件更新成功：id={}", id);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "文件更新失败: " + e.getMessage());
        }
    }

    /**
     * 更新版本信息（不更新文件）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateVersion(ScriptVersionControlDTO versionDTO) {
        log.info("更新版本信息：id={}", versionDTO.getId());
        
        ScriptVersionControl version = versionControlMapper.selectById(versionDTO.getId());
        if (version == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "版本记录不存在");
        }
        
        if (StringUtils.hasText(versionDTO.getRemark())) {
            version.setRemark(versionDTO.getRemark());
        }
        version.setUpdateTime(new Date());
        
        versionControlMapper.updateById(version);
        log.info("版本信息更新成功：id={}", versionDTO.getId());
    }

    /**
     * 删除版本
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersion(String id) {
        log.info("删除版本：id={}", id);
        
        ScriptVersionControl version = versionControlMapper.selectById(id);
        if (version == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "版本记录不存在");
        }
        
        // 删除文件
        deleteFileFromLocal(version.getFileUrl());
        
        // 删除数据库记录
        versionControlMapper.deleteById(id);
        log.info("版本删除成功：id={}", id);
    }

    /**
     * 批量删除版本
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteVersion(Collection<String> ids) {
        log.info("批量删除版本：ids={}", ids);
        
        // 删除文件
        for (String id : ids) {
            ScriptVersionControl version = versionControlMapper.selectById(id);
            if (version != null) {
                deleteFileFromLocal(version.getFileUrl());
            }
        }
        
        // 删除数据库记录
        versionControlMapper.deleteByIds(ids);
        log.info("批量删除版本成功");
    }

    /**
     * 从本地删除文件
     */
    private void deleteFileFromLocal(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }
        
        try {
            // 从URL中提取文件路径
            String relativePath = fileUrl.replace(urlPrefix + "/", "");
            String filePath = uploadPath + File.separator + relativePath.replace("/", File.separator);
            
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                log.info("删除文件成功：{}", filePath);
            }
        } catch (Exception e) {
            log.error("删除文件失败：{}", fileUrl, e);
        }
    }

    /**
     * 计算文件MD5值
     */
    private String calculateMd5(byte[] fileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "计算MD5失败: " + e.getMessage());
        }
    }

    /**
     * 获取游戏的版本历史记录
     */
    public List<ScriptVersionControl> getVersionHistory(String gameId) {
        log.info("获取版本历史记录：gameId={}", gameId);
        
        LambdaQueryWrapper<ScriptVersionControl> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptVersionControl::getGameId, gameId);
        wrapper.orderByDesc(ScriptVersionControl::getVersion);
        
        return versionControlMapper.selectList(wrapper);
    }

    /**
     * 根据游戏ID获取最新版本
     * 用于脚本端获取最新可用版本
     */
    public ScriptVersionControl getLatestVersionByGameId(String gameId) {
        log.info("获取最新版本：gameId={}", gameId);
        
        LambdaQueryWrapper<ScriptVersionControl> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptVersionControl::getGameId, gameId);
        wrapper.orderByDesc(ScriptVersionControl::getVersion);
        wrapper.last("LIMIT 1");
        
        ScriptVersionControl version = versionControlMapper.selectOne(wrapper);
        if (version == null) {
            log.warn("未找到游戏的版本记录：gameId={}", gameId);
        }
        
        return version;
    }

    /**
     * 保存文件到本地
     */
    private String saveFileToLocal(MultipartFile file, String gameId) throws IOException {
        // 生成日期文件夹（年月日）
        String dateFolder = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        
        // 目标目录：uploadPath/gameId/日期/
        String targetDir = uploadPath + File.separator + gameId + File.separator + dateFolder;
        
        // 确保目录存在
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 生成文件名：时间戳 + 原始文件名
        String originalFilename = file.getOriginalFilename();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = timestamp + "_" + originalFilename;
        
        // 保存文件
        Path filePath = Paths.get(targetDir, filename);
        Files.write(filePath, file.getBytes());
        
        // 返回URL
        return urlPrefix + "/" + gameId + "/" + dateFolder + "/" + filename;
    }

}
