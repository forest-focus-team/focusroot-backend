import { Href, useRouter } from "expo-router"; // Thêm Href ở đây
import { Button, StyleSheet, Text, View } from "react-native";

export default function ProfileScreen() {
  const router = useRouter();
  return (
    <View style={styles.container}>
      <Text style={styles.title}>👤 Màn Hình Hồ Sơ Cá Nhân (Wireframe 4)</Text>
      <Text style={styles.subtitle}>
        Quản lý thông tin tài khoản thành viên.
      </Text>
      <View style={{ marginTop: 20, width: "60%" }}>
        {/* Sửa lại dòng Button dưới đây */}
        <Button
          title="Đi tới Cài Đặt (Màn 8)"
          color="orange"
          onPress={() => router.push("/settings" as Href)}
        />
      </View>
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
