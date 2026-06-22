import React, { useEffect, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  FlatList,
  StyleSheet,
  Text,
  View
} from "react-native";
import { apiClient } from "../../constants/axiosClient";

// Định nghĩa kiểu dữ liệu Cây từ Backend
interface Tree {
  id: string;
  name: string;
  status: "ALIVE" | "WITHERED"; // Trạng thái sống hoặc héo
  plantedAt: string;
}

export default function ForestScreen() {
  const [trees, setTrees] = useState<Tree[]>([]);
  const [loading, setLoading] = useState(false);

  // Gọi API lấy danh sách cây từ /api/forest
  const fetchForest = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get("/api/forest");
      setTrees(response.data || []);
    } catch (error: any) {
      const errorMsg =
        error.response?.data?.message ||
        "Không thể tải danh sách cây trong rừng!";
      Alert.alert("Lỗi hệ thống", errorMsg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchForest();
  }, []);

  // Render từng item cây trong danh sách lưới (Grid)
  const renderTreeItem = ({ item }: { item: Tree }) => {
    const isAlive = item.status === "ALIVE";
    return (
      <View
        style={[
          styles.treeCard,
          isAlive ? styles.aliveCard : styles.witheredCard,
        ]}
      >
        <Text style={styles.treeIcon}>{isAlive ? "🌲" : "🍂"}</Text>
        <Text style={styles.treeName}>{item.name}</Text>
        <Text style={styles.treeStatus}>
          {isAlive ? "Đang sống" : "Đã héo"}
        </Text>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>🌲 Khu Rừng Của Bạn</Text>
      <Text style={styles.subtitle}>
        Danh sách các cây bạn đã gieo trồng thật từ hệ thống
      </Text>

      {loading ? (
        <ActivityIndicator size="large" color="#4CAF50" style={{ flex: 1 }} />
      ) : (
        <FlatList
          data={trees}
          keyExtractor={(item) => item.id}
          renderItem={renderTreeItem}
          numColumns={2} // Hiển thị dạng lưới 2 cột
          contentContainerStyle={styles.listContainer}
          ListEmptyComponent={
            <Text style={styles.emptyText}>
              Rừng còn trống. Hãy bắt đầu tập trung để trồng cây nhé! 🎯
            </Text>
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 20, paddingTop: 40 },
  title: { fontSize: 22, fontWeight: "bold", color: "#2E7D32" },
  subtitle: { fontSize: 13, color: "gray", marginBottom: 20 },
  listContainer: { paddingBottom: 20, gap: 15 },
  treeCard: {
    flex: 1,
    margin: 8,
    padding: 15,
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
  },
  aliveCard: { backgroundColor: "#E8F5E9", borderColor: "#A5D6A7" },
  witheredCard: { backgroundColor: "#EFEBE9", borderColor: "#BCAAA4" },
  treeIcon: { fontSize: 40, marginBottom: 5 },
  treeName: { fontSize: 14, fontWeight: "bold", color: "#3E2723" },
  treeStatus: { fontSize: 12, color: "gray", marginTop: 2 },
  emptyText: {
    textAlign: "center",
    color: "gray",
    marginTop: 40,
    fontStyle: "italic",
  },
});
