import { useRouter } from "expo-router";
import * as SecureStore from "expo-secure-store";
import React, { useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import { apiClient } from "../../constants/axiosClient";

export default function LoginScreen() {
  const router = useRouter();

  // Quản lý dữ liệu nhập và trạng thái Loading / Error (Yêu cầu Tuần 3)
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!email.trim() || !password.trim()) {
      Alert.alert("Thông báo", "Vui lòng nhập đầy đủ Email và Mật khẩu!");
      return;
    }

    setLoading(true);
    try {
      const response = await apiClient.post("/auth/login", {
        email: email.trim(),
        password: password,
      });

      const { accessToken, refreshToken } = response.data;

      // Lưu trữ token bảo mật
      await SecureStore.setItemAsync("accessToken", accessToken);
      await SecureStore.setItemAsync("refreshToken", refreshToken);

      Alert.alert("Thành công", "Đăng nhập thành công!");
    } catch (error: any) {
      const errorMsg =
        error.response?.data?.message ||
        "Không thể kết nối tới máy chủ. Vui lòng thử lại!";
      Alert.alert("Lỗi đăng nhập", errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>🔑 Màn Hình Đăng Nhập (Hi-Fi)</Text>

      {/* Form nhập liệu */}
      <View style={styles.formContainer}>
        <TextInput
          style={styles.input}
          placeholder="Nhập Email"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
          editable={!loading}
        />
        <TextInput
          style={styles.input}
          placeholder="Nhập Mật khẩu"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          autoCapitalize="none"
          editable={!loading}
        />
      </View>

      <View style={styles.buttonContainer}>
        {loading ? (
          <ActivityIndicator size="large" color="#0000ff" />
        ) : (
          <Button title="Đăng Nhập" color="#4CAF50" onPress={handleLogin} />
        )}

        {/* Thêm "as any" ở đây để sửa triệt để lỗi TypeScript */}
        <Button
          title="Chưa có tài khoản? Đăng ký ngay"
          onPress={() => router.push("/(auth)/register" as any)}
          disabled={loading}
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
    backgroundColor: "#fff",
  },
  title: { fontSize: 20, fontWeight: "bold", marginBottom: 20 },
  formContainer: { width: "80%", marginBottom: 20, gap: 12 },
  input: {
    width: "100%",
    height: 45,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    paddingHorizontal: 10,
    backgroundColor: "#fafafa",
  },
  buttonContainer: { gap: 15, width: "80%" },
});
