#include <ESP8266WiFi.h>


const char* ssid = "TcpToSerialServer";
//const char* password = "";

WiFiServer wifiServer(80);

void setup() {
  Serial.begin(38400);
  Serial.println("Zaczynam");
  
  delay(3000);

  // w przypadku resetu programowego,zeby wifi tez zresetowalo
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);

  // if we want other than default 192.168.4.1
  IPAddress localIp(192,168,0,1);
  IPAddress gateway(192,168,0,1);
  IPAddress subnet(255,255,255,0);


  WiFi.softAPConfig(localIp,gateway,subnet);
  
  // WiFi.softAp(ssid,password,channel,hidden)
  WiFi.softAP(ssid);
  IPAddress myIP = WiFi.softAPIP();
  Serial.println("AP stworzone");
  Serial.print("Moje IP: ");
  Serial.print(myIP);
  Serial.println();

  wifiServer.begin();
}

void loop() {
  WiFiClient client = wifiServer.available();
  if (client) {
    while (client.connected()) {
      while (client.available() > 0) {
        char c = client.read();
        Serial.write(c);
        delay(10);
      }

    if (Serial.available()) {
      size_t len = Serial.available();
      uint8_t sbuf[len];
      Serial.readBytes(sbuf, len);
      client.write(sbuf, len);
      delay(1);
    }
    }
  }
  client.stop();
}
