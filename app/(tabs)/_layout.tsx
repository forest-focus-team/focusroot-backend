import { Tabs } from "expo-router";

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{ tabBarActiveTintColor: "#2f95dc", headerShown: true }}
    >
      <Tabs.Screen name="index" options={{ title: "Trang Chủ" }} />
      <Tabs.Screen name="search" options={{ title: "Tìm Kiếm" }} />
      <Tabs.Screen name="notifications" options={{ title: "Thông Báo" }} />
      <Tabs.Screen name="profile" options={{ title: "Cá Nhân" }} />
    </Tabs>
  );
}
