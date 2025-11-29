package org.shanling.simplemanage.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.Result;
import org.shanling.simplemanage.config.encrypt.ApiEncrypt;
import org.shanling.simplemanage.dto.CardLoginDTO;
import org.shanling.simplemanage.entity.ScriptVersionControl;
import org.shanling.simplemanage.service.ScriptCardAuthService;
import org.shanling.simplemanage.service.ScriptVersionControlService;
import org.shanling.simplemanage.service.ScriptCardDeviceService;
import org.shanling.simplemanage.util.JwtUtil;
import org.shanling.simplemanage.vo.CardLoginVO;
import org.shanling.simplemanage.vo.PreCheckVO;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 脚本对外开放API接口
 * 用于脚本端调用，无需登录认证
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/open-api/script")
public class ScriptOpenApiController {

    private final ScriptCardAuthService cardAuthService;
    private final ScriptVersionControlService versionControlService;
    private final ScriptCardDeviceService cardDeviceService;
    private final JwtUtil jwtUtil;

    public ScriptOpenApiController(ScriptCardAuthService cardAuthService,
                                     ScriptVersionControlService versionControlService,
                                     ScriptCardDeviceService cardDeviceService,
                                     JwtUtil jwtUtil) {
        this.cardAuthService = cardAuthService;
        this.versionControlService = versionControlService;
        this.cardDeviceService = cardDeviceService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 卡密登录
     * 注意：启用加密后，需要前端使用 RSA+AES 加密请求和响应数据
     * 详细说明请参考 ENCRYPTION_GUIDE.md
     */
    @PostMapping("/login")
    @ApiEncrypt(response = true)  // 启用请求和响应加密
    public Result<CardLoginVO> cardLogin(@Valid @RequestBody CardLoginDTO loginDTO) {
        try {
            CardLoginVO loginVO = cardAuthService.cardLogin(loginDTO);
            log.info("卡密登录成功，卡号：{}****，设备：{}****",
                    loginDTO.getCardNo().substring(0, Math.min(4, loginDTO.getCardNo().length())),
                    loginDTO.getDeviceAndroidId().substring(0, Math.min(8, loginDTO.getDeviceAndroidId().length())));
            return Result.success(loginVO);
        } catch (Exception e) {
            log.warn("卡密登录失败，卡号：{}****，原因：{}",
                    loginDTO.getCardNo().substring(0, Math.min(4, loginDTO.getCardNo().length())),
                    e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证卡密token
     */
    @PostMapping("/verify")
    public Result<CardLoginVO> verifyToken(@RequestHeader(value = "Authorization", required = false) String authorization,
                                           @RequestHeader(value = "Card-Token", required = false) String cardTokenHeader) {
        // 支持两种token格式：Authorization Bearer token 和 Card-Token
        String cardToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            cardToken = authorization.substring(7);
        } else if (cardTokenHeader != null) {
            cardToken = cardTokenHeader;
        }

        if (cardToken == null) {
            return Result.error("缺少认证token");
        }

        try {
            CardLoginVO loginVO = cardAuthService.verifyCardToken(cardToken);
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error("token验证失败：" + e.getMessage());
        }
    }

    /**
     * 获取卡密信息
     */
    @GetMapping("/card-info")
    public Result<CardLoginVO> getCardInfo(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String cardToken = jwtUtil.getTokenFromHeader(authorization);

        if (cardToken == null) {
            return Result.error("未登录");
        }

        try {
            CardLoginVO loginVO = cardAuthService.getCardInfo(cardToken);
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error("获取卡密信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取最新脚本版本
     */
    @GetMapping("/latest/{gameId}")
    public Result<ScriptVersionControl> getLatestVersion(@PathVariable String gameId) {
        try {
            ScriptVersionControl version = versionControlService.getLatestVersionByGameId(gameId);
            if (version == null) {
                return Result.error("未找到该游戏的脚本版本");
            }
            return Result.success(version);
        } catch (Exception e) {
            log.error("获取最新版本失败，游戏ID：{}，原因：{}", gameId, e.getMessage());
            return Result.error("获取最新版本失败：" + e.getMessage());
        }
    }

    /**
     * 脚本启动前预检查
     * 用于验证token有效性（包括剩余时长）和版本MD5校验
     */
    @GetMapping("/pre-check/{gameId}")
    public Result<PreCheckVO> preCheck(@PathVariable String gameId, HttpServletRequest request) {
        PreCheckVO preCheckVO = new PreCheckVO();

        try {
            // 1. 获取并验证token
            String authorization = request.getHeader("Authorization");
            String cardToken = jwtUtil.getTokenFromHeader(authorization);

            if (cardToken == null) {
                preCheckVO.setTokenValid(false);
                preCheckVO.setMessage("未登录，请先进行卡密登录");
                return Result.error(preCheckVO.getMessage(), preCheckVO);
            }

            // 2. 验证token并获取卡密信息
            CardLoginVO cardInfo = cardAuthService.verifyCardToken(cardToken);

            // 3. 计算更精确的剩余时间
            Date now = new Date();
            Date expireTime = cardInfo.getExpireTime();
            long remainingMillis = expireTime.getTime() - now.getTime();
            long remainingMinutes = remainingMillis / (60 * 1000);
            long remainingHours = remainingMillis / (60 * 60 * 1000);
            long remainingDays = remainingMillis / (24 * 60 * 60 * 1000);

            // 4. 检查卡密是否过期
            if (remainingMinutes <= 0) {
                preCheckVO.setTokenValid(false);
                preCheckVO.setMessage("卡密已过期，请续费");
                return Result.error(preCheckVO.getMessage(), preCheckVO);
            }

            // 5. 检查是否有该游戏的权限
            boolean hasPermission = cardInfo.getGames().stream()
                    .anyMatch(game -> gameId.equals(game.getGameId()));

            if (!hasPermission) {
                preCheckVO.setTokenValid(false);
                preCheckVO.setMessage("无权限访问该游戏");
                return Result.error(preCheckVO.getMessage(), preCheckVO);
            }

            // 6. 检查设备绑定数量限制
            if (cardInfo.getDeviceSize() != null && cardInfo.getDeviceSize() > 0) {
                long boundDeviceCount = cardDeviceService.countBoundDevices(cardInfo.getCardNo());
                if (boundDeviceCount > cardInfo.getDeviceSize()) {
                    preCheckVO.setTokenValid(false);
                    preCheckVO.setMessage(String.format("设备绑定数量已达上限（%d/%d），无法运行脚本", 
                            boundDeviceCount, cardInfo.getDeviceSize()));
                    log.warn("卡密设备数量超限，卡号：{}****，已绑定：{}，限制：{}",
                            cardInfo.getCardNo().substring(0, Math.min(4, cardInfo.getCardNo().length())),
                            boundDeviceCount,
                            cardInfo.getDeviceSize());
                    return Result.error(preCheckVO.getMessage(), preCheckVO);
                }
            }

            // 7. 获取最新版本信息（带MD5）
            ScriptVersionControl version = versionControlService.getLatestVersionByGameId(gameId);
            if (version != null) {
                preCheckVO.setGameId(version.getGameId());
                preCheckVO.setVersion(version.getVersion());
                preCheckVO.setFileMd5(version.getFileMd5());
                preCheckVO.setFileUrl(version.getFileUrl());
                preCheckVO.setFileSize(version.getFileSize());
            }

            // 8. 构建返回结果
            preCheckVO.setTokenValid(true);
            preCheckVO.setCardNo(cardInfo.getCardNo());
            preCheckVO.setExpireTime(expireTime);
            preCheckVO.setRemainingDays(remainingDays);
            preCheckVO.setRemainingHours(remainingHours);
            preCheckVO.setRemainingMinutes(remainingMinutes);

            // 9. 设置提示消息
            String timeInfo;
            if (remainingDays > 0) {
                timeInfo = String.format("剩余 %d 天", remainingDays);
            } else if (remainingHours > 0) {
                timeInfo = String.format("剩余 %d 小时", remainingHours);
            } else {
                timeInfo = String.format("剩余 %d 分钟", remainingMinutes);
            }
            preCheckVO.setMessage(String.format("验证通过，%s", timeInfo));

            log.info("预检查成功，卡号：{}****",
                    cardInfo.getCardNo().substring(0, Math.min(4, cardInfo.getCardNo().length())));

            return Result.success(preCheckVO);

        } catch (Exception e) {
            log.error("预检查失败，游戏ID：{}，原因：{}", gameId, e.getMessage());
            preCheckVO.setTokenValid(false);
            preCheckVO.setMessage("预检查失败：" + e.getMessage());
            return Result.error(preCheckVO.getMessage(), preCheckVO);
        }
    }

    /**
     * 验证打包的用户信息
     */
    @GetMapping("/checkUser")
    public Result<Boolean> checkUser() {
        return Result.success(true);
    }

}
