# API 加密使用指南

## 功能概述

本项目实现了基于 RSA + AES 的混合加密方案，用于保护 API 请求和响应数据的安全性。

### 加密流程

1. **请求加密**：
   - 前端生成随机 32 位 AES 密钥
   - 使用 Base64 编码 AES 密钥
   - 使用 RSA 公钥加密 Base64 编码后的 AES 密钥，并放入请求头 `encrypt-key`
   - 使用 AES 密钥加密请求体数据
   - 发送加密后的请求

2. **请求解密**：
   - 后端从请求头获取 RSA 加密的 AES 密钥
   - 使用 RSA 私钥解密获得 Base64 编码的 AES 密钥
   - Base64 解码得到原始 AES 密钥
   - 使用 AES 密钥解密请求体

3. **响应加密**：
   - 后端生成随机 32 位 AES 密钥
   - 使用 AES 密钥加密响应数据
   - 使用 Base64 编码 AES 密钥
   - 使用 RSA 公钥加密 Base64 编码后的 AES 密钥，并放入响应头 `encrypt-key`
   - 返回加密后的响应

4. **响应解密**：
   - 前端从响应头获取 RSA 加密的 AES 密钥
   - 使用 RSA 私钥解密获得 Base64 编码的 AES 密钥
   - Base64 解码得到原始 AES 密钥
   - 使用 AES 密钥解密响应体

## 配置说明

### 1. 数据库配置密钥（推荐）

在 `script_rsa_key` 表中配置 RSA 密钥对：

```sql
-- 查看现有密钥
SELECT * FROM script_rsa_key WHERE is_default = 1 AND status = 0;
```

密钥字段说明：
- `request_public_key`: 请求加密公钥（前端使用）
- `request_private_key`: 请求解密私钥（后端使用）
- `response_public_key`: 响应加密公钥（后端使用）
- `response_private_key`: 响应解密私钥（前端使用）

### 2. 配置文件配置（备用）

在 `application.yml` 中配置默认密钥（当数据库中没有密钥时使用）：

```yaml
api-decrypt:
  enabled: true  # 启用加密
  header-flag: encrypt-key  # 请求头字段名
  public-key: YOUR_RSA_PUBLIC_KEY
  private-key: YOUR_RSA_PRIVATE_KEY
```

### 3. 启用/禁用加密

在 `application.yml` 中：

```yaml
api-decrypt:
  enabled: true  # true=启用，false=禁用
```

## 使用方法

### 后端使用

在需要加密的 Controller 方法上添加 `@ApiEncrypt` 注解：

```java
import org.shanling.simplemanage.config.encrypt.ApiEncrypt;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    // 请求和响应都加密
    @PostMapping("/data")
    @ApiEncrypt(response = true)
    public Result<?> secureEndpoint(@RequestBody DataDTO data) {
        // 处理业务逻辑
        return Result.success(processData(data));
    }

    // 仅请求加密，响应不加密
    @PostMapping("/upload")
    @ApiEncrypt(response = false)
    public Result<?> uploadData(@RequestBody DataDTO data) {
        // 处理业务逻辑
        return Result.success();
    }
}
```

注解参数说明：
- `response`: 是否加密响应数据，默认 `true`

### 前端使用示例

#### JavaScript/TypeScript 示例

```javascript
import CryptoJS from 'crypto-js';
import JSEncrypt from 'jsencrypt';

// RSA 密钥（从后端获取或配置）
const REQUEST_PUBLIC_KEY = 'YOUR_REQUEST_PUBLIC_KEY';
const RESPONSE_PRIVATE_KEY = 'YOUR_RESPONSE_PRIVATE_KEY';

// 加密请求
async function encryptedRequest(url, data) {
    // 1. 生成随机 AES 密钥（32位）
    const aesKey = CryptoJS.lib.WordArray.random(32).toString();
    
    // 2. Base64 编码 AES 密钥
    const aesKeyBase64 = btoa(aesKey);
    
    // 3. RSA 加密 AES 密钥
    const encrypt = new JSEncrypt();
    encrypt.setPublicKey(REQUEST_PUBLIC_KEY);
    const encryptedAesKey = encrypt.encrypt(aesKeyBase64);
    
    // 4. AES 加密请求数据
    const jsonData = JSON.stringify(data);
    const encryptedData = CryptoJS.AES.encrypt(
        jsonData, 
        CryptoJS.enc.Utf8.parse(aesKey),
        {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        }
    ).toString();
    
    // 5. 发送请求
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'encrypt-key': encryptedAesKey
        },
        body: encryptedData
    });
    
    // 6. 解密响应
    const encryptedResponse = await response.text();
    const responseAesKey = response.headers.get('encrypt-key');
    
    if (responseAesKey) {
        // RSA 解密 AES 密钥
        const decrypt = new JSEncrypt();
        decrypt.setPrivateKey(RESPONSE_PRIVATE_KEY);
        const decryptedAesKeyBase64 = decrypt.decrypt(responseAesKey);
        const decryptedAesKey = atob(decryptedAesKeyBase64);
        
        // AES 解密响应数据
        const decryptedBytes = CryptoJS.AES.decrypt(
            encryptedResponse,
            CryptoJS.enc.Utf8.parse(decryptedAesKey),
            {
                mode: CryptoJS.mode.ECB,
                padding: CryptoJS.pad.Pkcs7
            }
        );
        
        const decryptedData = decryptedBytes.toString(CryptoJS.enc.Utf8);
        return JSON.parse(decryptedData);
    }
    
    return JSON.parse(encryptedResponse);
}

// 使用示例
const result = await encryptedRequest('/api/secure/data', {
    name: 'test',
    value: 123
});
console.log(result);
```

## 密钥生成

### 使用后端接口生成

访问 ScriptRsaKeyController 提供的密钥管理接口。

### 使用工具类生成

```java
import org.shanling.simplemanage.util.EncryptUtils;
import java.util.Map;

public class KeyGenerator {
    public static void main(String[] args) {
        // 生成请求密钥对
        Map<String, String> requestKeyPair = EncryptUtils.generateRsaKey();
        System.out.println("Request Public Key: " + requestKeyPair.get("publicKey"));
        System.out.println("Request Private Key: " + requestKeyPair.get("privateKey"));
        
        // 生成响应密钥对
        Map<String, String> responseKeyPair = EncryptUtils.generateRsaKey();
        System.out.println("Response Public Key: " + responseKeyPair.get("publicKey"));
        System.out.println("Response Private Key: " + responseKeyPair.get("privateKey"));
    }
}
```

## 注意事项

1. **密钥安全**：
   - 私钥必须妥善保管，不能泄露
   - 建议在数据库中配置密钥，不要硬编码在代码中
   - 生产环境定期更换密钥

2. **性能考虑**：
   - 加密会增加请求响应时间
   - 仅对敏感接口启用加密
   - 可以通过配置开关灵活控制

3. **错误处理**：
   - 如果密钥配置错误，接口会返回错误信息
   - 确保前后端使用的密钥对匹配

4. **开发调试**：
   - 开发环境可以设置 `api-decrypt.enabled=false` 禁用加密
   - 便于使用 Postman 等工具测试接口

5. **前端实现兼容**：
   - 后端已适配 AutoJS 的 `http.postJson()` 发送方式
   - 会自动处理 JSON 序列化导致的加密数据格式问题
   - 如果使用其他前端框架，建议发送纯文本格式的加密数据

## 故障排查

### 问题：接口返回"加密密钥未配置"

**解决方案**：
1. 检查数据库 `script_rsa_key` 表是否有默认密钥（`is_default=1` 且 `status=0`）
2. 如果没有，在 `application.yml` 中配置默认密钥
3. 或者调用密钥管理接口创建默认密钥

### 问题：请求解密失败

**解决方案**：
1. 检查前端是否正确设置请求头 `encrypt-key`
2. 确认前端使用的公钥与后端私钥是配对的
3. 检查 AES 密钥长度是否为 32 位
4. 确认加密算法和模式一致（AES/ECB/PKCS7Padding）

### 问题：响应解密失败

**解决方案**：
1. 检查响应头 `encrypt-key` 是否正确返回
2. 确认前端使用的私钥与后端公钥是配对的
3. 检查解密算法和模式一致

## 扩展功能

如需支持多租户不同密钥，可以修改 `ScriptRsaKeyProvider`，根据租户ID获取不同的密钥配置。

## 相关文件

- 加密工具类：`org.shanling.simplemanage.util.EncryptUtils`
- 加密注解：`org.shanling.simplemanage.config.encrypt.ApiEncrypt`
- 配置类：`org.shanling.simplemanage.config.encrypt.ApiDecryptProperties`
- 过滤器：`org.shanling.simplemanage.config.encrypt.CryptoFilter`
- 密钥提供器：`org.shanling.simplemanage.config.encrypt.ScriptRsaKeyProvider`
