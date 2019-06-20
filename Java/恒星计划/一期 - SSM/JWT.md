### JWT

#### 背景

![1558778915434](assets/1558778915434.png)



![1558778933525](assets/1558778933525.png) 

#### 介绍

```
JSON Web Token（JWT）是一个开放的行业标准（RFC 7519），它定义了一种简介的、自包含的协议格式，用于在通信双方传递json对象，传递的信息经过数字签名可以被验证和信任。JWT可以使用HMAC算法或使用RSA的公钥/私钥对来签名，防止被篡改。

官网：https://jwt.io/

标准：https://tools.ietf.org/html/rfc7519
```



#### 优缺点

```
优点：

    1、jwt基于json，非常方便解析。
    
    2、可以在令牌中 自定义 丰富的内容，易扩展。
    
    3、通过非对称加密算法及数字签名技术，JWT防止篡改，安全性高。
    
    4、资源服务使用JWT可不依赖认证服务即可完成授权。
    
缺点：

	１、JWT令牌较长，占存储空间比较大。
	
```



#### 组成

```
JWT令牌由三部分组成，每部分中间使用点（.）分隔，比如：xxxxx.yyyyy.zzzzz

Header

    头部包括令牌的类型（即JWT）及使用的哈希算法（如HMAC SHA256或RSA）
    
    一个例子如下：
    
    下边是Header部分的内容
    
    {
      "alg": "HS256",
      "typ": "JWT"
    }
    
    将上边的内容使用Base64Url编码，得到一个字符串就是JWT令牌的第一部分。

Payload

    第二部分是负载，内容也是一个json对象，它是存放有效信息的地方，它可以存放jwt提供的现成字段，比如：iss（签发者）,exp（过期时间戳）, sub（面向的用户）等，也可自定义字段。此部分不建议存放敏感信息，因为此部分可以解码还原原始内容。最后将第二部分负载使用Base64Url编码，得到一个字符串就是JWT令牌的第二部分。

一个例子：
{
  "sub": "1234567890",
  "name": "Itcast",
  "admin": true,
  "age":20
}

Signature

	第三部分是签名，此部分用于防止jwt内容被篡改。这个部分使用base64url将前两部分进行编码，编码后使用点（.）连接组成字符串，最后使用header中声明, 签名算法进行签名。


一个例子：

    HMACSHA256( base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)


    base64UrlEncode(header) ：jwt令牌的第一部分。
    
    base64UrlEncode(payload)：jwt令牌的第二部分。
    
    secret：签名所使用的密钥。
    
```

![1558778861498](assets/1558778861498.png) 



#### 入门

```
JWT令牌生成采用非对称加密算法 , 需要生成对应的私钥和公钥 .
```

##### 生成私钥和公钥

1)  生成密钥证书

```
下边命令生成密钥证书，采用RSA 算法每个证书包含公钥和私钥

keytool -genkeypair -alias itcastkey -keyalg RSA -keypass itcast -keystore itcast.keystore -storepass itcast

指令解析 : 

    Keytool 是一个java提供的证书管理工具
    
    -alias：密钥的别名
    
    -keyalg：使用的hash算法
    
    -keypass：密钥的访问密码
    
    -keystore：密钥库文件名，itcast.keystore保存了生成的证书
    
    -storepass：密钥库的访问密码
    
    
    
查询证书信息：
	
	keytool -list -keystore itcast.keystore

删除别名
	
	keytool -delete -alias itcastkey -keystore itcast.keystore
	
```



2)  导出公钥

```
openssl是一个加解密工具包，这里使用openssl来导出公钥信息。

安装 openssl：http://slproweb.com/products/Win32OpenSSL.html

安装资料目录下的 Win64OpenSSL-1_1_0g.exe

配置openssl的path环境变量，本教程配置在 D:\OpenSSL-Win64\bin

cmd进入 itcast.keystore 文件所在目录执行如下命令：


	keytool -list -rfc --keystore itcast.keystore | openssl x509 -inform pem -pubkey

```

```
-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw9MbPMkfRgaLTqGRUhxufW/nhiRs4bMAmah2K1Inv+IanqsOn3gYITS99BNjpRRdA2NKQeoqkW7FO0oeI60K+5DhYJwU/PnV8dA5BT7C3S+KAfk8IQqv/o4R0dEY5Dn65DAlHF6gpB14bJ42uA5XZGffD08BNMjjjfYBzG8WT2z6TlNeEijhOn70KoCsU5NbAIK1JXEZ7KytWZts/tao3vGekc/HLzDvPNprh+3BSyyzJ/7nihLr1PLdeSFH8dwdMXappCjdySEQQEqGDS0B+RReeR1nVm3/Gzd+5okh0iRt6wD/+IbBMsK1fzpDzFf+8GQyxWx/pKC3Z7m578c9PwIDAQAB-----END PUBLIC KEY-----
```













