package sdei.detector.inspector.sync.service.engine;
 
import sdei.detector.inspector.sync.service.ws.WSResponse;
import sdei.detector.inspector.sync.service.ws.WSRequest;
/**
 *	The Sycn engine service used to sync data between mobile client and server
 */
interface ISyncEngine { 
	void cancelSync(in String sync_id);
	void cancelSyncGroup(in String sync_group_id);
	String login(in WSRequest request, in String email, in String pass);
	String forgotPass(in WSRequest request, in String email);
	String getProperty(in WSRequest request, in String technicianId);
	String getBookingProperty(in WSRequest request, in String technicianId);
	String[] uploadPhoto(in WSRequest request, in String report_uuid, in String photo_uuid, in String report_id);
	String[] addReport(in WSRequest request, in String report_uuid, in String path,in String lat,in String lng,in String bookingId,in String technicianId,in String serviceSheetId);
	String unAllocatedBooking(in WSRequest request, in String technicianId,in String bookingId,in String reason,in String propertyId,in String key);
	String sendNotificationMessage(in WSRequest request, in String message);
	String saveLocation(in WSRequest request, in String lat, in String lng, in String technicianId);
	String getHistoryProperty(in WSRequest request, in String technicianId);
	
 }	                  