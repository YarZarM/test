import * as Notifications from "expo-notifications";
import * as Device from "expo-device";
import axios from "axios";

export async function registerForPushNotifications() {
  if (!Device.isDevice) {
    return console.log("Must use physical device for push notifications");
  }

  const { status: existingStatus } = await Notifications.getPermissionsAsync();
  let finalStatus = existingStatus;

  if (existingStatus !== "granted") {
    const { status } = await Notifications.requestPermissionsAsync();
    finalStatus = status;
  }

  if (finalStatus !== "granted") {
    console.log("Permission not granted");
    return;
  }

  const token = (await Notifications.getExpoPushTokenAsync()).data;
  console.log("Expo Push Token:", token);

  // send token to backend
  await axios.post("https://migraine-junction.onrender.com//api/register-token", {
    user_id: "YZMM",
    fcm_token: token, // yes you can store expo token here
  });
}
