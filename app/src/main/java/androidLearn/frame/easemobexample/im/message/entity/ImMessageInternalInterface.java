package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.im.message.ImMessageStatus;
import androidLearn.frame.easemobExample.im.message.ImMessageType;

public interface ImMessageInternalInterface {
  /**
  * 消息id
  */
  String getMessageId();

  /**
   * 消息所属会话id
   */
  String getConversationId();

  /**
   * 对于文本消息来说他就是文字内容；
   * 对于图片消息则为“[图片]”语音消息为“[语音]”,用于会话列表上显示会话的最后一条消息时使用。
   */
  String getText();

  /**
   * 获取消息类型
   */
  ImMessageType getType();

  /**
   * 特殊用途，例如获取leancloud message中的时间戳或者message id，用于检索特定时间段历史记录
   */
  Object getToken();

  /**
   * 获取发送者ID，只对接受到的消息有效
   */
  String getSenderId();

  /**
   * 接收者者ID，只对接受到的消息有效
   */
  String getSendToId();

  /**
   * 获取消息时间
   */
  long getTime();

  /**
   * 设置是否已读
   */
  void setRead(boolean isRead);

  /**
   * 获取是否已读
   */
  boolean isRead();

  /**
   * 获取消息发送状态
   */
  ImMessageStatus getStatus();

  /**
   * 设置消息发送状态
   */
  void setStatus(ImMessageStatus status);

  /**
   * 是否为发送消息，true为发送的消息，false为接收的消息
   */
  boolean isSendMessage();

  /**
   * 获取所使用的IM SDK中实际消息体的对象，例如使用环信时是EMMessage
   */
  Object getMessageObject();

  /**
   * 设置所使用的IM SDK中实际消息体的对象，例如使用环信时是EMMessage
   */
  void setMessageObject(Object msg);

  void setAttribute(String key, Object value);

  Object getAttribute(String key);
}
