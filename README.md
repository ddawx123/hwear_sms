# hwear_sms
Huawei Watch 3 HarmonyOS SMS Application For Standalone eSIM message receiver
- 因华为watch3官方不支持eSIM独立号卡（仅支持一号双终端），无内置短信app，通过自有渠道开通独立号卡后无法正常接收短信，故制作此简易app处理此问题

>> 服务实现

>>> APP首次启动需要申请短信和服务前台唤醒权限，必须同意权限申请

>>> 消息推送监听基于用户级Service实现，受制于厂商系统定制化，可能存在后台服务被杀无法保活的情况

>>> 请不要拦截通知和开启免打扰模式
