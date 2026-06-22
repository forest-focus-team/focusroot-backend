import { useRouter } from "expo-router";
import { Button, StyleSheet, Text, View } from "react-native";

export default function RegisterScreen() {
  const router = useRouter();
  return (
    <View style={styles.container}>
      <Text style={styles.title}>📝 Màn Hình Đăng Ký (Wireframe 6)</Text>
      <Button title="Quay lại Đăng Nhập" onPress={() => router.back()} />
    </View>
  );
}
const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: "center", alignItems: "center" },
  title: { fontSize: 20, fontWeight: "bold", marginBottom: 10 },
});
