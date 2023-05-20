## 项目背景
用于宣传当地地方而编写的旅游宣传网站
## 应用场景
主要用于宣传当地旅游信息，满足用户对一个陌生地方的了解。
## 页面展示
### 用户的登录模块
1.登录、注册，设置有正则校验。
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684383449509-8e5b92e6-3392-44c0-87cc-ceb54263d2f2.png#averageHue=%23ced9e8&clientId=ue9a689c2-20f2-4&from=paste&height=609&id=u24cdd6fd&originHeight=609&originWidth=1264&originalType=binary&ratio=1&rotation=0&showTitle=false&size=266367&status=done&style=none&taskId=ue8cc12cd-74bb-4aba-862d-34b2288fafa&title=&width=1264)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684383517645-ffb3c7f1-1503-4351-a905-cf70cdaa6c4c.png#averageHue=%23c8d5e6&clientId=ue9a689c2-20f2-4&from=paste&height=608&id=u49a026fe&originHeight=608&originWidth=1115&originalType=binary&ratio=1&rotation=0&showTitle=false&size=239903&status=done&style=none&taskId=u502044a9-062e-4605-918c-311f8887ae0&title=&width=1115)
2.找回密码，通过邮箱发送用户验证码校验用户信息，验证码有效时间5分钟，用Redis存贮验证码。
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684383500443-76a61b9d-48f2-400f-91dc-15cfdb00a5c2.png#averageHue=%23cbc29b&clientId=ue9a689c2-20f2-4&from=paste&height=327&id=ud357656f&originHeight=327&originWidth=1243&originalType=binary&ratio=1&rotation=0&showTitle=false&size=35610&status=done&style=none&taskId=u46188595-417a-4d9a-9580-f6466775ff6&title=&width=1243)
### 主页浏览
1.主页展示热门酒店、景点、旅游攻略、旅游路线等信息。轮播图则使用的是jQuery的无限轮播图插件。
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684383657338-ffa10722-2014-47be-aab8-936f4385a5a0.png#averageHue=%23a3c7ce&clientId=ue9a689c2-20f2-4&from=paste&height=923&id=uc4026867&originHeight=923&originWidth=1486&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1029450&status=done&style=none&taskId=ub784ec02-b1a6-4256-b109-d462cb89793&title=&width=1486)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684383806064-1480d25e-e826-4204-bef8-1a8a7df7a36b.png#averageHue=%23addcdc&clientId=ue9a689c2-20f2-4&from=paste&height=899&id=u119879c0&originHeight=899&originWidth=1365&originalType=binary&ratio=1&rotation=0&showTitle=false&size=322437&status=done&style=none&taskId=u885ee0b1-17c5-4f1d-91b7-8b9b0c1972c&title=&width=1365)
### 后台管理
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684384105265-02151d4f-f978-4de3-9109-109ebffd4ac9.png#averageHue=%23e9e8e8&clientId=ue9a689c2-20f2-4&from=paste&height=614&id=uc21c8618&originHeight=614&originWidth=1920&originalType=binary&ratio=1&rotation=0&showTitle=false&size=53905&status=done&style=none&taskId=u7b55500e-49d7-4c69-879f-8b7879c2c7a&title=&width=1920)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/32622406/1684384116225-e8f0aa56-f627-4770-8cac-68045bab639b.png#averageHue=%23e9e7e7&clientId=ue9a689c2-20f2-4&from=paste&height=681&id=u2873d32d&originHeight=681&originWidth=1920&originalType=binary&ratio=1&rotation=0&showTitle=false&size=59561&status=done&style=none&taskId=ue97994d1-61bc-4086-a2a1-57f474b08ee&title=&width=1920)
## 技术栈

---

### 前端	
主要技术：

- Thymleaf
- jQuery
- Bootrap
- Echarts
### 后端
主要技术：

- Spring Boot 2.1.4
- MySQL
- Redis
- JPA

依赖库：

- Gson：Json 解析
- Easy Excel：Excel 导入导出
## 快速启动

---

运行SQL文件，配置文件修改，下载依赖。
