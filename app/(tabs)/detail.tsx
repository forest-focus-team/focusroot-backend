import { useRouter } from "expo-router";
import { Button, StyleSheet, Text, View } from "react-native";

export default function DetailScreen() {
  const router = useRouter();
  return (
    <View style={styles.container}>
      <Text style={styles.title}>ℹ️ Màn Hình Chi Tiết (Wireframe 7)</Text>
      <Button title="Quay lại Trang Chủ" onPress={() => router.back()} />
    </View>
  );
}
const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: "center", alignItems: "center" },
  title: { fontSize: 20, fontWeight: "bold", marginBottom: 10 },
});
