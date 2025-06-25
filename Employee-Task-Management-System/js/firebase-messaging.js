// Firebase Real-time Messaging

// Save a message to the database
function sendMessage(messageData) {
  const messageId = database.ref().child('messages').push().key;
  
  // Create message object
  const message = {
    id: messageId,
    senderId: messageData.senderId,
    senderName: messageData.senderName,
    receiverId: messageData.receiverId,
    content: messageData.content,
    timestamp: firebase.database.ServerValue.TIMESTAMP,
    read: false
  };
  
  // Create conversation ID (sorted user IDs to ensure same ID for both users)
  const userIds = [messageData.senderId, messageData.receiverId].sort();
  const conversationId = userIds.join('_');
  
  // Create database updates object
  const updates = {};
  updates['/messages/' + messageId] = message;
  updates['/conversations/' + conversationId + '/messages/' + messageId] = message;
  updates['/conversations/' + conversationId + '/participants/' + messageData.senderId] = true;
  updates['/conversations/' + conversationId + '/participants/' + messageData.receiverId] = true;
  updates['/conversations/' + conversationId + '/lastMessage'] = message;
  updates['/conversations/' + conversationId + '/lastMessageTimestamp'] = firebase.database.ServerValue.TIMESTAMP;
  
  // Update user conversation lists
  updates['/user-conversations/' + messageData.senderId + '/' + conversationId] = {
    withUser: messageData.receiverId,
    lastMessageTimestamp: firebase.database.ServerValue.TIMESTAMP
  };
  
  updates['/user-conversations/' + messageData.receiverId + '/' + conversationId] = {
    withUser: messageData.senderId,
    lastMessageTimestamp: firebase.database.ServerValue.TIMESTAMP,
    unread: firebase.database.ServerValue.increment(1)
  };
  
  // Update the database
  return database.ref().update(updates)
    .then(() => {
      console.log("Message sent successfully:", messageId);
      return messageId;
    })
    .catch((error) => {
      console.error("Error sending message:", error);
      throw error;
    });
}

// Get all conversations for a user
function getUserConversations(userId) {
  return database.ref('user-conversations/' + userId).orderByChild('lastMessageTimestamp').once('value')
    .then((snapshot) => {
      const conversations = [];
      const promises = [];
      
      snapshot.forEach((childSnapshot) => {
        const conversationId = childSnapshot.key;
        const conversationData = childSnapshot.val();
        
        // Get details of the other user
        const otherUserId = conversationData.withUser;
        const promise = database.ref('users/' + otherUserId).once('value')
          .then((userSnapshot) => {
            const userData = userSnapshot.val() || { username: 'Unknown User' };
            
            // Get the last message
            return database.ref('conversations/' + conversationId + '/lastMessage').once('value')
              .then((messageSnapshot) => {
                const lastMessage = messageSnapshot.val() || {};
                
                conversations.push({
                  id: conversationId,
                  user: {
                    id: otherUserId,
                    username: userData.username,
                    fullName: userData.fullName || userData.username,
                    email: userData.email
                  },
                  lastMessage: {
                    content: lastMessage.content,
                    timestamp: lastMessage.timestamp,
                    senderId: lastMessage.senderId
                  },
                  unread: conversationData.unread || 0
                });
              });
          });
        
        promises.push(promise);
      });
      
      return Promise.all(promises).then(() => {
        console.log("Retrieved user conversations:", conversations.length);
        // Sort by last message timestamp (newest first)
        return conversations.sort((a, b) => (b.lastMessage.timestamp || 0) - (a.lastMessage.timestamp || 0));
      });
    })
    .catch((error) => {
      console.error("Error retrieving conversations:", error);
      throw error;
    });
}

// Get all messages in a conversation
function getConversationMessages(conversationId) {
  return database.ref('conversations/' + conversationId + '/messages').orderByChild('timestamp').once('value')
    .then((snapshot) => {
      const messages = [];
      snapshot.forEach((childSnapshot) => {
        messages.push(childSnapshot.val());
      });
      console.log("Retrieved conversation messages:", messages.length);
      return messages;
    })
    .catch((error) => {
      console.error("Error retrieving conversation messages:", error);
      throw error;
    });
}

// Mark conversation as read
function markConversationAsRead(conversationId, userId) {
  // First, get all unread messages in this conversation
  return database.ref('conversations/' + conversationId + '/messages').orderByChild('read').equalTo(false).once('value')
    .then((snapshot) => {
      const updates = {};
      
      // Mark each message as read
      snapshot.forEach((childSnapshot) => {
        const message = childSnapshot.val();
        if (message.receiverId === userId) {
          updates['/conversations/' + conversationId + '/messages/' + message.id + '/read'] = true;
          updates['/messages/' + message.id + '/read'] = true;
        }
      });
      
      // Reset unread counter
      updates['/user-conversations/' + userId + '/' + conversationId + '/unread'] = 0;
      
      return database.ref().update(updates);
    })
    .then(() => {
      console.log("Conversation marked as read:", conversationId);
      return conversationId;
    })
    .catch((error) => {
      console.error("Error marking conversation as read:", error);
      throw error;
    });
}

// Listen for new messages in a conversation
function subscribeToConversation(conversationId, callback) {
  const messagesRef = database.ref('conversations/' + conversationId + '/messages');
  
  messagesRef.orderByChild('timestamp').startAfter(Date.now()).on('child_added', (snapshot) => {
    const message = snapshot.val();
    callback(message);
  });
  
  return messagesRef; // Return ref so we can unsubscribe later
}

// Unsubscribe from conversation updates
function unsubscribeFromConversation(messagesRef) {
  if (messagesRef) {
    messagesRef.off();
    console.log("Unsubscribed from conversation");
  }
}

// Get total unread message count for a user
function getTotalUnreadMessages(userId) {
  return database.ref('user-conversations/' + userId).once('value')
    .then((snapshot) => {
      let totalUnread = 0;
      snapshot.forEach((childSnapshot) => {
        const conversationData = childSnapshot.val();
        totalUnread += conversationData.unread || 0;
      });
      console.log("Total unread messages:", totalUnread);
      return totalUnread;
    })
    .catch((error) => {
      console.error("Error getting unread count:", error);
      throw error;
    });
} 