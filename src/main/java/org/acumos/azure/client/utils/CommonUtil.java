package org.acumos.azure.client.utils;

import java.util.Date;

import org.acumos.azure.client.service.impl.AzureCompositeSolution;
import org.acumos.azure.client.transport.MLNotification;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	Logger logger =LoggerFactory.getLogger(CommonUtil.class);	
	/**
	 * 
	 * @param notificationId
	 * @param userId
	 */
	public CommonDataServiceRestClientImpl getClient(String datasource,String userName,String password) {
		CommonDataServiceRestClientImpl client = new CommonDataServiceRestClientImpl(datasource, userName, password);
		return client;
	}
	/*public void addNotificationUser(String notificationId, String userId) {
        logger.debug("addNotificationUser");
    	CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
    	client.addUserToNotification(notificationId,userId);
     }*/
	
	
	/**
	 * 
	 * @param mlpNotification
	 * @return
	 */
	 public org.acumos.azure.client.transport.MLNotification createNotification(MLPNotification mlpNotification,
			 String dataSource,String dataUserName,String dataPassword) {
		 logger.debug("Start===createNotification============");
         CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
         MLNotification mlNotification = Utils.convertToMLNotification(client.createNotification(mlpNotification));
         logger.debug("End===createNotification============");
         return mlNotification;
	 }
	 
	/**
	  * 
	  * @param msg
	  * @param userId
	  */
	 public void generateNotification(String msg, String userId,String dataSource,String dataUserName,String dataPassword) {
		 logger.debug("Start===generateNotification============");
		 logger.debug("=====userId====="+userId+"==msg==="+msg);
         MLPNotification notification = new MLPNotification();
         try {
                 if (msg != null) {
                     notification.setTitle(msg);
                     // Provide the IP address and port of the probe Instance
                     notification.setMessage(msg);
                     Date startDate = new Date();
                     Date endDate = new Date(startDate.getTime() + (1000 * 60 * 60 * 24));
                     notification.setStart(startDate);
                     notification.setEnd(endDate);
                     CommonDataServiceRestClientImpl client=getClient(dataSource,dataUserName,dataPassword);
                     notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
                     MLNotification mLNotification = createNotification(notification,dataSource,dataUserName,dataPassword);
                     logger.debug("=====mLNotification.getNotificationId()====="+mLNotification.getNotificationId());
                     client.addUserToNotification(mLNotification.getNotificationId(),userId);
             }
         } catch (Exception e) {
            logger.error("Exception Occurred while getNotifications", e);
         }
         logger.debug("End===generateNotification============"); 
	 }

}
