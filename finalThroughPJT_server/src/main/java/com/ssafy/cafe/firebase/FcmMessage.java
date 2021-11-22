package com.ssafy.cafe.firebase;

import java.util.Map;


public class FcmMessage {
	 private boolean validate_only;
	    private Message message;
	    
	    public FcmMessage() {}
	    public FcmMessage(boolean validate_only, Message message) {
			super();
			this.validate_only = validate_only;
			this.message = message;
		}


		public boolean isValidate_only() {
			return validate_only;
		}


		public void setValidate_only(boolean validate_only) {
			this.validate_only = validate_only;
		}


		public Message getMessage() {
			return message;
		}


		public void setMessage(Message message) {
			this.message = message;
		}


		/** Message
		 * 
		 * @author USER1
		 *
		 */
		public static class Message {
	        private Notifications notification;
	        private String token;
	        
	        // background에서 보내려면
	        private Map<String, String> data;
	        
	        public Map<String, String> getData() {
				return data;
			}

			public void setData(Map<String, String> data) {
				this.data = data;
			}
			//

			public Message() {}
	        
			public Message(Notifications notification, String token) {
				super();
				this.notification = notification;
				this.token = token;
			}
			public Notifications getNotification() {
				return notification;
			}
			public void setNotification(Notifications notification) {
				this.notification = notification;
			}
			public String getToken() {
				return token;
			}
			public void setToken(String token) {
				this.token = token;
			}

	    }


		/** Notification
		 * 
		 * @author USER1
		 *
		 */
	    public static class Notifications {
	        private String title;
	        private String body;
	        private String image;
	        
	      	        
			public Notifications(String title, String body, String image) {
				super();
				this.title = title;
				this.body = body;
				this.image = image;
			}
			
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public String getBody() {
				return body;
			}
			public void setBody(String body) {
				this.body = body;
			}
			public String getImage() {
				return image;
			}
			public void setImage(String image) {
				this.image = image;
			}
	        
	        
	        
	        
	    }
}
