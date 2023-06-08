//esp bên phảii
#include <WiFiManager.h>
#include <LiquidCrystal_I2C.h>
#include "FirebaseESP8266.h"
#define FIREBASE_HOST "https://hethongtuoicayesp32-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "X2rQGceaxsfP7dcRBEKBDwlLVN4Eom0ErgYOokSX"
#define WIFI_SSID "Thanhduy"
#define WIFI_PASSWORD "88888888"
LiquidCrystal_I2C lcd(0x27, 16, 2);  //SCL D1 SDA D2
bool connect;
FirebaseData firebaseData;
unsigned long checktime = 0;
const int maybom = D5;     // set chân máy bơm       D5
const int button = D7;     // set chan nut nhan         D7
const int cbmucnuoc = A0;  // set chân cảm biến mức nước A0
//-------------------------------------------------------------
int old_mucnuoc = -1;  // tạo biến lưu giá trị mực nước.
int dembom = 0;        // biến lưu trạng thái máy bơm khi không có WiFi
void setup() {
  WiFiManager wm;
  Serial.begin(9600);
  pinMode(button, INPUT);
  pinMode(maybom, OUTPUT);
  pinMode(cbmucnuoc, INPUT);
  digitalWrite(maybom, LOW);
  lcd.init();
  lcd.backlight();
  // Kết nối wifi.
  //wm.resetSettings();
  lcd.setCursor(3, 0);
  lcd.print("Connecting");
  lcd.setCursor(4, 1);
  lcd.print("to WiFi!");
  connect = wm.autoConnect(WIFI_SSID, WIFI_PASSWORD);
  if (!connect) {
    Serial.println("Failed to connect!");
  } else {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Connected to ");
    lcd.setCursor(0, 1);
    lcd.print(WiFi.SSID());
    Serial.println("Connected!");
    delay(5000);
    lcd.clear();
  }
  //ket noi Firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.setString(firebaseData, "/connect/SSID", WiFi.SSID());
}
void loop() {
  // doc cam bien muc nuoc
  int real_value = 0;
  for (int i = 0; i <= 1; i++) {  // đọc 2 lần
    real_value += analogRead(cbmucnuoc);
  }
  int value = real_value / 2;  // chia cho 2 để lấy giá trị chính xác nhất
  int new_mucnuoc = map(value, 270, 1023, 0, 100);
  new_mucnuoc = 100 - new_mucnuoc;  // chuyển từ khô về ẩm
  if (WiFi.status() == WL_CONNECTED) {
    Firebase.setInt(firebaseData, "/connect/espcontrol", 50);
    if (new_mucnuoc != old_mucnuoc) {  // kiểm tra xem độ ẩm mới khác cũ không, nếu khác thì ghi lên 5base
      Firebase.setInt(firebaseData, "/monitor/mucnuoc", new_mucnuoc);
      old_mucnuoc = new_mucnuoc;
      Serial.print(old_mucnuoc);
      Serial.println("%");
      lcd.setCursor(0, 0);
      lcd.print("Muc nuoc: ");
      lcd.print("       ");
      lcd.setCursor(10, 0);
      lcd.print(old_mucnuoc);
      lcd.print("%");
    }
    // nút nhấn bật or tắt máy bơm
    if (digitalRead(button) == LOW) {
      Firebase.getInt(firebaseData, "/control/maybom");
      int buttonpump = firebaseData.intData() == 1 ? HIGH : LOW;  // gán giá trị HIGH or LOW cho máy bơm
      if (buttonpump == HIGH) {                                   // máy bơm sẽ bật khi giá trị trên firebase != trạng thái máy bơm.
        Firebase.setInt(firebaseData, "/control/maybom", 0);
      } else {
        Firebase.setInt(firebaseData, "/control/maybom", 1);
      }
    }
    // Bật tắt máy bơm
    Firebase.getInt(firebaseData, "/control/maybom");
    int statusmaybom = firebaseData.intData();
    lcd.setCursor(0, 1);
    lcd.print("May bom: ");
    lcd.print(statusmaybom == 1 ? "Bat     " : "Tat     ");
    int new_pump = statusmaybom == 1 ? HIGH : LOW;  // gán giá trị HIGH or LOW cho máy bơm
    if (new_pump != digitalRead(maybom)) {          // máy bơm sẽ bật khi giá trị trên firebase != trạng thái máy bơm.
      digitalWrite(maybom, new_pump);
      checktime = millis();
    }
    // kiểm tra xem nước có lên không?
    if (millis() - checktime >= 15000 && digitalRead(maybom) == 1) {
      Firebase.getFloat(firebaseData, "/monitor/luuluong");
      if (firebaseData.floatData() <= 0.10) {
        // Lưu thời gian của lần đọc mới nhất
        checktime = millis();
        Firebase.setInt(firebaseData, "/control/maybom", 0);
      }
    }
    // kiểm tra xem đã hết nước chưa?
    if (new_mucnuoc <= 8) {
      Firebase.setInt(firebaseData, "/control/maybom", 0);
    }
  } else {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Disconnect!");
    lcd.setCursor(0, 1);
    lcd.print("Reconnect...");
    if (digitalRead(button) == LOW) {
      dembom += 1;
      delay(700);
    }
    if (dembom > 1) {
      dembom = 0;
    }
    if (dembom == 1) {
      digitalWrite(maybom, HIGH);
    } else {
      digitalWrite(maybom, LOW);
    }
    if (new_mucnuoc <= 8) {
      dembom = 0;
    }
    delay(500);
  }
  Serial.println("xong 1 lan loop");
}