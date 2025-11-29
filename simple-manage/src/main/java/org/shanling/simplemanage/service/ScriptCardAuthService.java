package org.shanling.simplemanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.shanling.simplemanage.common.ResultCode;
import org.shanling.simplemanage.dto.CardLoginDTO;
import org.shanling.simplemanage.entity.ScriptCard;
import org.shanling.simplemanage.entity.ScriptCardDevice;
import org.shanling.simplemanage.entity.ScriptCardGame;
import org.shanling.simplemanage.exception.BusinessException;
import org.shanling.simplemanage.mapper.ScriptCardMapper;
import org.shanling.simplemanage.mapper.ScriptCardDeviceMapper;
import org.shanling.simplemanage.mapper.ScriptCardGameMapper;
import org.shanling.simplemanage.util.JwtUtil;
import org.shanling.simplemanage.vo.CardLoginVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 脚本卡密认证服务
 * 
 * @author shanling
 */
@Slf4j
@Service
public class ScriptCardAuthService {

    private final ScriptCardMapper cardMapper;
    private final ScriptCardDeviceMapper deviceMapper;
    private final ScriptCardGameMapper cardGameMapper;
    private final JwtUtil jwtUtil;

    public ScriptCardAuthService(ScriptCardMapper cardMapper,
                                  ScriptCardDeviceMapper deviceMapper,
                                  ScriptCardGameMapper cardGameMapper,
                                  JwtUtil jwtUtil) {
        this.cardMapper = cardMapper;
        this.deviceMapper = deviceMapper;
        this.cardGameMapper = cardGameMapper;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 卡密登录
     */
    @Transactional(rollbackFor = Exception.class)
    public CardLoginVO cardLogin(CardLoginDTO loginDTO) {
        log.info("卡密登录：cardNo={}****, deviceId={}****",
                loginDTO.getCardNo().substring(0, Math.min(4, loginDTO.getCardNo().length())),
                loginDTO.getDeviceAndroidId().substring(0, Math.min(8, loginDTO.getDeviceAndroidId().length())));

        // 1. 验证卡密是否存在且有效
        ScriptCard card = validateCard(loginDTO.getCardNo());

        // 2. 如果是首次登录（expireTime为null），根据expireDay计算并设置过期时间
        Date expireTime = card.getExpireTime();
        if (expireTime == null && card.getExpireDay() != null) {
            long expireMillis = System.currentTimeMillis() + (card.getExpireDay() * 24L * 60 * 60 * 1000);
            expireTime = new Date(expireMillis);
            card.setExpireTime(expireTime);
            log.info("首次登录，设置过期时间：cardNo={}****, expireTime={}", 
                    card.getCardNo().substring(0, Math.min(4, card.getCardNo().length())), 
                    expireTime);
        }

        // 3. 验证过期时间是否有效
        if (expireTime == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密配置错误：缺少过期天数");
        }

        // 4. 验证和绑定设备
        validateAndBindDevice(card, loginDTO);

        // 5. 计算剩余天数
        long now = System.currentTimeMillis();
        long remainingMillis = expireTime.getTime() - now;
        long remainingDays = remainingMillis / (24 * 60 * 60 * 1000);

        if (remainingDays < 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密已过期");
        }

        // 6. 生成JWT token（包含卡号和过期时间）
        String cardToken = jwtUtil.generateToken(card.getId(), "card:" + card.getCardNo());

        // 7. 获取关联的游戏列表
        List<CardLoginVO.GameInfo> games = getCardGames(card.getId());

        // 8. 构建返回对象
        CardLoginVO loginVO = new CardLoginVO();
        loginVO.setCardToken(cardToken);
        loginVO.setCardNo(card.getCardNo());
        loginVO.setExpireTime(expireTime);
        loginVO.setRemainingDays(remainingDays);
        loginVO.setDeviceSize(card.getDeviceSize());
        loginVO.setGames(games);
        loginVO.setLoginTime(new Date());

        // 9. 更新卡密最后登录时间（如果expireTime被更新了，也会一起保存）
        card.setLoginDate(new Date());
        cardMapper.updateById(card);

        log.info("卡密登录成功：cardNo={}****, 剩余天数={}", 
                card.getCardNo().substring(0, Math.min(4, card.getCardNo().length())), 
                remainingDays);

        return loginVO;
    }

    /**
     * 验证卡密token
     */
    public CardLoginVO verifyCardToken(String cardToken) {
        log.info("验证卡密token");

        if (cardToken == null || cardToken.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "token不能为空");
        }

        // 1. 验证JWT token
        if (!jwtUtil.validateToken(cardToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "token无效或已过期");
        }

        // 2. 从token中获取卡号
        String username = jwtUtil.getUsernameFromToken(cardToken);
        if (username == null || !username.startsWith("card:")) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "token格式错误");
        }

        String cardNo = username.substring(5); // 去掉"card:"前缀

        // 3. 查询卡密信息
        ScriptCard card = validateCard(cardNo);

        // 4. 验证过期时间
        Date expireTime = card.getExpireTime();
        if (expireTime == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "卡密未激活，请先登录");
        }

        // 5. 计算剩余天数
        long now = System.currentTimeMillis();
        long remainingMillis = expireTime.getTime() - now;
        long remainingDays = remainingMillis / (24 * 60 * 60 * 1000);

        if (remainingDays < 0) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "卡密已过期");
        }

        // 6. 获取关联的游戏列表
        List<CardLoginVO.GameInfo> games = getCardGames(card.getId());

        // 7. 构建返回对象
        CardLoginVO loginVO = new CardLoginVO();
        loginVO.setCardToken(cardToken);
        loginVO.setCardNo(card.getCardNo());
        loginVO.setExpireTime(expireTime);
        loginVO.setRemainingDays(remainingDays);
        loginVO.setDeviceSize(card.getDeviceSize());
        loginVO.setGames(games);
        loginVO.setLoginTime(new Date());

        return loginVO;
    }

    /**
     * 获取卡密信息
     */
    public CardLoginVO getCardInfo(String cardToken) {
        return verifyCardToken(cardToken);
    }

    /**
     * 验证卡密是否有效
     */
    private ScriptCard validateCard(String cardNo) {
        LambdaQueryWrapper<ScriptCard> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCard::getCardNo, cardNo);
        wrapper.eq(ScriptCard::getStatus, 1); // 状态：1-正常

        ScriptCard card = cardMapper.selectOne(wrapper);
        if (card == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密不存在或已停用");
        }

        // 检查是否过期
        if (card.getExpireTime() != null && card.getExpireTime().before(new Date())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "卡密已过期");
        }

        return card;
    }

    /**
     * 验证和绑定设备
     */
    @Transactional(rollbackFor = Exception.class)
    private void validateAndBindDevice(ScriptCard card, CardLoginDTO loginDTO) {
        String deviceId = loginDTO.getDeviceAndroidId();

        // 1. 检查设备是否已绑定
        LambdaQueryWrapper<ScriptCardDevice> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCardDevice::getCardNo, card.getCardNo());
        wrapper.eq(ScriptCardDevice::getDeviceAndroidId, deviceId);

        ScriptCardDevice existDevice = deviceMapper.selectOne(wrapper);

        if (existDevice != null) {
            // 设备已绑定，更新设备信息
            existDevice.setDeviceWidth(loginDTO.getDeviceWidth());
            existDevice.setDeviceHeight(loginDTO.getDeviceHeight());
            existDevice.setDeviceBrand(loginDTO.getDeviceBrand());
            existDevice.setDeviceModel(loginDTO.getDeviceModel());
            existDevice.setDeviceSdkInt(loginDTO.getDeviceSdkInt());
            existDevice.setUpdateTime(new Date());
            deviceMapper.updateById(existDevice);
            log.info("更新已绑定设备信息：deviceId={}****", deviceId.substring(0, Math.min(8, deviceId.length())));
            return;
        }

        // 2. 新设备，检查绑定数量限制
        if (card.getDeviceSize() != null && card.getDeviceSize() > 0) {
            LambdaQueryWrapper<ScriptCardDevice> countWrapper = Wrappers.lambdaQuery();
            countWrapper.eq(ScriptCardDevice::getCardNo, card.getCardNo());
            long boundCount = deviceMapper.selectCount(countWrapper);

            if (boundCount >= card.getDeviceSize()) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),
                        String.format("设备绑定数量已达上限（%d/%d）", boundCount, card.getDeviceSize()));
            }
        }

        // 3. 绑定新设备
        ScriptCardDevice newDevice = new ScriptCardDevice();
        newDevice.setCardNo(card.getCardNo());
        newDevice.setDeviceAndroidId(deviceId);
        newDevice.setDeviceWidth(loginDTO.getDeviceWidth());
        newDevice.setDeviceHeight(loginDTO.getDeviceHeight());
        newDevice.setDeviceBuildId(loginDTO.getDeviceBuildId());
        newDevice.setDeviceBroad(loginDTO.getDeviceBroad());
        newDevice.setDeviceBrand(loginDTO.getDeviceBrand());
        newDevice.setDeviceName(loginDTO.getDeviceName());
        newDevice.setDeviceModel(loginDTO.getDeviceModel());
        newDevice.setDeviceSdkInt(loginDTO.getDeviceSdkInt());
        newDevice.setDeviceImei(loginDTO.getDeviceImei());
        newDevice.setCreateTime(new Date());
        newDevice.setUpdateTime(new Date());

        deviceMapper.insert(newDevice);
        log.info("绑定新设备：cardNo={}****, deviceId={}****",
                card.getCardNo().substring(0, Math.min(4, card.getCardNo().length())),
                deviceId.substring(0, Math.min(8, deviceId.length())));
    }

    /**
     * 获取卡密关联的游戏列表
     */
    private List<CardLoginVO.GameInfo> getCardGames(String cardId) {
        LambdaQueryWrapper<ScriptCardGame> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScriptCardGame::getCardId, cardId);

        List<ScriptCardGame> cardGames = cardGameMapper.selectList(wrapper);

        return cardGames.stream().map(cg -> {
            CardLoginVO.GameInfo gameInfo = new CardLoginVO.GameInfo();
            gameInfo.setGameId(cg.getGameId());
            gameInfo.setGameTitle(cg.getGameTitle());
            return gameInfo;
        }).collect(Collectors.toList());
    }

}
