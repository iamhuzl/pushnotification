<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Admin Console</title>
	<meta name="menu" content="notification" />    
</head>

<body>

<h1>发送通知</h1>

<%--<div style="background:#eee; margin:20px 0px; padding:20px; width:500px; border:solid 1px #999;">--%>
<div style="margin:20px 0px;">
<form action="notification.do?action=send" method="post" style="margin: 0px;">
<table width="600" cellpadding="4" cellspacing="0" border="0">
<tr>
	<td width="20%">发送给:</td>
	<td width="80%">
		<input type="radio" name="broadcast" value="Y" checked="checked" />  在线用户 
        <input type="radio" name="broadcast" value="N" /> 指定用户(多个用户之间用英文;分隔)
        <input type="radio" name="broadcast" value="A" /> 所有用户  
	</td>
</tr>
<tr id="trUsername" style="display:none;">
	<td>用户名:</td>
	<td><input type="text" id="username" name="username" value="" style="width:380px;" /></td>
</tr>
    <tr>
    	<td>通知类型:</td>
    	<td>
            <select name="type" style="width:380px;" >
                <option value="">通用消息</option>
                <option value="1">聊天消息通知</option>
                <option value="2">群组成员变更通知</option>
                <option value="3">群组邀请加入通知</option>
                <option value="4">群组邀请结果通知</option>
                <option value="5">好友分享通知</option>
            </select>
    </tr>
<tr>
	<td>通知标题:</td>
	<td><input type="text" id="title" name="title" value="中奖通知" style="width:380px;" /></td>
</tr>
<tr>
	<td>通知消息内容:</td>
	<td><textarea id="message" name="message" style="width:380px; height:80px;" >恭喜您获得了您参加了我爱幻方活动中获得了iPhone5S大奖</textarea></td>
</tr>
<%--
<tr>
	<td>Ticker:</td>
	<td><input type="text" id="ticker" name="ticker" value="" style="width:380px;" /></td>
</tr>
--%>
<tr>
	<td>URI:</td>
	<td><input type="text" id="uri" name="uri" value="http://192.168.167.131:8080/PushNotification/notification.do" style="width:380px;" />
	    <br/><span style="font-size:0.8em">ex) http://www.dokdocorea.com, geo:37.24,131.86, tel:111-222-3333</span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td><input type="submit" value=" 发 送 " /></td>
</tr>
</table> 
</form>
</div>

<script type="text/javascript"> 
//<![CDATA[
 
$(function() {
	$('input[name=broadcast]').click(function() {
		if ($('input[name=broadcast]')[1].checked) {
			$('#trUsername').show();
		} else {
			$('#trUsername').hide();
		}
	});
	
	if ($('input[name=broadcast]')[1].checked) {
		$('#trUsername').show();
	} else {
		$('#trUsername').hide();
	}	
});
 
//]]>
</script>

</body>
</html>
