<?php
// Firebase Configuration
$firebaseConfig = [
    'apiKey' => "YOUR_API_KEY",
    'authDomain' => "your-project-id.firebaseapp.com",
    'databaseURL' => "https://your-project-id-default-rtdb.firebaseio.com",
    'projectId' => "your-project-id",
    'storageBucket' => "your-project-id.appspot.com",
    'messagingSenderId' => "YOUR_MESSAGING_SENDER_ID",
    'appId' => "YOUR_APP_ID",
    'measurementId' => "YOUR_MEASUREMENT_ID"
];

// Function to initialize Firebase JavaScript SDK
function getFirebaseJsConfig() {
    global $firebaseConfig;
    return json_encode($firebaseConfig);
}
?> 