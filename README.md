# 🌐 Full Complete WebView 4in1  

🌍 Languages: [English](README.md) | [Português Brasileiro](README.pt-BR.md)

A simple but powerful **Android WebView App** with offline handling, retry system, external link handling (WhatsApp, Facebook, YouTube, Maps, etc.), and a double-back press exit confirmation.  

This repository contains:  
- `MainActivity.java` → Main logic of the WebView app.  
- `activity_main.xml` → Layout with WebView + Offline Screen.  
- `strings.xml` → Centralized strings for easy customization.  
- `AndroidManifest.xml` → Permissions and deep linking.  



## 🚀 Features  
✅ Load any website (set in `strings.xml`)  
✅ Works with JavaScript and local storage  
✅ Handles offline mode with retry option  
✅ Detects and opens external links in native apps (WhatsApp, Facebook, YouTube, Maps, etc.)  
✅ Double-tap back button to exit the app  
✅ Fully customizable strings (translations ready)  
✅ Supports **deep linking**  



## 📂 Project Structure  

```
📦 Full_Complete_WebApp_WebView_4in1
 ┣ 📂 app/src/main/java/com/josemarpedro/Full_Complete_WebApp_WebView_4in1/MainActivity.java
 ┣ 📂 app/src/main/res/layout/activity_main.xml
 ┣ 📂 app/src/main/res/values/strings.xml
 ┣ 📂 app/src/main/AndroidManifest.xml
```



## ⚙️ How to Use  

1. Clone this repository:  
   ```bash
   git clone https://github.com/your-username/Full_Complete_WebApp_WebView_4in1.git
   cd Full_Complete_WebApp_WebView_4in1
   ```

2. Open the project in **Android Studio**.

3. Update your **package name** in:  
   - `MainActivity.java`  
   - `AndroidManifest.xml`  

4. Set your website URL in:  
   ```xml
   res/values/strings.xml
   <string name="site">https://your-website.com</string>

   Default: www.google.com
   ```

5. Run the app on an Android device or emulator.  



## 📜 Permissions  

Declared in `AndroidManifest.xml`:  
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```



## 🔗 Deep Linking  

You can configure your app to respond to custom links. Example:  
```xml
<data
   android:scheme="com.josemarpedro.Full_Complete_WebApp_WebView_4in1"
   android:host="auth" />
```

This allows URLs like:  
```
com.josemarpedro.Full_Complete_WebApp_WebView_4in1://auth
```



## 🛠 Tech Stack  
- **Java** (Android)  
- **WebView**  
- **ConstraintLayout**  



## 📸 Screens  

🔹 **Online Mode:** Displays the website.  
🔹 **Offline Mode:** Shows retry screen.  
🔹 **Double Back Exit:** Toast + confirmation.  



✍️ Author: **Josemar Pedro**  
📌 License: open-source