AndroidSDK - 行云Android前端SDK
=============================

DESCRPTION
-------
行云Android前端SDK

HOWTO
-------
### 编译
在根目录执行ant编译即可。生成的xingcloudandroid.jar会存放于output目录下

### 从旧版行云SDK迁移
在执行ant编译时请传入immigrate参数，值为旧版行云AndroidSDK.jar存放路径。如
	ant -Dimmigrate="~path to you jar"
这种方式迁移，同时也会将前后台通信密钥进行迁移

### 修改前后台通信密钥
在执行ant编译时请传入skey/ckey参数，分别对应secret key及consumer key，请保证此key与后台sdk生成时一一对应。如
	ant -Dskey="skey" -Dckey="ckey"
