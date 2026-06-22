import { useRouter } from "expo-router";
import React, { useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  Platform,
  StyleSheet,
  Text,
  View,
} from "react-native";
import { apiClient } from "../../constants/axiosClient";

// Định nghĩa kiểu dữ liệu trả về từ API Stats của Backend
interface StatsSummary {
  totalFocusTime: number; // Tính bằng phút hoặc giây tùy backend
  totalTreesPlanted: number;
  currentStreak: number;
}

export default function HomeScreen() {
  const router = useRouter();

  // --- State của Màn hình Timer ---
  const [seconds, setSeconds] = useState(0);
  const [isActive, setIsActive] = useState(false);
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const timerRef = useRef<any>(null);

  // --- State mới của Màn hình Home (Tuần 5) ---
  const [stats, setStats] = useState<StatsSummary | null>(null);
  const [loadingStats, setLoadingStats] = useState(false);

  // 1. Tự động gọi API /api/stats/summary khi vừa vào màn hình Home (Yêu cầu Tuần 5)
  const fetchStatsSummary = async () => {
    setLoadingStats(true);
    try {
      const response = await apiClient.get("/api/stats/summary");
      setStats(response.data);
    } catch (error: any) {
      // Xử lý error cho API call (Yêu cầu chung Tuần 5)
      const errorMsg =
        error.response?.data?.message || "Không thể tải số liệu thống kê!";
      Alert.alert("Lỗi hệ thống", errorMsg);
    } finally {
      setLoadingStats(false);
    }
  };

  useEffect(() => {
    fetchStatsSummary();
  }, []);

  // Bộ đếm thời gian chạy thực tế của Timer
  useEffect(() => {
    if (isActive) {
      timerRef.current = setInterval(() => {
        setSeconds((prev) => prev + 1);
      }, 1000);
    } else if (!isActive && timerRef.current !== null) {
      clearInterval(timerRef.current);
    }
    return () => {
      if (timerRef.current !== null) {
        clearInterval(timerRef.current);
      }
    };
  }, [isActive]);

  const formatTime = (totalSeconds: number) => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  // 2. API POST /sessions/start
  const handleStartSession = async () => {
    setLoading(true);
    try {
      const response = await apiClient.post("/sessions/start", {
        duration: 1500,
        tags: ["Study"],
      });

      const { id } = response.data;
      setSessionId(id || "mock-session-id");
      setIsActive(true);
      Alert.alert("Thông báo", "Đã bắt đầu phiên tập trung mới!");
    } catch (error: any) {
      const errorMsg =
        error.response?.data?.message || "Không thể kết nối API Start Session!";
      Alert.alert("Lỗi", errorMsg);
    } finally {
      setLoading(false);
    }
  };

  // 3. API POST /sessions/end
  const handleEndSession = async () => {
    setLoading(true);
    try {
      await apiClient.post("/sessions/end", {
        sessionId: sessionId,
        actualDuration: seconds,
      });

      setIsActive(false);
      setSeconds(0);
      setSessionId(null);
      Alert.alert("Thành công", "Đã lưu lại phiên tập trung của bạn!");

      // Tập trung xong thì cập nhật lại số liệu thống kê mới nhất trên màn Home
      fetchStatsSummary();
    } catch (error: any) {
      const errorMsg =
        error.response?.data?.message || "Không thể kết nối API End Session!";
      Alert.alert("Lỗi", errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>🏠 Màn Hình Trang Chủ & Hẹn Giờ</Text>

      {/* --- KHU VỰC HIỂN THỊ STATS THẬT (MÀN HOME - TUẦN 5) --- */}
      <View style={styles.statsContainer}>
        <Text style={styles.sectionTitle}>📊 Thống kê của bạn</Text>
        {loadingStats ? (
          <ActivityIndicator size="small" color="#007AFF" />
        ) : stats ? (
          <View style={styles.statsGrid}>
            <Text style={styles.statsText}>
              ⏱️ Thời gian:{" "}
              <Text style={styles.boldText}>{stats.totalFocusTime} phút</Text>
            </Text>
            <Text style={styles.statsText}>
              🌲 Đã trồng:{" "}
              <Text style={styles.boldText}>{stats.totalTreesPlanted} cây</Text>
            </Text>
            <Text style={styles.statsText}>
              🔥 Chuỗi ngày:{" "}
              <Text style={styles.boldText}>{stats.currentStreak} ngày</Text>
            </Text>
          </View>
        ) : (
          <Text style={styles.errorText}>Chưa có dữ liệu thống kê.</Text>
        )}
      </View>

      {/* --- KHU VỰC ĐẾM GIỜ (MÀN TIMER) --- */}
      <View style={styles.timerContainer}>
        <Text style={styles.timerText}>{formatTime(seconds)}</Text>
        <Text style={styles.statusText}>
          {isActive ? "⏳ Đang tập trung..." : "🎯 Sẵn sàng bứt phá!"}
        </Text>
      </View>

      <View style={styles.buttonContainer}>
        {loading ? (
          <ActivityIndicator size="large" color="#4CAF50" />
        ) : !isActive ? (
          <Button
            title="🏁 Bắt đầu Tập Trung"
            color="#4CAF50"
            onPress={handleStartSession}
          />
        ) : (
          <Button
            title="🛑 Dừng / Kết thúc"
            color="#F44336"
            onPress={handleEndSession}
          />
        )}

        <View style={styles.divider} />

        <Button
          title="Đến Màn Hình Đăng Nhập"
          onPress={() => router.push("/(auth)/login" as any)}
          disabled={loading || loadingStats}
        />
        <Button
          title="Xem Chi Tiết Màn 7"
          color="green"
          onPress={() => router.push("/detail" as any)}
          disabled={loading || loadingStats}
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
  title: { fontSize: 20, fontWeight: "bold", marginBottom: 10 },
  sectionTitle: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 8,
    color: "#333",
  },
  statsContainer: {
    width: "80%",
    padding: 15,
    borderRadius: 12,
    backgroundColor: "#E3F2FD",
    marginVertical: 10,
  },
  statsGrid: { gap: 5 },
  statsText: { fontSize: 14, color: "#1565C0" },
  boldText: { fontWeight: "bold" },
  errorText: { fontSize: 13, color: "gray", fontStyle: "italic" },
  timerContainer: {
    alignItems: "center",
    marginVertical: 25,
    padding: 20,
    borderRadius: 15,
    backgroundColor: "#f5f5f5",
    width: "80%",
  },
  timerText: {
    fontSize: 48,
    fontWeight: "bold",
    color: "#333",
    fontFamily: Platform.OS === "ios" ? "Courier" : "monospace",
  },
  statusText: { fontSize: 16, marginTop: 10, color: "gray" },
  buttonContainer: { gap: 15, width: "80%" },
  divider: { height: 1, backgroundColor: "#ccc", marginVertical: 10 },
});
