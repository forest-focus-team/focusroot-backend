import { StyleSheet, Text, View } from "react-native";

export default function NotificationsScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>🔔 Màn Hình Thông Báo (Wireframe 3)</Text>
      <Text style={styles.subtitle}>
        Danh sách cập nhật trạng thái dự án, nhắc nhở thời gian.
      </Text>
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
  },
  title: { fontSize: 20, fontWeight: "bold", marginBottom: 10 },
  subtitle: { fontSize: 14, color: "gray" },
});
