//esp bên tráii
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <WiFiManager.h>
#include "FirebaseESP8266.h"

#define FIREBASE_HOST "https://hethongtuoicayesp32-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "X2rQGceaxsfP7dcRBEKBDwlLVN4Eom0ErgYOokSX"
#define WIFI_SSID "Thanhduy1"
#define WIFI_PASSWORD "88888888"
#define SCREEN_WIDTH 128  // OLED display width, in pixels
#define SCREEN_HEIGHT 64  // OLED display height, in pixels

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);
bool conncet;
FirebaseData firebaseData;
// Khai báo chân kết nối
const int cbdat = A0;         // chân cảm biến độ ẩm đất
const int flowMeterPin = 14;  // Chân kết nối với cảm biến lưu lượng  D5
// Khai báo biến
int old_doamdat = -1;             // tạo biến lưu giá trị độ ẩm đất.
unsigned int flowPulseCount = 0;  // Số lần nhận được xung từ cảm biến
unsigned long flowPulseTime = 0;  // Thời gian từ lần nhận được xung trước đến lần nhận được xung hiện tại
float flowRate = 0.0;             // Lưu lượng nước tính bằng lít/phút
float totalLiters = 0.0;          // Tổng số lít nước đã chảy qua cảm biến
float flowRate_old = -0.1;
float totalLiters_old = -0.1;
void setup() {
  WiFiManager wm;
  Serial.begin(9600);
  pinMode(cbdat, INPUT);
  pinMode(flowMeterPin, INPUT_PULLUP);

  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {  // Address 0x3D for 128x64
    Serial.println(F("SSD1306 allocation failed"));
    for (;;)
      ;
  }
  delay(500);
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(WHITE);
  display.setCursor(0, 0);
  display.println("Connecting to Wifi!");
  display.display();

  // Kết nối wifi.
  // wm.resetSettings();
  conncet = wm.autoConnect(WIFI_SSID, WIFI_PASSWORD);
  if (!conncet) {
    Serial.println("Fail!");
  } else {
    display.clearDisplay();
    display.setCursor(0, 0);
    display.println("Connected to Wifi!");
    display.setCursor(0, 8);
    display.println(WiFi.SSID());
    display.display();
    delay(3000);
    display.clearDisplay();
  }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.getFloat(firebaseData, "/monitor/luongnuoc");
  totalLiters = firebaseData.floatData();
}
void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    display.clearDisplay();
    display.setCursor(0, 0);
    display.print("WiFi: ");
    display.setCursor(32, 0);
    display.println(WiFi.SSID());
    display.setCursor(40, 10);
    display.print("Monitor");
    Firebase.setInt(firebaseData, "/connect/espmonitor", 50);
    // Đọc tín hiệu xung từ cảm biến
    flowPulseCount = pulseIn(flowMeterPin, HIGH);
    // Tính toán lưu lượng và tổng số lít nước
    flowPulseTime = millis() - flowPulseTime;
    flowRate = 1.0 / (flowPulseTime / (float)flowPulseCount) / 7.5;  // 7.5 là hệ số đổi từ xung/s sang lít/phút
    totalLiters += (flowRate / 60.0) * (flowPulseTime / 1000.0);
    // In kết quả ra Serial Monitor
    Serial.print("Luu luong: ");
    Serial.print(flowRate);
    Serial.print(" lit/phut, Tong so lit: ");
    Serial.println(totalLiters);
    flowPulseTime = millis();
    if (flowRate != flowRate_old) {
      char flowRateStr[8];
      dtostrf(flowRate, 6, 2, flowRateStr);
      Firebase.setFloat(firebaseData, "/monitor/luuluong", atof(flowRateStr));
      char totalLitersStr[16];
      dtostrf(totalLiters, 15, 2, totalLitersStr);
      Firebase.setFloat(firebaseData, "/monitor/luongnuoc", atof(totalLitersStr));
      flowRate_old = flowRate;
    }
    display.setCursor(0, 20);
    display.print("Luu luong:     ");
    display.setCursor(72, 20);
    display.println(flowRate);
    display.setCursor(0, 30);
    display.print("Luong nuoc:     ");
    display.setCursor(72, 30);
    display.println(totalLiters);
    // doc cam bien do am dat
    int real_value = 0;
    for (int i = 0; i <= 1; i++) {  // đọc 2 lần
      real_value += analogRead(cbdat);
    }
    int value = real_value / 2;  // chia cho 2 để lấy giá trị chính xác nhất
    int new_doamdat = map(value, 270, 1023, 0, 100);
    new_doamdat = 100 - new_doamdat;  // chuyển từ khô về ẩm
    // hiển thị độ ẩm đất lên màn hình oled.
    display.setCursor(0, 40);
    display.print("Do am dat: ");
    display.setCursor(72, 40);
    display.println(new_doamdat);
    if (new_doamdat != old_doamdat) {  // kiểm tra xem độ ẩm mới khác cũ không, nếu khác thì ghi lên 5base
      Firebase.setInt(firebaseData, "/monitor/doamdat", new_doamdat);
      old_doamdat = new_doamdat;
      Serial.print(old_doamdat);
      Serial.println("%");
    }
    // kiểm tra đã quá độ ẩm?
    Firebase.getInt(firebaseData, "/value/max");
    display.setCursor(0, 50);
    display.print("Value max: ");
    display.setCursor(72, 50);
    display.println(firebaseData.intData());
    if (firebaseData.intData() <= old_doamdat) {
      Firebase.setInt(firebaseData, "/control/maybom", 0);
    }
    display.display();
  } else {
    display.clearDisplay();
    display.setCursor(0, 0);
    display.print("Disconnected!");
    display.display();
  }
  Serial.println("xong 1 lan loop/trai");
}