// Firebase Realtime Database Operations

// Save a task to the database
function saveTask(taskData) {
  const taskId = taskData.id || database.ref().child('tasks').push().key;
  
  // Create task object
  const task = {
    id: taskId,
    title: taskData.title,
    description: taskData.description,
    assignedTo: taskData.assignedTo,
    assignedBy: taskData.assignedBy,
    status: taskData.status || 'pending',
    deadline: taskData.deadline || null,
    priority: taskData.priority || 'medium',
    createdAt: taskData.createdAt || firebase.database.ServerValue.TIMESTAMP,
    updatedAt: firebase.database.ServerValue.TIMESTAMP
  };
  
  // Create database updates object
  const updates = {};
  updates['/tasks/' + taskId] = task;
  
  // Add to user's tasks list
  updates['/user-tasks/' + taskData.assignedTo + '/' + taskId] = task;
  
  // Add to creator's created tasks list if different from assignee
  if (taskData.assignedBy !== taskData.assignedTo) {
    updates['/user-created-tasks/' + taskData.assignedBy + '/' + taskId] = task;
  }
  
  // Update the database
  return database.ref().update(updates)
    .then(() => {
      console.log("Task saved successfully:", taskId);
      return taskId;
    })
    .catch((error) => {
      console.error("Error saving task:", error);
      throw error;
    });
}

// Get all tasks
function getAllTasks() {
  return database.ref('tasks').once('value')
    .then((snapshot) => {
      const tasks = [];
      snapshot.forEach((childSnapshot) => {
        tasks.push(childSnapshot.val());
      });
      console.log("Retrieved all tasks:", tasks.length);
      return tasks;
    })
    .catch((error) => {
      console.error("Error retrieving tasks:", error);
      throw error;
    });
}

// Get tasks for a specific user
function getUserTasks(userId) {
  return database.ref('user-tasks/' + userId).once('value')
    .then((snapshot) => {
      const tasks = [];
      snapshot.forEach((childSnapshot) => {
        tasks.push(childSnapshot.val());
      });
      console.log("Retrieved user tasks:", tasks.length);
      return tasks;
    })
    .catch((error) => {
      console.error("Error retrieving user tasks:", error);
      throw error;
    });
}

// Update task status
function updateTaskStatus(taskId, newStatus) {
  const updates = {};
  updates['/tasks/' + taskId + '/status'] = newStatus;
  updates['/tasks/' + taskId + '/updatedAt'] = firebase.database.ServerValue.TIMESTAMP;
  
  // Find all places this task appears and update status
  return database.ref('tasks/' + taskId).once('value')
    .then((snapshot) => {
      const task = snapshot.val();
      if (task) {
        updates['/user-tasks/' + task.assignedTo + '/' + taskId + '/status'] = newStatus;
        updates['/user-tasks/' + task.assignedTo + '/' + taskId + '/updatedAt'] = firebase.database.ServerValue.TIMESTAMP;
        
        if (task.assignedBy !== task.assignedTo) {
          updates['/user-created-tasks/' + task.assignedBy + '/' + taskId + '/status'] = newStatus;
          updates['/user-created-tasks/' + task.assignedBy + '/' + taskId + '/updatedAt'] = firebase.database.ServerValue.TIMESTAMP;
        }
        
        return database.ref().update(updates);
      } else {
        throw new Error("Task not found");
      }
    })
    .then(() => {
      console.log("Task status updated successfully:", taskId, newStatus);
      return taskId;
    })
    .catch((error) => {
      console.error("Error updating task status:", error);
      throw error;
    });
}

// Delete a task
function deleteTask(taskId) {
  // First get the task to find all references
  return database.ref('tasks/' + taskId).once('value')
    .then((snapshot) => {
      const task = snapshot.val();
      if (task) {
        const updates = {};
        updates['/tasks/' + taskId] = null;
        updates['/user-tasks/' + task.assignedTo + '/' + taskId] = null;
        
        if (task.assignedBy !== task.assignedTo) {
          updates['/user-created-tasks/' + task.assignedBy + '/' + taskId] = null;
        }
        
        return database.ref().update(updates);
      } else {
        throw new Error("Task not found");
      }
    })
    .then(() => {
      console.log("Task deleted successfully:", taskId);
      return taskId;
    })
    .catch((error) => {
      console.error("Error deleting task:", error);
      throw error;
    });
}

// Save a notification
function saveNotification(notificationData) {
  const notificationId = database.ref().child('notifications').push().key;
  
  const notification = {
    id: notificationId,
    userId: notificationData.userId,
    message: notificationData.message,
    read: false,
    createdAt: firebase.database.ServerValue.TIMESTAMP
  };
  
  const updates = {};
  updates['/notifications/' + notificationId] = notification;
  updates['/user-notifications/' + notificationData.userId + '/' + notificationId] = notification;
  
  return database.ref().update(updates)
    .then(() => {
      console.log("Notification saved successfully:", notificationId);
      return notificationId;
    })
    .catch((error) => {
      console.error("Error saving notification:", error);
      throw error;
    });
}

// Get user notifications
function getUserNotifications(userId) {
  return database.ref('user-notifications/' + userId).orderByChild('createdAt').once('value')
    .then((snapshot) => {
      const notifications = [];
      snapshot.forEach((childSnapshot) => {
        notifications.push(childSnapshot.val());
      });
      console.log("Retrieved user notifications:", notifications.length);
      return notifications.reverse(); // Most recent first
    })
    .catch((error) => {
      console.error("Error retrieving notifications:", error);
      throw error;
    });
}

// Mark notification as read
function markNotificationAsRead(notificationId, userId) {
  const updates = {};
  updates['/notifications/' + notificationId + '/read'] = true;
  updates['/user-notifications/' + userId + '/' + notificationId + '/read'] = true;
  
  return database.ref().update(updates)
    .then(() => {
      console.log("Notification marked as read:", notificationId);
      return notificationId;
    })
    .catch((error) => {
      console.error("Error marking notification as read:", error);
      throw error;
    });
}

// Real-time notification listener (subscribe to new notifications)
function subscribeToUserNotifications(userId, callback) {
  const notificationsRef = database.ref('user-notifications/' + userId);
  
  notificationsRef.on('child_added', (snapshot) => {
    const notification = snapshot.val();
    callback(notification);
  });
  
  return notificationsRef; // Return ref so we can unsubscribe later
}

// Unsubscribe from notifications
function unsubscribeFromNotifications(notificationsRef) {
  if (notificationsRef) {
    notificationsRef.off();
    console.log("Unsubscribed from notifications");
  }
} 