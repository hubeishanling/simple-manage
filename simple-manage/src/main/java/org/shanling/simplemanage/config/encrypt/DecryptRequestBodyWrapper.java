package org.shanling.simplemanage.config.encrypt;

import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.shanling.simplemanage.util.EncryptUtils;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 解密请求参数包装器
 *
 * @author shanling
 */
public class DecryptRequestBodyWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public DecryptRequestBodyWrapper(HttpServletRequest request, String privateKey, String headerFlag) throws IOException {
        super(request);
        // 获取 AES 密码 采用 RSA 加密
        String headerRsa = request.getHeader(headerFlag);
        String decryptAes = EncryptUtils.decryptByRsa(headerRsa, privateKey);
        // 解密 AES 密码
        String aesPassword = EncryptUtils.decryptByBase64(decryptAes);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
        String requestBody = new String(readBytes, StandardCharsets.UTF_8);
        
        // 处理前端 postJson 序列化的问题
        // 前端使用 http.postJson() 会将加密字符串再次JSON序列化，导致多了外层引号
        // 例如："abc123..." 变成 "\"abc123...\""
        // 需要先去掉JSON字符串的外层引号
        String encryptedData = requestBody;
        if (requestBody.startsWith("\"") && requestBody.endsWith("\"")) {
            // 去掉首尾的引号，并处理转义字符
            encryptedData = requestBody.substring(1, requestBody.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        
        // 解密 body 采用 AES 加密
        String decryptBody = EncryptUtils.decryptByAes(encryptedData, aesPassword);
        body = decryptBody.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
