#  Push Notification 流程介绍 
基于AndroidPN 改造的消息推送，支持自定义协议
## 消息处理过程 ##
* 客户端服务器交互初始化
* 用户登录认证
* 客户端发起在线心跳通知
* 发送消息时若用户在线则直接发送，否则保存到数据库
* 服务器从数据库中获取未接收过的消息列表，循环发送同时更新发送状态为已发送，接收通知更新状态为已接收，再次收到通知则更新为已读
* 消息状态变化过程 未发送(0)->已发送(1)->已接收(2)->已读(3)

## XMPP 协议交互过程 ##
### 初始化
> 客户端请求
```
<stream:stream to="192.168.167.131" xmlns="jabber:client" xmlns:stream="http://etherx.jabber.org/streams" version="1.0">
```
> 服务器应答
```
<?xml version='1.0' encoding='UTF-8'?><stream:stream xmlns:stream="http://etherx.jabber.org/streams" xmlns="jabber:client" from="127.0.0.1" id="8c73c61" xml:lang="en" version="1.0">
```
***
### TLS询问
> 服务器请求：
```
<stream:features><starttls xmlns="urn:ietf:params:xml:ns:xmpp-tls"></starttls><auth xmlns="http://jabber.org/features/iq-auth"/><register xmlns="http://jabber.org/features/iq-register"/></stream:features>
```
> 客户端应答：
```
<starttls xmlns="urn:ietf:params:xml:ns:xmpp-tls"/>
```
> 服务器应答:
```
<proceed xmlns="urn:ietf:params:xml:ns:xmpp-tls"/>
```

### 登录认证过程
> 客户端请求：
```
<stream:stream to="127.0.0.1" xmlns="jabber:client" xmlns:stream="http://etherx.jabber.org/streams" version="1.0">
```
> 服务器应答：
```
<?xml version='1.0' encoding='UTF-8'?><stream:stream xmlns:stream="http://etherx.jabber.org/streams" xmlns="jabber:client" from="127.0.0.1" id="8c73c61" xml:lang="en" version="1.0"><stream:features><auth xmlns="http://jabber.org/features/iq-auth"/><register xmlns="http://jabber.org/features/iq-register"/></stream:features>
```
> 查询用户信息请求
```
<iq id="5yf2Q-192" type="get"><query xmlns="jabber:iq:auth"><username>9278a6a6a33b47cfa6bfd212b226440c</username></query></iq>
```
```
<iq type="result" id="5yf2Q-192"><query xmlns="jabber:iq:auth"><username>9278a6a6a33b47cfa6bfd212b226440c</username><password/><digest/><resource/></query></iq>
```
> 用户认证请求
```
<iq id="5yf2Q-193" type="set"><query xmlns="jabber:iq:auth"><username>9278a6a6a33b47cfa6bfd212b226440c</username><digest>bb3214907ed9eee5f7e847fdf80d3784dc6a6417</digest><resource>AndroidpnClient</resource></query></iq>
```
应答
```
<iq type="result" id="5yf2Q-193" to="9278a6a6a33b47cfa6bfd212b226440c@127.0.0.1/AndroidpnClient"/>
```
> 发送在线心跳
```
<presence id="5yf2Q-195"></presence>
```
