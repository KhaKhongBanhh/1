// Initialize Firebase
const firebaseConfig = JSON.parse(document.getElementById('firebaseConfig').textContent);
firebase.initializeApp(firebaseConfig);

// Get references to Firebase services
const auth = firebase.auth();
const database = firebase.database();

// Authentication state observer
auth.onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in
    console.log("User is signed in:", user.uid);
    document.getElementById('loginStatus').textContent = 'Logged in as: ' + user.email;
    
    // Get user data from Realtime Database
    getUserData(user.uid);
  } else {
    // User is signed out
    console.log("User is signed out");
    document.getElementById('loginStatus').textContent = 'Not logged in';
  }
});

// Login function
function firebaseLogin(email, password) {
  return auth.signInWithEmailAndPassword(email, password)
    .then((userCredential) => {
      // Signed in
      const user = userCredential.user;
      console.log("Login successful:", user.uid);
      return user;
    })
    .catch((error) => {
      const errorCode = error.code;
      const errorMessage = error.message;
      console.error("Login error:", errorCode, errorMessage);
      throw error;
    });
}

// Register function
function firebaseRegister(email, password, userData) {
  return auth.createUserWithEmailAndPassword(email, password)
    .then((userCredential) => {
      // Signed up
      const user = userCredential.user;
      console.log("Registration successful:", user.uid);
      
      // Store additional user data in Realtime Database
      return storeUserData(user.uid, userData);
    })
    .catch((error) => {
      const errorCode = error.code;
      const errorMessage = error.message;
      console.error("Registration error:", errorCode, errorMessage);
      throw error;
    });
}

// Logout function
function firebaseLogout() {
  return auth.signOut()
    .then(() => {
      console.log("Logout successful");
    })
    .catch((error) => {
      console.error("Logout error:", error);
      throw error;
    });
}

// Store user data in Realtime Database
function storeUserData(userId, userData) {
  return database.ref('users/' + userId).set({
    email: userData.email,
    username: userData.username,
    role: userData.role,
    fullName: userData.fullName,
    timestamp: firebase.database.ServerValue.TIMESTAMP
  })
  .then(() => {
    console.log("User data stored successfully");
    return userId;
  })
  .catch((error) => {
    console.error("Error storing user data:", error);
    throw error;
  });
}

// Get user data from Realtime Database
function getUserData(userId) {
  return database.ref('users/' + userId).once('value')
    .then((snapshot) => {
      const userData = snapshot.val();
      console.log("Retrieved user data:", userData);
      return userData;
    })
    .catch((error) => {
      console.error("Error retrieving user data:", error);
      throw error;
    });
}

// Reset password
function resetPassword(email) {
  return auth.sendPasswordResetEmail(email)
    .then(() => {
      console.log("Password reset email sent");
    })
    .catch((error) => {
      console.error("Error sending password reset email:", error);
      throw error;
    });
} 